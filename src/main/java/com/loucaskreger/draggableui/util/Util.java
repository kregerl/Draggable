package com.loucaskreger.draggableui.util;

public class Util {

	public static int clamp(int val, int min, int max) {
		return  Math.max(min, Math.min(max, val));
	}

}
