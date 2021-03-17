package com.loucaskreger.draggableui.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class BoundingBox {

	// Create another bounding box for the size of the screen, stored in the screen
	private Vec2i pos;
	private Vec2i prevPos;
	private Color4f color;
	@Nullable
	private DraggableWidget widget;
	private boolean visible;
	private int height;
	private int width;

	public BoundingBox(Vec2i pos, @Nullable DraggableWidget widget, int width, int height) {
		this.width = width;
		this.height = height;
		this.pos = pos;
		this.prevPos = null;
		this.visible = false;
		this.widget = widget;
		this.color = new Color4f(1f, 0f, 0f, 1f);
	}

	/**
	 * Returns a CollisionSide which contains the side of the collision and whether
	 * or not the objects did collide.
	 * 
	 * @param The other bounding box "this" is colliding with.
	 * @return The side in which the box is colliding on, otherwise none.
	 */
	public CollisionSide collidesWith(BoundingBox box, boolean simulate) {
		// Do the within collision inside this method
		BoundingBox interiorBounds = this.widget.getParent().interiorBounds;
		CollisionSide result = CollisionSide.NONE;
		// Difference between x and y pos of BoundingBoxes (Minkowski difference)
		int dx = (this.pos.x + this.width / 2) - (box.pos.x + box.width / 2);
		int dy = (this.pos.y + this.height / 2) - (box.pos.y + box.height / 2);
		// Width and Height of newly created box.
		int width = (this.width + box.width) / 2;
		int height = (this.height + box.height) / 2;
		int crossWidth = width * dy;
		int crossHeight = height * dx;

		// Something is wrong and the pos of the widget is actually the cursor position.
		// Create a second pos for the cursor and one for the top left of the widget
		if (this.prevPos != null) {
//			System.out.println("Pos X: " + this.pos.x);
//			System.out.println("Pos Y: " + this.pos.y);
//
//			System.out.println("prevPos X: " + this.prevPos.x);
//			System.out.println("prevPos Y: " + this.prevP.os.y);

		}
//
//		System.out.println("crossWidth: " + crossWidth);
//		System.out.println("crossHeight: " + crossHeight);

		if (Math.abs(dx) <= width && Math.abs(dy) <= height) {
			if (crossWidth > crossHeight) {
				if (crossWidth > (-crossHeight)) {
					// TOP
					result = CollisionSide.TOP;
					if (!simulate) {
						this.pos = new Vec2i(this.pos.x, box.pos.y + box.height);
					}
				} else {
					// RIGHT
					result = CollisionSide.RIGHT;
					if (!simulate) {
//						if (this.widget != null) {
//							int xDist = this.pos.x + box.pos.x;
//							if (xDist < this.width) {
//								this.pos = new Vec2i(0, box.pos.y + box.height);
//							}
//						}
						this.pos = new Vec2i(box.pos.x - this.width, this.pos.y);
					}
				}
			} else {
				if (crossWidth > -(crossHeight)) {
					// LEFT
					result = CollisionSide.LEFT;
					if (!simulate) {
//						if (this.widget != null) {
//							int xDist = interiorBounds.width - (box.pos.x + box.width);
//							if (xDist < this.width) {
//								this.pos = new Vec2i(interiorBounds.width - this.width, box.pos.y + box.height);
// 							}
//						}
						this.pos = new Vec2i(box.pos.x + box.width, this.pos.y);
					}
				} else {
					// BOTTOM
					result = CollisionSide.BOTTOM;
					if (!simulate) {
//						if (this.widget != null) {
//							int yDist = box.pos.y - interiorBounds.pos.y;
//							System.out.println(yDist);
//							if (yDist < this.height) {
//								this.pos = new Vec2i(box.pos.x - this.width, box.pos.y + box.height);
//								return result;
//							}
//						}
						this.pos = new Vec2i(this.pos.x, box.pos.y - this.height);
					}
				}
			}

		}
		return result;
	}

	/**
	 * Checks whether or not one bounding box is fully within another.
	 * 
	 * @param box(BoundingBox)  that "this" must stay within.
	 * @param simulate(boolean) determines whether or not "this" position should be
	 *                          updated as a collision.
	 * @return CollisionSide that corresponds to which side of "this" object has
	 *         collided with the outer edge of the parameter box.
	 */
	public CollisionSide isWithinBounds(BoundingBox box, boolean simulate) {
		CollisionSide result = CollisionSide.NONE;
		if (this.pos.x <= box.pos.x) {
			result = CollisionSide.LEFT;
			if (!simulate) {
				this.pos = new Vec2i(box.pos.x, this.pos.y);
			}
		}
		if (this.pos.x + this.width >= box.pos.x + box.width) {
			result = CollisionSide.RIGHT;
			if (!simulate) {
				this.pos = new Vec2i(box.pos.x + box.width - this.width - 1, this.pos.y);
			}
		}

		if (this.pos.y <= box.pos.y) {
//			if (result == CollisionSide.LEFT) {
//				result = CollisionSide.TOPLEFT;
//			} else if (result == CollisionSide.RIGHT) {
//				result = CollisionSide.TOPRIGHT;
//			} else {
			result = CollisionSide.TOP;
//			}
			if (!simulate) {
				this.pos = new Vec2i(this.pos.x, box.pos.y);
			}
		}

		if (this.pos.y + this.height >= box.pos.y + box.height) {
//			if (result == CollisionSide.LEFT) {
//				result = CollisionSide.BOTTOMLEFT;
//			} else if (result == CollisionSide.RIGHT) {
//				result = CollisionSide.BOTTOMRIGHT;
//			} else {
			result = CollisionSide.BOTTOM;
//			}
			if (!simulate) {
				this.pos = new Vec2i(this.pos.x, box.pos.y + box.height - this.height);
			}
		}

//		System.out.println(result);
		return result;
	}

	public boolean isValidPosition(Vec2i pos, List<DraggableWidget> widgets) {
		for (DraggableWidget widget : widgets) {
			if (widget.getBoundingBox() != this) {
				boolean collided = widget.collidesWith(this, true).collided();
				System.out.println("Valid: " + collided);
				return collided;
			}
		}
		return true;
	}

	public void drawBoundingBoxOutline() {
		if (this.visible) {
			IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

			// topLeft -> topRight
			this.drawLine(builder, this.getTopLeft(), this.getTopRight());
			// topRight -> bottomRight
			this.drawLine(builder, this.getTopRight(), this.getBottomRight());
			// bottomRight -> bottomLeft
			this.drawLine(builder, this.getBottomRight(), this.getBottomLeft());
			// bottomLeft -> topLeft
			this.drawLine(builder, this.getBottomLeft(), this.getTopLeft());
			buffer.finish(RenderType.LINES);
		}
	}

	private void drawLine(IVertexBuilder builder, Vec2i point1, Vec2i point2) {
		this.drawLine(builder, point1.x, point1.y, point2.x, point2.y);
	}

	private void drawLine(IVertexBuilder builder, int x1, int y1, int x2, int y2) {
		builder.pos(x1, y1, 0).color(this.color.red, this.color.green, this.color.blue, this.color.alpha).endVertex();
		builder.pos(x2, y2, 0).color(this.color.red, this.color.green, this.color.blue, this.color.alpha).endVertex();
	}

	public Vec2i getTopLeft() {
		return this.pos;
	}

	public Vec2i getTopRight() {
		return new Vec2i(this.pos.x + this.width, this.pos.y);
	}

	public Vec2i getBottomLeft() {
		return new Vec2i(this.pos.x, this.pos.y + this.height);
	}

	public Vec2i getBottomRight() {
		return new Vec2i(this.pos.x + this.width, this.pos.y + this.height);
	}

	public Vec2i getPos() {
		return this.pos;
	}

	public Vec2i getPrevPos() {
		return this.prevPos;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void move(Vec2i pos) {
		this.prevPos = this.pos;
		this.pos = pos;
	}

	public void setPos(Vec2i pos) {
		this.pos = pos;
	}

	public class Color4f {

		public float red;
		public float green;
		public float blue;
		public float alpha;

		public Color4f(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}

	public enum CollisionSide {
		NONE(false), LEFT(true), RIGHT(true), TOP(true), BOTTOM(true), TOPLEFT(true), BOTTOMLEFT(true), TOPRIGHT(true),
		BOTTOMRIGHT(true),;

		private final boolean collided;

		private CollisionSide(boolean collided) {
			this.collided = collided;
		}

		public boolean collided() {
			return this.collided;
		}
	}

}
