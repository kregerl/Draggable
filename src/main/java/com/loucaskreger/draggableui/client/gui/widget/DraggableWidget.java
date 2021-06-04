package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Canvas;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;
import com.loucaskreger.draggableui.util.WidgetType;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DraggableWidget extends ForgeRegistryEntry<DraggableWidget> implements INBTSerializable<CompoundNBT> {

//	private UUID uuid;

	private BoundingBox2D boundingBox;
	private BoundingBox2D cursorBoundingBox;
	protected Vec2i mouseOffset;
	protected Vec2i cursorPos;
	protected Vec2i prevCursorPos;
	protected Canvas canvas;
	protected WidgetType type;

	private boolean isEnabled;
	private boolean isSelected;
	private boolean shouldMoveToDefaultPos;
	protected boolean isSerilizable;
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
		this.isSerilizable = true;
		this.shouldMoveToDefaultPos = false;
		this.cursorPos = null;
		this.prevCursorPos = null;
		this.parentScreen = null;
		this.defaultPosition = null;
		this.mouseOffset = Vec2i.ZERO;
		this.type = WidgetType.ANY;
		this.canvas = new Canvas(this.getBoundingBox());
//		this.uuid = UUID.randomUUID();
	}

	public DraggableWidget(Vec2i pos, int width, int height) {
		this(pos.x, pos.y, width, height);
	}

	public DraggableWidget(BoundingBox2D box) {
		this(box.getPos(), box.getWidth(), box.getHeight());
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		this.updateCursorPositions(mouseX, mouseY);

		if (this.cursorPos != null) {
			this.cursorBoundingBox = new BoundingBox2D(this.cursorPos, this.getBoundingBox().getWidth(),
					this.getBoundingBox().getHeight(), new Color4f(0.25f, 0.25f, 0.88f, 1f));
		}

		if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox())) {
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

	protected void updateCursorPositions(double mouseX, double mouseY) {
		if (this.cursorPos != null) {
			this.prevCursorPos = this.cursorPos;
		}
		this.cursorPos = new Vec2i(mouseX, mouseY);
	}

	protected void moveCursorBounds(Vec2i offset) {
		if (this.cursorBoundingBox != null) {
			this.cursorBoundingBox.setPos(this.cursorPos.subtract(offset));
			this.cursorBoundingBox.setVisible(true);
		}
	}

	protected Vec2i getCursorVelocity() {
		if (this.prevCursorPos != null && this.cursorPos != null) {
			Vec2i vec = this.prevCursorPos.subtract(this.cursorPos);
			Vec2i result = Vec2i.ZERO;
			if (this.prevCursorPos != null) {
				if (this.prevCursorPos.x < this.cursorPos.x) {
					result = result.add(new Vec2i(-vec.x, 0));
				} else if (this.prevCursorPos.x > this.cursorPos.x) {
					result = result.add(new Vec2i(-vec.x, 0));
				}

				if (this.prevCursorPos.y < this.cursorPos.y) {
					result = result.add(new Vec2i(0, -vec.y));
				} else if (this.prevCursorPos.y > this.cursorPos.y) {
					result = result.add(new Vec2i(0, -vec.y));
				}

			}
			return result;
		}
		return Vec2i.ZERO;
	}

	protected List<BoundingBox2D> getWidgetBounds() {
		List<BoundingBox2D> widgetBoundingBoxes = new ArrayList<BoundingBox2D>();
		this.parentScreen.widgets.forEach(i -> {
			if (i != this)
				widgetBoundingBoxes.add(i.getBoundingBox());
		});
		return widgetBoundingBoxes;
	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected() && this.parentScreen != null) {
			this.updateCursorPositions(mouseX, mouseY);
			this.moveCursorBounds(this.mouseOffset);
			// Apply velocity based on mouse movement
			Vec2i velocity = this.getCursorVelocity();
			this.getBoundingBox().addVelocity(velocity);

			// Resolve the collisions with static objects
			boolean outerCol = this.resolveStaticCollisions();

			// Resolve the collisions with draggable objects
			boolean objCol = this.resolveObjectCollisions();

			List<BoundingBox2D> widgetBoundingBoxes = this.getWidgetBounds();

			// --------------------------------------------------------
			// Check for collisions against all objects
			if ((outerCol || objCol) && !this.cursorBoundingBox.collidesAny(widgetBoundingBoxes)
					&& !this.cursorBoundingBox.collidesAny(this.parentScreen.staticWidgets.stream()
							.map(sw -> sw.getBoundingBox()).collect(Collectors.toList()))
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
					&& !this.cursorBoundingBox.collidesAny(this.parentScreen.staticWidgets.stream()
							.map(sw -> sw.getBoundingBox()).collect(Collectors.toList()))
					&& !this.cursorBoundingBox.isWithinAny(widgetBoundingBoxes)) {
				finalPos = this.cursorPos.subtract(this.mouseOffset);
			}

			this.getBoundingBox().setPos(finalPos);
			this.getBoundingBox().setVelocity(Vec2i.ZERO);
		}
	}

	public void mouseMoved(double mouseX, double mouseY) {
		// Empty
	}

	/*
	 * Only called inside mouseDragged. parent screen null check already happened
	 */
	protected boolean resolveStaticCollisions() {
		boolean result = false;
		for (BoundingBox2D box : this.parentScreen.staticWidgets.stream().map(sw -> sw.getBoundingBox())
				.collect(Collectors.toList())) {
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
	protected boolean resolveObjectCollisions() {
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

	public void mouseReleased(double mouseX, double mouseY, int scrollDelta) {

		this.getBoundingBox().setVelocity(Vec2i.ZERO);
		this.setSelected(false);
		this.cursorBoundingBox = null;
		this.cursorPos = null;
		this.prevCursorPos = null;
	}

	/**
	 * Only ticks while the draggable screen is open. Use {@link ITickableWidget} if
	 * widget needs to tick all the time.
	 */
	public void tick() {

		if (this.shouldMoveToDefaultPos) {
			this.moveToDefaultPosition();
		}
	}

	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		if (this.isEnabled()) {
			this.canvas.render(screen);
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

	public void keyPressed(int keyCode, int scanCode, int modifiers) {

	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean shouldMoveToDefaultPos() {
		return this.shouldMoveToDefaultPos;
	}

	public void setShouldMoveToDefaultPos(boolean shouldMoveToDefaultPos) {
		this.shouldMoveToDefaultPos = shouldMoveToDefaultPos;
	}

	public BoundingBox2D getBoundingBox() {
		return this.boundingBox;
	}

	public Vec2i getCenterPos() {
		return new Vec2i(this.getBoundingBox().getPos().x + this.getBoundingBox().getWidth() / 2,
				this.getBoundingBox().getPos().y + this.getBoundingBox().getHeight() / 2);
	}

	public Vec2i getMouseOffset() {
		return this.mouseOffset;
	}

	public DraggableScreen getParentScreen() {
		return this.parentScreen;
	}

	public void setScreen(DraggableScreen screen) {
		this.parentScreen = screen;
	}

	public BoundingBox2D getCursorBoundingBox() {
		return this.cursorBoundingBox;
	}

	public void setMouseOffset(Vec2i offset) {
		this.mouseOffset = offset;
	}

	protected void moveToDefaultPosition() {
		if (this.defaultPosition != null && this.isEnabled() && this.parentScreen != null) {
			this.getBoundingBox().setPos(this.defaultPosition.apply(this.parentScreen));
			this.shouldMoveToDefaultPos = false;
		}
	}

//	public UUID getUUID() {
//		return this.uuid;
//	}

	private static final String ID_KEY = "id";
	private static final String UUID_KEY = "uuid";
	private static final String BOX_KEY = "boundingbox";
	private static final String ENABLED_KEY = "enabled";

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(ID_KEY, this.getRegistryName().toString());
//		nbt.putString(UUID_KEY, this.uuid.toString());
		nbt.put(BOX_KEY, this.getBoundingBox().serializeNBT());
		nbt.putBoolean(ENABLED_KEY, this.isEnabled());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
//		this.uuid = UUID.fromString(nbt.getString(UUID_KEY));
		BoundingBox2D box = BoundingBox2D.read(nbt.getCompound(BOX_KEY));
		this.getBoundingBox().setPos(box.getPos());
		this.getBoundingBox().setWidth(box.getWidth());
		this.getBoundingBox().setHeight(box.getHeight());
		this.setEnabled(nbt.getBoolean(ENABLED_KEY));
	}

	public boolean isSerilizable() {
		return isSerilizable;
	}

//	@Override
//	public Memento<CompoundNBT> saveState() {
//		return null;
//	}
//
//	@Override
//	public void restoreState(Memento<CompoundNBT> memento) {
//		// TODO Auto-generated method stub
//
//	}

//	@Override
//	public Memento<CompoundNBT> saveState() {
//		return new Memento<CompoundNBT>(this.serializeNBT());
//	}
//
//	@Override
//	public void restoreState(Memento<CompoundNBT> memento) {
//		this.deserializeNBT(memento.getState());
//	}

}
