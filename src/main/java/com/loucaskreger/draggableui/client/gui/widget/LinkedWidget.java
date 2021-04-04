package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Vec2i;

public class LinkedWidget extends DraggableWidget {

	/**
	 * TODO:
	 * - Update the collisions so the linked widget also collides with unselected objects and static colliders.
	 */
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

	@Override
	public void mouseClicked(double mouseX, double mouseY) {
		super.mouseClicked(mouseX, mouseY);
		boolean isInBoundsX = mouseX > this.getBoundingBox().getPos().x
				&& mouseX < this.getBoundingBox().getPos().x + this.getBoundingBox().getWidth();
		boolean isInBoundsY = mouseY > this.getBoundingBox().getPos().y
				&& mouseY < this.getBoundingBox().getPos().y + this.getBoundingBox().getHeight();

		if (isInBoundsX && isInBoundsY) {
			this.updateOffset();
			this.linked = true;
		}
	}

	public void updateOffset() {
		this.offset = this.getBoundingBox().getPos().subtract(this.linkedWidget.get().getBoundingBox().getPos());
	}
	

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		if (this.linked && this.isSelected()) {
			this.linkedWidget.get().updateCursorPositions(mouseX, mouseY);
			this.linkedWidget.get().moveCursorBounds();
			this.linkedWidget.get().move();
//			Vec2i velocity = this.getCursorVelocity();
//			Vec2i finalPos = this.linkedWidget.get().getBoundingBox().getPos().add(velocity);
//			this.linkedWidget.get().resolveStaticCollisions();
////			this.linkedWidget.get().resolveObjectCollisions();
//			this.linkedWidget.get().getBoundingBox().setPos(finalPos);
		}
		super.mouseDragged(mouseX, mouseY);
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
