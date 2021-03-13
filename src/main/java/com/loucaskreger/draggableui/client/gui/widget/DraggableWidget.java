package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.BoundingBox;
import com.loucaskreger.draggableui.util.BoundingBox.CollisionSide;
import com.loucaskreger.draggableui.util.Vec2i;

import net.minecraft.client.gui.screen.Screen;

public class DraggableWidget {

	private static int widgetCounter;

	/*
	 * The x and y components of the distance between the widget pos and the cursor
	 * pos.
	 */
	private Vec2i mouseDistance;

	/*
	 * Remove this and replace all the pos methods with the bounding box pos.
	 */
//	private Vec2i pos;
	// Last updated cursor position
	private Vec2i cursorPos;
	// Enabled by default
	private boolean enabled;
	// Disabled by default
	private boolean selected;
	private BoundingBox boundingBox;
	private String name;

	public int width;
	public int height;

	public DraggableWidget(int x, int y, int width, int height, boolean enabled, String name) {
		this.width = width;
		this.height = height;
		this.setEnabled(enabled);
		this.setSelected(false);
		this.cursorPos = null;
		this.mouseDistance = null;
		this.boundingBox = new BoundingBox(new Vec2i(x, y), width, height);
		this.setName(name);
		widgetCounter++;
	}

	public DraggableWidget(int width, int height) {
		this(0, 0, width, height, true, "widget " + widgetCounter);
	}

	public void mouseClicked(double mouseX, double mouseY) {

		this.cursorPos = new Vec2i(mouseX, mouseY);

		boolean isInBoundsX = this.cursorPos.x > this.boundingBox.getPos().x
				&& this.cursorPos.x < this.boundingBox.getPos().x + this.width;
		boolean isInBoundsY = this.cursorPos.y > this.boundingBox.getPos().y
				&& this.cursorPos.y < this.boundingBox.getPos().y + this.height;

		if (isInBoundsX && isInBoundsY) {
			this.setSelected(true);
			updatePosition((int) Math.round(mouseX), (int) Math.round(mouseY));
		}
	}

	public void updatePosition(int mouseX, int mouseY) {
		this.mouseDistance = new Vec2i((int) Math.round(mouseX) - this.boundingBox.getPos().x,
				(int) Math.round(mouseY) - this.boundingBox.getPos().y);
	}

	public void render(int mouseX, int mouseY, Screen screen) {
		if (this.enabled) {
			int x = this.boundingBox.getPos().x;
			int y = this.boundingBox.getPos().y;

			if (this.boundingBox.getPos().x <= 0) {
				x = 0;
				clearCursorPos();
			}
			if (this.boundingBox.getPos().y <= 0) {
				y = 0;
				clearCursorPos();
			}
			if (x + this.width >= screen.width) {
				x = screen.width - this.width;
				clearCursorPos();

			}
			if (y + this.height >= screen.height) {
				y = screen.height - this.height;
				clearCursorPos();
			}
			this.boundingBox.setPos(new Vec2i(x, y));
			this.updateBoundingBoxPos();

			if (this.boundingBox.isVisible()) {
				this.boundingBox.drawBoundingBoxOutline();
			}
		}

	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected()) {
			this.setPos((int) Math.round(mouseX) - this.mouseDistance.x,
					(int) Math.round(mouseY) - this.mouseDistance.y);
			this.updateBoundingBoxPos();

		}
	}

	public CollisionSide collidesWith(BoundingBox box) {
		return this.boundingBox.collidesWith(box);
	}

	public CollisionSide collidesWith(DraggableWidget widget) {
		return this.boundingBox.collidesWith(widget.getBoundingBox());
	}

	public void updateBoundingBoxPos() {
		this.boundingBox.setPos(this.getPos());
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPos(Vec2i pos) {
		this.boundingBox.setPos(pos);
	}

	public void setPos(int x, int y) {
		this.setPos(new Vec2i(x, y));
	}

	public Vec2i getPos() {
		return this.boundingBox.getPos();
	}

	public Vec2i getDistance() {
		return this.mouseDistance;
	}

	private void clearCursorPos() {
		this.cursorPos = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
