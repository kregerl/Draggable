package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;

public class LinkingWidget extends DraggableWidget {

	// Change to a list of suppliers so multiple widgets can be linked
	protected Supplier<? extends LinkableWidget> linkedWidget;
	protected boolean linked;
	private Vec2i offset;

	public LinkingWidget(int x, int y, int width, int height, Supplier<? extends LinkableWidget> linkedWidget) {
		super(x, y, width, height);
		this.offset = null;
		this.linkedWidget = linkedWidget;
		this.linked = true;
	}

	public LinkingWidget(Vec2i pos, int width, int height, Supplier<? extends LinkableWidget> linkedWidget) {
		this(pos.x, pos.y, width, height, linkedWidget);
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
		if (this.linkedWidget.get() instanceof LinkableWidget) {
			this.linked = ((LinkableWidget) this.linkedWidget.get()).isLinked();
		}
	}

	public void updateOffset() {
		this.offset = this.cursorPos.subtract(this.linkedWidget.get().getBoundingBox().getPos());
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		if (this.linked && this.isSelected()) {
			this.linkedWidget.get().updateCursorPositions(mouseX, mouseY);
			this.linkedWidget.get().moveCursorBounds(this.offset);
			BoundingBox2D linkedWidgetBox = this.linkedWidget.get().getBoundingBox();

			this.linkedWidget.get().getBoundingBox().setVelocity(this.getCursorVelocity());

			boolean resStatic = this.linkedWidget.get().resolveStaticCollisions();
			boolean resDynamic = this.linkedWidget.get().resolveObjectCollisions();

			List<BoundingBox2D> widgetBoundingBoxes = this.getWidgetBounds();

			if ((resStatic || resDynamic) && !linkedWidgetBox.collidesAny(widgetBoundingBoxes)
					&& !linkedWidgetBox.collidesAny(this.parentScreen.staticWidgets)
					&& !linkedWidgetBox.isWithinAny(widgetBoundingBoxes)) {

				linkedWidgetBox.setVelocity(this.cursorPos.subtract(this.offset).subtract(linkedWidgetBox.getPos()));
			}

			Vec2i finalPos = this.linkedWidget.get().getBoundingBox().getPos()
					.add(this.linkedWidget.get().getBoundingBox().getVelocity());

			BoundingBox2D simulatedBoundingBox = new BoundingBox2D(finalPos, linkedWidgetBox.getWidth(),
					linkedWidgetBox.getHeight());

			if (!this.linkedWidget.get().getCursorBoundingBox().equals(simulatedBoundingBox)
					&& !this.linkedWidget.get().getCursorBoundingBox().collidesAny(widgetBoundingBoxes)
					&& !this.linkedWidget.get().getCursorBoundingBox().collidesAny(this.parentScreen.staticWidgets)
					&& !this.linkedWidget.get().getCursorBoundingBox().isWithinAny(widgetBoundingBoxes)) {
				finalPos = this.cursorPos.subtract(this.offset);
			}

			this.linkedWidget.get().getBoundingBox().setPos(finalPos);
			this.linkedWidget.get().getBoundingBox().setVelocity(Vec2i.ZERO);

		}
	}

	@Override
	protected List<BoundingBox2D> getWidgetBounds() {
		List<BoundingBox2D> widgetBoundingBoxes = new ArrayList<BoundingBox2D>();
		this.parentScreen.widgets.forEach(i -> {
			if (i != this && (this.linked ? i != this.linkedWidget.get() : true))
				widgetBoundingBoxes.add(i.getBoundingBox());
		});
		return widgetBoundingBoxes;
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	protected Vec2i getLinkingOffset() {
		return this.offset;
	}

}
