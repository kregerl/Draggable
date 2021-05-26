package com.loucaskreger.draggableui.client.gui.widget;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;

public class LinkingWidget extends DraggableWidget {

	private static final Color4f HOVER_COLOR = new Color4f(0.0f, 0.5f, 0.5f, 1.0f);
	protected Map<ResourceLocation, Supplier<? extends LinkableWidget>> linkedWidgets;
	protected boolean hasDefaultLinks;
	/**
	 * This list stores all the linkable widgets enabled on the screen.
	 */
	protected List<Supplier<? extends LinkableWidget>> defaultLinks;

	protected boolean linked;
	private boolean drawHoverLines;
	private boolean isLinkingModeKeyPressed;
	private boolean linkingMode;

	public LinkingWidget(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.linkedWidgets = new HashMap<ResourceLocation, Supplier<? extends LinkableWidget>>();
		this.defaultLinks = new ArrayList<Supplier<? extends LinkableWidget>>();
		this.hasDefaultLinks = true;
		this.linked = false;
		this.linkingMode = false;
		this.isLinkingModeKeyPressed = false;
		this.drawHoverLines = false;
	}

	public LinkingWidget(Vec2i pos, int width, int height) {
		this(pos.x, pos.y, width, height);
	}

	@Override
	public void init() {
		super.init();
		// If there are any default widgets, link them and update their status
		if (this.linkedWidgets.isEmpty() && this.hasDefaultLinks) {
			for (LinkableWidget widget : this.defaultLinks.stream().map(i -> i.get()).collect(Collectors.toList())) {
				this.linkedWidgets.put(widget.getRegistryName(), () -> widget);
				this.linked = true;
				widget.setLinked(true);
			}
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int scrollDelta) {
		super.mouseClicked(mouseX, mouseY, scrollDelta);

		if (this.isLinkingModeKeyPressed && this.isSelected()) {
			this.linkingMode = true;
			this.setSelected(false);
		}

		if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox())) {
			this.linkedWidgets.forEach((loc, wid) -> {
				if (wid.get().shouldMoveToDefaultPos())
					wid.get().setShouldMoveToDefaultPos(false);
			});
			this.updateOffset();
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == GLFW_KEY_LEFT_SHIFT) {
			this.isLinkingModeKeyPressed = true;
		} else {
			this.isLinkingModeKeyPressed = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.linked = this.areAnyLinked();
		Iterator<Supplier<? extends LinkableWidget>> it = this.linkedWidgets.values().iterator();
		while (it.hasNext()) {
			LinkableWidget widget = it.next().get();
			if (!widget.isLinked()) {
				it.remove();
			}
		}

	}

