package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Vec2i;

public class LinkedWidget extends DraggableWidget {

	// Change to linkingWidget and linkableWidget to change all of mouseClicked when
	// a linkableWidget is linked to a linking widget.
	protected Supplier<DraggableWidget> linkedWidget;
	private Vec2i offset;
	protected boolean linked;

	public LinkedWidget(int x, int y, int width, int height, Supplier<DraggableWidget> linkedWidget, DraggableWidget thisWidget) {
		super(x, y, width, height);
		this.offset = null;
		this.linkedWidget = linkedWidget;
		this.linked = true;
	}

	public LinkedWidget(Vec2i pos, int width, int height, Supplier<DraggableWidget> linkedWidget, DraggableWidget thisWidget) {
		this(pos.x, pos.y, width, height, linkedWidget, thisWidget);
	}

	public void updateOffset() {
		System.out.println(this.linkedWidget.get().getBoundingBox().getPos());
		this.offset = this.getBoundingBox().getPos().subtract(this.linkedWidget.get().getBoundingBox().getPos());

	}

	@Override
	public void mouseClicked(double mouseX, double mouseY) {
		super.mouseClicked(mouseX, mouseY);
		boolean isInBoundsX = mouseX > this.getBoundingBox().getPos().x
				&& mouseX < this.getBoundingBox().getPos().x + this.getBoundingBox().getWidth();
		boolean isInBoundsY = mouseY > this.getBoundingBox().getPos().y
				&& mouseY < this.getBoundingBox().getPos().y + this.getBoundingBox().getHeight();

		if (isInBoundsX && isInBoundsY) {
			this.linkedWidget.get().setSelected(true);
			this.updateOffset();
			this.linked = true;
		}
	}

//	@Override
//	public void mouseDragged(int mouseX, int mouseY) {
//		super.mouseDragged(mouseX, mouseY);
//		if (this.linked) {
//			this.updateOffset();
//			if (this.offset != null) {
////				BoundingBox2D widgetBox = this.linkedWidget.get().getBoundingBox();
////				widgetBox.setPos(widgetBox.getPos().add(this.offset));
//			}
//		}
//	}

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
