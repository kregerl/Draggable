package com.loucaskreger.draggableui.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class BoundingBox {

	private Vec2i pos;
	private Color4f color;
	private boolean visible;
	private int height;
	private int width;

	public BoundingBox(Vec2i pos, int width, int height) {
		this.width = width;
		this.height = height;
		this.pos = pos;
		this.visible = false;
		this.color = new Color4f(1f, 0f, 0f, 1f);
	}

	/**
	 * 
	 * @param box
	 * @return The side in which the box is colliding on, or none.
	 */
	public CollisionSide collidesWith(BoundingBox box) {
		CollisionSide result = CollisionSide.NONE;
		// Difference between x and y pos of BoundingBoxes (Minkowski difference)
		int dx = (this.pos.x + this.width / 2) - (box.pos.x + box.width / 2);
		int dy = (this.pos.y + this.height / 2) - (box.pos.y + box.height / 2);
		// Width and Height of newly created box.
		int width = (this.width + box.width) / 2;
		int height = (this.height + box.height) / 2;
		int crossWidth = width * dy;
		int crossHeight = height * dx;

		if (Math.abs(dx) <= width && Math.abs(dy) <= height) {
			if (crossWidth > crossHeight) {
				if (crossWidth > (-crossHeight)) {
					// TOP
					result = CollisionSide.TOP;
//					this.pos = new Vec2i(this.pos.x, box.pos.y + box.height);
				} else {
					// RIGHT
					result = CollisionSide.RIGHT;
//					this.pos = new Vec2i(box.pos.x - this.width, this.pos.y);
				}
			} else {
				if (crossWidth > -(crossHeight)) {
					// LEFT
					result = CollisionSide.LEFT;
//					this.pos = new Vec2i(box.pos.x + box.width, this.pos.y);
				} else {
					// BOTTOM
					result = CollisionSide.BOTTOM;
//					this.pos = new Vec2i(this.pos.x, box.pos.y - this.height);
				}
			}
		}

//		System.out.println(result);
		return result;
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
		NONE(false), LEFT(true), RIGHT(true), TOP(true), BOTTOM(true);

		private final boolean collided;

		private CollisionSide(boolean collided) {
			this.collided = collided;
		}

		public boolean collided() {
			return this.collided;
		}
	}

}
