package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;

import net.minecraft.util.ResourceLocation;

public class LinkingWidget extends DraggableWidget {

	// Change to a list of suppliers so multiple widgets can be linked
	protected Map<ResourceLocation, Supplier<? extends LinkableWidget>> linkedWidgets;
	private List<Supplier<? extends LinkableWidget>> widgets;
	protected boolean linked;

	public LinkingWidget(int x, int y, int width, int height, List<Supplier<? extends LinkableWidget>> linkedWidgets) {
		super(x, y, width, height);
		this.linkedWidgets = new HashMap<ResourceLocation, Supplier<? extends LinkableWidget>>();
		this.widgets = linkedWidgets;
		if (this.linkedWidgets.size() > 0) {
			this.linked = true;
		}
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

		if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox())) {
			this.updateOffset();
			this.linked = true;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.linked = this.areAnyLinked();
	}

	private boolean areAnyLinked() {
		boolean result = false;
		for (Supplier<? extends LinkableWidget> widget : this.linkedWidgets.values()) {
			if (widget.get().isLinked()) {
				result = true;
				break;
			}
		}
		return result;
	}

	// Each linkable widget need this method, it takes in a Vec2i which is the
	// cursorPos.
	public void updateOffset() {
		this.linkedWidgets.values().forEach(i -> i.get().updateOffset(this.cursorPos));
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		if (this.linked && this.isSelected()) {
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
//			this.linkedWidget.get().updateCursorPositions(mouseX, mouseY);
//			this.linkedWidget.get().moveCursorBounds(this.offset);
//			BoundingBox2D linkedWidgetBox = this.linkedWidget.get().getBoundingBox();
//
//			this.linkedWidget.get().getBoundingBox().setVelocity(this.getCursorVelocity());
//
//			boolean resStatic = this.linkedWidget.get().resolveStaticCollisions();
//			boolean resDynamic = this.linkedWidget.get().resolveObjectCollisions();
//
//			List<BoundingBox2D> widgetBoundingBoxes = this.getWidgetBounds();
//
//			if ((resStatic || resDynamic) && !linkedWidgetBox.collidesAny(widgetBoundingBoxes)
//					&& !linkedWidgetBox.collidesAny(this.parentScreen.staticWidgets)
//					&& !linkedWidgetBox.isWithinAny(widgetBoundingBoxes)) {
//
//				linkedWidgetBox.setVelocity(this.cursorPos.subtract(this.offset).subtract(linkedWidgetBox.getPos()));
//			}
//
//			Vec2i finalPos = this.linkedWidget.get().getBoundingBox().getPos()
//					.add(this.linkedWidget.get().getBoundingBox().getVelocity());
//
//			BoundingBox2D simulatedBoundingBox = new BoundingBox2D(finalPos, linkedWidgetBox.getWidth(),
//					linkedWidgetBox.getHeight());
//
//			if (!this.linkedWidget.get().getCursorBoundingBox().equals(simulatedBoundingBox)
//					&& !this.linkedWidget.get().getCursorBoundingBox().collidesAny(widgetBoundingBoxes)
//					&& !this.linkedWidget.get().getCursorBoundingBox().collidesAny(this.parentScreen.staticWidgets)
//					&& !this.linkedWidget.get().getCursorBoundingBox().isWithinAny(widgetBoundingBoxes)) {
//				finalPos = this.cursorPos.subtract(this.offset);
//			}
//
//			this.linkedWidget.get().getBoundingBox().setPos(finalPos);
//			this.linkedWidget.get().getBoundingBox().setVelocity(Vec2i.ZERO);

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

}
