package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
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
	// Last updated cursor position
	private Vec2i cursorPos;
	private Vec2i prevCursorPos;
	private Vec2i initialPos;
	// Enabled by default
	private boolean enabled;
	// Disabled by default
	private boolean selected;
	private BoundingBox boundingBox;
	private String name;
	private DraggableScreen parentScreen;

	public int width;
	public int height;

	public DraggableWidget(int x, int y, int width, int height, boolean enabled, String name, DraggableScreen parent) {
		this.width = width;
		this.height = height;
		this.setEnabled(enabled);
		this.setSelected(false);
		this.cursorPos = null;
		this.prevCursorPos = null;
		this.mouseDistance = null;
		this.initialPos = new Vec2i(x, y);
		this.boundingBox = new BoundingBox(new Vec2i(x, y), this, width, height);
		this.setName(name);
		this.parentScreen = parent;
		widgetCounter++;
	}

	public DraggableWidget(int width, int height, DraggableScreen screen) {
		this(0, 0, width, height, true, "widget " + widgetCounter, screen);
	}

	public void mouseClicked(double mouseX, double mouseY) {
		if (this.cursorPos != null) {
			this.prevCursorPos = this.cursorPos;
		}
		this.cursorPos = new Vec2i(mouseX, mouseY);

		boolean isInBoundsX = this.cursorPos.x > this.boundingBox.getPos().x
				&& this.cursorPos.x < this.boundingBox.getPos().x + this.width;
		boolean isInBoundsY = this.cursorPos.y > this.boundingBox.getPos().y
				&& this.cursorPos.y < this.boundingBox.getPos().y + this.height;

		if (isInBoundsX && isInBoundsY) {
			this.setSelected(true);
			updatePosition((int) Math.round(mouseX), (int) Math.round(mouseY));
			this.initialPos = this.getPos();
		}
	}

	public void updatePosition(int mouseX, int mouseY) {
		this.mouseDistance = new Vec2i((int) Math.round(mouseX) - this.boundingBox.getPos().x,
				(int) Math.round(mouseY) - this.boundingBox.getPos().y);
	}

	public void render(int mouseX, int mouseY, Screen screen) {
		if (this.enabled) {
			if (this.boundingBox.isVisible()) {
				this.boundingBox.drawBoundingBoxOutline();
			}
		}

	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected()) {
			this.prevCursorPos = this.cursorPos;
			this.cursorPos = new Vec2i(mouseX, mouseY);

			if (this.cursorPos.x >= this.parentScreen.width || this.cursorPos.x <= 0
					|| this.cursorPos.y >= this.parentScreen.height || this.cursorPos.y <= 0) {
				this.setSelected(false);
				return;
			}
			this.setPos((int) Math.round(mouseX) - this.mouseDistance.x,
					(int) Math.round(mouseY) - this.mouseDistance.y);

			for (DraggableWidget widget : this.parentScreen.widgets) {
				if (this != widget) {
					CollisionSide collisionSide = this.collidesWith(widget, false);
					System.out.println("screen: " + collisionSide);

					CollisionSide isWithinScreen = this.boundingBox.isWithinBounds(this.parentScreen.interiorBounds,
							false);

					boolean isInBoundsX = this.cursorPos.x > this.boundingBox.getPos().x
							&& this.cursorPos.x < this.boundingBox.getPos().x + this.width;
					boolean isInBoundsY = this.cursorPos.y > this.boundingBox.getPos().y
							&& this.cursorPos.y < this.boundingBox.getPos().y + this.height;
					if (isInBoundsX && isInBoundsY) {
//						if (isWithinScreen != CollisionSide.NONE) {
//							this.updatePosition(mouseX, mouseY);
//						}
					}

//					System.out.println("screen: " + isWithinScreen);
//					BoundingBox interiorBounds = this.parentScreen.interiorBounds;
//					List<CollisionSide> screenCollisions = new ArrayList<CollisionSide>();
//					if (isWithinScreen == CollisionSide.TOPLEFT) {
//						screenCollisions.add(CollisionSide.LEFT);
//						screenCollisions.add(CollisionSide.TOP);
//					} else if (isWithinScreen == CollisionSide.TOPRIGHT) {
//						screenCollisions.add(CollisionSide.RIGHT);
//						screenCollisions.add(CollisionSide.TOP);
//					} else if (isWithinScreen == CollisionSide.BOTTOMLEFT) {
//						screenCollisions.add(CollisionSide.LEFT);
//						screenCollisions.add(CollisionSide.BOTTOM);
//					} else if (isWithinScreen == CollisionSide.BOTTOMRIGHT) {
//						screenCollisions.add(CollisionSide.RIGHT);
//						screenCollisions.add(CollisionSide.BOTTOM);
//					} else {
//						screenCollisions.add(isWithinScreen);
//					}
//
//					int setX = this.getPos().x;
//					int setY = this.getPos().y;
//
//					int x = 0;
//					int y = 0;
//					if (screenCollisions.contains(CollisionSide.BOTTOM)) {
//						int penY = interiorBounds.getPos().x + interiorBounds.getHeight()
//								- (this.getPos().y + this.height);
//						System.out.println("PenY : " + penY);
//						if (penY <= 0)
//							y += penY;
//					}
//
//					if (screenCollisions.contains(CollisionSide.TOP)) {
//						int penY = interiorBounds.getPos().y - this.getPos().y;
//						if (penY >= 0)
//							y += penY;
//					}
//
//					if (screenCollisions.contains(CollisionSide.LEFT)) {
//						int penX = this.getPos().x - interiorBounds.getPos().x;
//						if (penX <= 0)
//							x += -penX;
//					}
//
//					if (screenCollisions.contains(CollisionSide.RIGHT)) {
//						int penX = interiorBounds.getPos().x + interiorBounds.getWidth()
//								- (this.getPos().x + this.width);
//						if (penX <= 0)
//							x += penX;
//					}
//
//					System.out.println(setX + x);
//					System.out.println(setY + y);
//					this.setPos(new Vec2i(setX + x, setY + y));

//					boolean isInBoundsX = this.cursorPos.x > this.boundingBox.getPos().x
//							&& this.cursorPos.x < this.boundingBox.getPos().x + this.width;
//					boolean isInBoundsY = this.cursorPos.y > this.boundingBox.getPos().y
//							&& this.cursorPos.y < this.boundingBox.getPos().y + this.height;
//					if (isInBoundsX && isInBoundsY) {
//
//					}

				}
			}

		}
	}
	
	public void mouseReleased() {
		
	}

	public CollisionSide collidesWith(BoundingBox box, boolean simulate) {
		return this.boundingBox.collidesWith(box, simulate);
	}

	public CollisionSide collidesWith(DraggableWidget widget, boolean simulate) {
		return this.boundingBox.collidesWith(widget.getBoundingBox(), simulate);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DraggableScreen getParent() {
		return this.parentScreen;
	}

	public Vec2i getCursorPos() {
		return this.cursorPos;
	}

	public void setCursorPos(Vec2i cursorPos) {
		this.cursorPos = cursorPos;
	}

}
