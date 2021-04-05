package com.loucaskreger.draggableui.util;

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

}
