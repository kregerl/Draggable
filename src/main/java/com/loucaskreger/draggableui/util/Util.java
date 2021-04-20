package com.loucaskreger.draggableui.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;

public class Util {

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	public static boolean isWithinBoundsX(int mouseX, BoundingBox2D boundingBox) {
		return mouseX > boundingBox.getPos().x && mouseX < boundingBox.getPos().x + boundingBox.getWidth();
	}

	public static boolean isWithinBoundsY(int mouseY, BoundingBox2D boundingBox) {
		return mouseY > boundingBox.getPos().y && mouseY < boundingBox.getPos().y + boundingBox.getHeight();
	}

	/**
	 * Returns whether or not the given mouse position (pos) is within the
	 * boundingBox
	 * 
	 * @param pos         - The position to check whether or not it is in the
	 *                    boundingBox.
	 * @param boundingBox - The boundingBox to check within.
	 * @return a boolean signifying whether or not the position is within the
	 *         boundingBox
	 */
	public static boolean isWithinBounds(Vec2i pos, BoundingBox2D boundingBox) {
		return isWithinBoundsX(pos.x, boundingBox) && isWithinBoundsY(pos.y, boundingBox);
	}
	
	public static void drawLine(IVertexBuilder builder, Color4f color, Vec2i point1, Vec2i point2) {
		Util.drawLine(builder, color, point1.x, point1.y, point2.x, point2.y);
	}

	public static void drawLine(IVertexBuilder builder, Color4f color, int x1, int y1, int x2, int y2) {
		builder.pos(x1, y1, 0).color(color.red, color.green, color.blue, color.alpha).endVertex();
		builder.pos(x2, y2, 0).color(color.red, color.green, color.blue, color.alpha).endVertex();
	}

}
