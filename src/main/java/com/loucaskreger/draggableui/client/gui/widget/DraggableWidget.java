package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Vec2i;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DraggableWidget extends ForgeRegistryEntry<DraggableWidget> {

	private BoundingBox2D boundingBox;
	private BoundingBox2D cursorBoundingBox;
	private Vec2i mouseOffset;
	private Vec2i cursorPos;
	private Vec2i prevCursorPos;

	private boolean isEnabled;
	private boolean isSelected;
	private boolean shouldMoveToDefaultPos;
	@Nullable
	protected DraggableScreen parentScreen;

	@Nullable
	/**
	 * A default position to go to on init if this widget is enabled. Initialized to
	 * null as the initialX and initialY should be used to set positions via
	 * constructor. This position is meant to be reused to reset the position of a
	 * widget.
	 */
	protected Function<Screen, Vec2i> defaultPosition;

	public DraggableWidget(int initialX, int initialY, int width, int height) {
		this.boundingBox = new BoundingBox2D(new Vec2i(initialX, initialY), width, height);
		this.isEnabled = false;
		this.isSelected = false;
		this.shouldMoveToDefaultPos = true;
		this.cursorPos = null;
		this.prevCursorPos = null;
		this.parentScreen = null;
		this.defaultPosition = null;
		this.mouseOffset = Vec2i.ZERO;
	}

	public DraggableWidget(Vec2i pos, int width, int height) {
		this(pos.x, pos.y, width, height);
	}

	public void mouseClicked(double mouseX, double mouseY) {
		if (this.cursorPos != null) {
			this.prevCursorPos = this.cursorPos;
		}
		this.cursorPos = new Vec2i(mouseX, mouseY);

		if (this.cursorPos != null) {
			this.cursorBoundingBox = new BoundingBox2D(this.cursorPos, this.getBoundingBox().getWidth(),
					this.getBoundingBox().getHeight(), new Color4f(0.25f, 0.25f, 0.88f, 1f));
		}

		boolean isInBoundsX = mouseX > this.boundingBox.getPos().x
				&& mouseX < this.boundingBox.getPos().x + this.getBoundingBox().getWidth();
		boolean isInBoundsY = mouseY > this.boundingBox.getPos().y
				&& mouseY < this.boundingBox.getPos().y + this.getBoundingBox().getHeight();

		if (isInBoundsX && isInBoundsY) {
			this.setSelected(true);
			this.updatePosition((int) Math.round(mouseX), (int) Math.round(mouseY));
		}
	}

	public void init() {
		if (this.shouldMoveToDefaultPos) {
			this.moveToDefaultPosition();
		}
	}

	private void updatePosition(int mouseX, int mouseY) {
		this.mouseOffset = new Vec2i((int) Math.round(mouseX) - this.boundingBox.getPos().x,
				(int) Math.round(mouseY) - this.boundingBox.getPos().y);
	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected() && this.parentScreen != null) {
			if (this.cursorPos != null) {
				this.prevCursorPos = this.cursorPos;
			}
			this.cursorPos = new Vec2i(mouseX, mouseY);

			if (this.cursorBoundingBox != null) {
				this.cursorBoundingBox.setPos(this.cursorPos.subtract(this.mouseOffset));
				this.cursorBoundingBox.setVisible(true);
			}
			// Apply velocity based on mouse cursor position
			Vec2i vec = this.prevCursorPos.subtract(this.cursorPos);
			if (this.prevCursorPos != null) {
				if (this.prevCursorPos.x < mouseX) {
					this.getBoundingBox().addVelocity(new Vec2i(-vec.x, 0));
				} else if (this.prevCursorPos.x > mouseX) {
					this.getBoundingBox().addVelocity(new Vec2i(-vec.x, 0));
				}

				if (this.prevCursorPos.y < mouseY) {
					this.getBoundingBox().addVelocity(new Vec2i(0, -vec.y));
				} else if (this.prevCursorPos.y > mouseY) {
					this.getBoundingBox().addVelocity(new Vec2i(0, -vec.y));
				}

			}
			// Resolve the collisions with static objects
			boolean outerCol = this.resolveStaticCollisions();

			// Resolve the collisions with draggable objects
			boolean objCol = this.resolveObjectCollisions();

			List<BoundingBox2D> widgetBoundingBoxes = new ArrayList<BoundingBox2D>();
			// Add all bounding boxes to a list, not including this one.
			this.parentScreen.widgets.forEach(i -> {
				if (i != this)
					widgetBoundingBoxes.add(i.getBoundingBox());
			});

//			--------------------------------------------------------
			// Check for collisions against all objects
			if ((outerCol || objCol) && !this.cursorBoundingBox.collidesAny(widgetBoundingBoxes)
					&& !this.cursorBoundingBox.collidesAny(this.parentScreen.staticWidgets)
					&& !this.cursorBoundingBox.isWithinAny(widgetBoundingBoxes)) {
				this.getBoundingBox().setVelocity(
						this.cursorPos.subtract(this.mouseOffset).subtract(this.getBoundingBox().getPos()));
			}
			// Store the new bounding box pos for later.
			Vec2i finalPos = this.getBoundingBox().getPos().add(this.getBoundingBox().getVelocity());
			/*
			 * Simulate the collisions of a replica bounding box when the cursor's bounding
			 * box and the widget's bounding box are not in the same place. The cursor must
			 * be in a valid position.
			 */
			BoundingBox2D simulatedBoundingBox = new BoundingBox2D(finalPos, this.getBoundingBox().getWidth(),
					this.getBoundingBox().getHeight());
			if (!this.cursorBoundingBox.equals(simulatedBoundingBox)
					&& !this.cursorBoundingBox.collidesAny(widgetBoundingBoxes)
					&& !this.cursorBoundingBox.collidesAny(this.parentScreen.staticWidgets)
					&& !this.cursorBoundingBox.isWithinAny(widgetBoundingBoxes)) {
				finalPos = this.cursorPos.subtract(this.mouseOffset);
			}

			this.getBoundingBox().setPos(finalPos);
			this.getBoundingBox().setVelocity(Vec2i.ZERO);

		}
	}

	/*
	 * Only called inside mouseDragged. parent screen null check already happened
	 */
	private boolean resolveStaticCollisions() {
		boolean result = false;
		for (BoundingBox2D box : this.parentScreen.staticWidgets) {
			// Left and Right collision
			if ((this.getBoundingBox().getVelocity().x > 0 && this.getBoundingBox().collidesOnLeft(box))
					|| (this.getBoundingBox().getVelocity().x < 0 && this.getBoundingBox().collidesOnRight(box))) {
				this.getBoundingBox().setVelocity(new Vec2i(0, this.getBoundingBox().getVelocity().y));
				result = true;
			}
			// Top and Bottom collisions
			if ((this.getBoundingBox().getVelocity().y > 0 && this.getBoundingBox().collidesOnTop(box))
					|| (this.getBoundingBox().getVelocity().y < 0 && this.getBoundingBox().collidesOnBottom(box))) {
				this.getBoundingBox().setVelocity(new Vec2i(-this.getBoundingBox().getVelocity().x, 0));
				result = true;
			}
		}
		return result;
	}

	/*
	 * Only called inside mouseDragged. parent screen null check already happened
	 */
	private boolean resolveObjectCollisions() {
		boolean result = false;
		for (DraggableWidget widget : this.parentScreen.widgets) {
			if (widget == this || !widget.isEnabled())
				continue;
			// Left and Right collision
			if ((this.getBoundingBox().getVelocity().x > 0
					&& this.getBoundingBox().collidesOnLeft(widget.getBoundingBox()))
					|| (this.getBoundingBox().getVelocity().x < 0
							&& this.getBoundingBox().collidesOnRight(widget.getBoundingBox()))) {
				this.getBoundingBox().setVelocity(new Vec2i(0, this.getBoundingBox().getVelocity().y));
				result = true;
			}
			// Top and Bottom collisions
			if ((this.getBoundingBox().getVelocity().y > 0
					&& this.getBoundingBox().collidesOnTop(widget.getBoundingBox()))
					|| (this.getBoundingBox().getVelocity().y < 0
							&& this.getBoundingBox().collidesOnBottom(widget.getBoundingBox()))) {
				this.getBoundingBox().setVelocity(new Vec2i(-this.getBoundingBox().getVelocity().x, 0));
				result = true;
			}
		}
		return result;
	}

	public void mouseReleased() {
		this.getBoundingBox().setVelocity(Vec2i.ZERO);
		this.setSelected(false);
		this.cursorBoundingBox = null;
	}

	public void tick() {

	}

	// Use a consumer for rendering?
	// functions the same as defaultPosition
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		if (this.isEnabled()) {
			if (this.getBoundingBox().isVisible()) {
				this.getBoundingBox().drawBoundingBoxOutline();
				if (this.cursorBoundingBox != null && this.cursorBoundingBox.isVisible()) {
					this.cursorBoundingBox.drawBoundingBoxOutline();
				}
			}
		}

	}

	public void onClose() {
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean shouldMoveToDefaultPos() {
		return shouldMoveToDefaultPos;
	}

	public void setShouldMoveToDefaultPos(boolean shouldMoveToDefaultPos) {
		this.shouldMoveToDefaultPos = shouldMoveToDefaultPos;
	}

	public BoundingBox2D getBoundingBox() {
		return boundingBox;
	}

	public Vec2i getMouseOffset() {
		return mouseOffset;
	}

	public DraggableScreen getScreen() {
		return this.parentScreen;
	}

	public void setScreen(DraggableScreen screen) {
		this.parentScreen = screen;
	}

	protected void moveToDefaultPosition() {
		if (this.defaultPosition != null && this.isEnabled()) {
			this.getBoundingBox().setPos(this.defaultPosition.apply(this.parentScreen));
		}
	}

	public static CompoundNBT serializeNBT(DraggableWidget widget) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("id", widget.getRegistryName().toString());
		nbt.putInt("x", widget.getBoundingBox().getPos().x);
		nbt.putInt("y", widget.getBoundingBox().getPos().y);
		nbt.putInt("width", widget.getBoundingBox().getWidth());
		nbt.putInt("height", widget.getBoundingBox().getHeight());
		nbt.putBoolean("enabled", widget.isEnabled());

		return nbt;
	}

	public static DraggableWidget deserializeNBT(CompoundNBT nbt) {
		ResourceLocation id = new ResourceLocation(nbt.getString("id"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int width = nbt.getInt("width");
		int height = nbt.getInt("height");
		boolean enabled = nbt.getBoolean("enabled");
		for (Entry<ResourceLocation, DraggableWidget> entry : GameRegistry.findRegistry(DraggableWidget.class)
				.getEntries()) {
			if (entry.getKey().equals(id)) {
				DraggableWidget widget = entry.getValue();
				widget.getBoundingBox().setPos(new Vec2i(x, y));
				widget.getBoundingBox().setWidth(width);
				widget.getBoundingBox().setHeight(height);
				widget.setEnabled(enabled);
				return widget;
			}
		}
		return new DraggableWidget(new Vec2i(x, y), width, height);
	}

}
