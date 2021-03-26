package com.loucaskreger.draggableui.util;

public class Vec2i {
	public static final Vec2i ZERO = new Vec2i(0, 0);
	public static final Vec2i ONE = new Vec2i(1, 1);
	public static final Vec2i UNIT_X = new Vec2i(1, 0);
	public static final Vec2i NEGATIVE_UNIT_X = new Vec2i(-1, 0);
	public static final Vec2i UNIT_Y = new Vec2i(0, 1);
	public static final Vec2i NEGATIVE_UNIT_Y = new Vec2i(0, -1);
	public static final Vec2i MAX = new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
	public static final Vec2i MIN = new Vec2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
	public final int x;
	public final int y;

	public Vec2i(int xIn, int yIn) {
		this.x = xIn;
		this.y = yIn;
	}

	public Vec2i(double xIn, double yIn) {
		this((int) Math.round(xIn), (int) Math.round(yIn));
	}

	public boolean equals(Vec2i other) {
		return this.x == other.x && this.y == other.y;
	}

	@Override
	public String toString() {
		return String.format("X: %d, Y: %d", x, y);
	}

	public Vec2i add(Vec2i pos) {
		return add(pos.x, pos.y);
	}

	public Vec2i add(int x, int y) {
		return new Vec2i(this.x + x, this.y + y);
	}

	public Vec2i add(double x, double y) {
		return new Vec2i((int) Math.round(this.x + x), (int) Math.round(this.y + y));
	}

	public Vec2i subtract(Vec2i pos) {
		return new Vec2i(this.x - pos.x, this.y - pos.y);
	}

	public Vec2i subtract(int x, int y) {
		return this.subtract(new Vec2i(x, y));
	}

	public Vec2i multiplyBy(int x, int y) {
		return new Vec2i(this.x + x, this.y + y);
	}

}
