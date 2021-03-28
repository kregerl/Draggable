package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Vec2i;

public class LinkedWidget extends DraggableWidget {

	protected Supplier<DraggableWidget> linkedWidget;
	private Vec2i offset;
	protected boolean linked;

	public LinkedWidget(int x, int y, int width, int height, Supplier<DraggableWidget> linkedWidget) {
		super(x, y, width, height);
		this.offset = null;
		this.linkedWidget = linkedWidget;
		this.linked = true;
	}

	public LinkedWidget(Vec2i pos, int width, int height, Supplier<DraggableWidget> linkedWidget) {
		this(pos.x, pos.y, width, height, linkedWidget);
	}

	public void updateOffset() {
		this.offset = this.getBoundingBox().getPos().subtract(this.linkedWidget.get().getBoundingBox().getPos());
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		if (this.linked) {
			this.updateOffset();
			if (this.offset != null) {
				this.linkedWidget.get().getBoundingBox()
						.setPos(this.linkedWidget.get().getBoundingBox().getPos().add(this.offset));
			}
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

}
