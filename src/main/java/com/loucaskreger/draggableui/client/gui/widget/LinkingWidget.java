package com.loucaskreger.draggableui.client.gui.widget;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
import net.minecraft.util.ResourceLocation;

public class LinkingWidget extends DraggableWidget {

	private static final int LINKING_KEY = GLFW_KEY_LEFT_SHIFT;
	// Change to a list of suppliers so multiple widgets can be linked
	protected Map<ResourceLocation, Supplier<? extends LinkableWidget>> linkedWidgets;
	// This list should not be used for ANYTHING, it is only here to be used in init to set the contents of the map
	private List<Supplier<? extends LinkableWidget>> widgets;

	protected boolean linked;
	private boolean isLinkingModeKeyPressed;
	private boolean linkingMode;

	public LinkingWidget(int x, int y, int width, int height, List<Supplier<? extends LinkableWidget>> linkedWidgets) {
		super(x, y, width, height);
		this.linkedWidgets = new HashMap<ResourceLocation, Supplier<? extends LinkableWidget>>();
		this.widgets = linkedWidgets;
		if (linkedWidgets.size() > 0) {
			this.linked = true;
		}
		this.linkingMode = false;
		this.isLinkingModeKeyPressed = false;
	}

	public LinkingWidget(Vec2i pos, int width, int height, List<Supplier<? extends LinkableWidget>> linkedWidget) {
		this(pos.x, pos.y, width, height, linkedWidget);
	}

	@Override
	public void init() {
		super.init();
		this.widgets.forEach(i -> {
			this.linkedWidgets.put(i.get().getRegistryName(), i);
		});
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY) {
		super.mouseClicked(mouseX, mouseY);

		if (this.isLinkingModeKeyPressed && this.isSelected()) {
			this.linkingMode = true;
			this.setSelected(false);
		}

		if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox())) {
			this.updateOffset();
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == LINKING_KEY) {
			this.isLinkingModeKeyPressed = true;
		} else {
			this.isLinkingModeKeyPressed = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.linked = this.areAnyLinked();
		Iterator<Supplier<? extends LinkableWidget >> it = this.linkedWidgets.values().iterator();
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
		// If shift is held draw line for linking widgets otherwise move the linked widgets
		if (this.linkingMode) {
			this.updateCursorPositions(mouseX, mouseY);
		} else if (this.linked && this.isSelected()) {
			for (Supplier<? extends LinkableWidget> widget : this.linkedWidgets.values()) {
				widget.get().updateCursorPositions(mouseX, mouseY);
				widget.get().moveCursorBounds(widget.get().getOffset());
				BoundingBox2D linkedWidgetBox = widget.get().getBoundingBox();

				widget.get().getBoundingBox().setVelocity(this.getCursorVelocity());

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

				BoundingBox2D simulatedBoundingBox = new BoundingBox2D(finalPos, linkedWidgetBox.getWidth(),
						linkedWidgetBox.getHeight());

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
		// Draw line from center to cursor
		if (this.linkingMode) {
			RenderSystem.pushMatrix();
			IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

			Util.drawLine(builder, new Color4f(0.0f, 0.85f, 0.25f, 1.0f), this.getCenterPos(), this.cursorPos);
			buffer.finish(RenderType.LINES);
			RenderSystem.popMatrix();
		}
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

}