	private boolean areAnyLinked() {
		boolean result = false;
		for (Supplier<? extends LinkableWidget> widget : this.linkedWidgets.values()) {
			if (widget.get().isLinked() && widget.get().isEnabled()) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void updateOffset() {
		this.linkedWidgets.values().forEach(i -> i.get().updateOffset(this.cursorPos));
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		// If shift is held draw line for linking widgets otherwise move the linked
		// widgets
		if (this.linkingMode) {
			this.updateCursorPositions(mouseX, mouseY);
		} else if (this.linked && this.isSelected()) {
			for (Supplier<? extends LinkableWidget> widget : this.linkedWidgets.values()) {
				widget.get().updateCursorPositions(mouseX, mouseY);
				widget.get().moveCursorBounds(widget.get().getOffset());
				BoundingBox2D linkedWidgetBox = widget.get().getBoundingBox();

				widget.get().getBoundingBox().setVelocity(this.getCursorVelocity());

				// If this is colliding with anything, move linked widgets with same velocity
				boolean resStatic = widget.get().resolveStaticCollisions();
				boolean resDynamic = widget.get().resolveObjectCollisions();

				List<BoundingBox2D> widgetBoundingBoxes = this.getWidgetBounds();

				if ((resStatic || resDynamic) && !linkedWidgetBox.collidesAny(widgetBoundingBoxes)
						&& !linkedWidgetBox.collidesAny(widget.get().getParentScreen().staticWidgets)
						&& !linkedWidgetBox.isWithinAny(widgetBoundingBoxes)) {

					linkedWidgetBox.setVelocity(
							this.cursorPos.subtract(widget.get().getOffset()).subtract(linkedWidgetBox.getPos()));
				}

				Vec2i finalPos = widget.get().getBoundingBox().getPos()
						.add(widget.get().getBoundingBox().getVelocity());

				// Simulate collisions at new pos
				BoundingBox2D simulatedBoundingBox = new BoundingBox2D(finalPos, linkedWidgetBox.getWidth(),
						linkedWidgetBox.getHeight());

				// If simulated bounding box fits, place it there
				if (!widget.get().getCursorBoundingBox().equals(simulatedBoundingBox)
						&& !widget.get().getCursorBoundingBox().collidesAny(widgetBoundingBoxes)
						&& !widget.get().getCursorBoundingBox()
								.collidesAny(widget.get().getParentScreen().staticWidgets)
						&& !widget.get().getCursorBoundingBox().isWithinAny(widgetBoundingBoxes)) {
					finalPos = this.cursorPos.subtract(widget.get().getOffset());
				}

				widget.get().getBoundingBox().setPos(finalPos);
				widget.get().getBoundingBox().setVelocity(Vec2i.ZERO);
			}

		}
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);
		if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox())) {
			this.getBoundingBox().setColor(HOVER_COLOR);
			this.drawHoverLines = true;
		} else {
			this.getBoundingBox().setColor(BoundingBox2D.DEFAULT_COLOR);
			this.drawHoverLines = false;
		}
	}

	@Override
	protected List<BoundingBox2D> getWidgetBounds() {
		List<BoundingBox2D> widgetBoundingBoxes = new ArrayList<BoundingBox2D>();
		List<LinkableWidget> linkableWidgets = new ArrayList<LinkableWidget>();
		this.linkedWidgets.values().forEach(i -> linkableWidgets.add(i.get()));
		this.parentScreen.widgets.forEach(i -> {
			if (i != this && (this.linked ? !linkableWidgets.contains(i) : true))
				widgetBoundingBoxes.add(i.getBoundingBox());
		});
		return widgetBoundingBoxes;
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		RenderSystem.pushMatrix();
		IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

		// Draw lines from this widget to all linked ones
		if (this.drawHoverLines) {
			for (LinkableWidget widget : this.linkedWidgets.values().stream().map(i -> i.get())
					.collect(Collectors.toList())) {
				if (widget.isEnabled())
					Util.drawLine(builder, HOVER_COLOR, this.getCenterPos(), widget.getCenterPos());
			}
		}

		// Draw line to cursor
		if (this.linkingMode) {
			Util.drawLine(builder, new Color4f(0.0f, 0.85f, 0.25f, 1.0f), this.getCenterPos(), this.cursorPos);
		}

		buffer.finish(RenderType.LINES);
		RenderSystem.popMatrix();
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		super.mouseReleased(mouseX, mouseY, scrollDelta);
		// Upon mouse release link the widgets
		if (this.linkingMode && this.isLinkingModeKeyPressed && this.parentScreen != null) {
			for (DraggableWidget widget : this.parentScreen.widgets) {
				if (widget instanceof LinkableWidget && widget.isEnabled()
						&& Util.isWithinBounds(new Vec2i(mouseX, mouseY), widget.getBoundingBox())) {
					LinkableWidget lWidget = ((LinkableWidget) widget);
					lWidget.setLinked(true);
					this.linkedWidgets.put(lWidget.getRegistryName(), () -> lWidget);
				}
			}
		}
		this.linkingMode = false;
		this.isLinkingModeKeyPressed = false;
	}

	/**
	 * Serialize and Deserialize any object data below
	 */

	private static final String LINKED_KEY = "linked";
	private static final String LIST_KEY = "list";
	private static final String LOCATION_KEY = "location";

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();
		nbt.putBoolean(LINKED_KEY, this.linked);
		ListNBT listNBT = new ListNBT();
		for (ResourceLocation loc : this.linkedWidgets.keySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(LOCATION_KEY, loc.toString());
			listNBT.add(tag);
		}
		nbt.put(LIST_KEY, listNBT);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		this.linked = nbt.getBoolean(LINKED_KEY);
		ListNBT listNBT = (ListNBT) nbt.get(LIST_KEY);
		if (listNBT.isEmpty()) {
			this.hasDefaultLinks = false;
		}
		for (int i = 0; i < listNBT.size(); i++) {
			ResourceLocation loc = new ResourceLocation(listNBT.getCompound(i).getString(LOCATION_KEY));
			List<LinkableWidget> wids = this.defaultLinks.stream().map(wid -> wid.get()).collect(Collectors.toList());

			for (DraggableWidget widget : wids) {
				ResourceLocation resLoc = widget.getRegistryName();
				if (resLoc.equals(loc) && !this.linkedWidgets.containsKey(loc)) {
					this.linkedWidgets.put(loc, () -> (LinkableWidget) widget);
				}
			}
		}

	}
}
