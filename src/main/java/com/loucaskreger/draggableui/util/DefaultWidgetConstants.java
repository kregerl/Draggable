package com.loucaskreger.draggableui.util;

import java.util.function.Function;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class DefaultWidgetConstants {

	public static final int HOTBAR_WIDTH = 182;
	public static final int HOTBAR_HEIGHT = 22;

	public static Function<Screen, Vec2i> getHotbarPos() {
		return (screen) -> new Vec2i((screen.width / 2) - 91, screen.height - 22);
	}

	public static final int HEALTH_WIDTH = 81;
	public static final int HEALTH_HEIGHT = 9;

	public static Function<Screen, Vec2i> getHealthPos() {
		return (screen) -> new Vec2i((screen.width / 2) - 91, screen.height - ForgeIngameGui.left_height);
	}

	public static final int EXPERIENCE_WIDTH = 182;
	public static final int EXPERIENCE_HEIGHT = 5;

	public static Function<Screen, Vec2i> getExperiencePos() {
		return (screen) -> new Vec2i((screen.width / 2) - 91, screen.height - 29);
	}

	public static final int HUNGER_WIDTH = 81;
	public static final int HUNGER_HEIGHT = 9;

	public static Function<Screen, Vec2i> getHungerPos() {
		return (screen) -> new Vec2i((screen.width / 2) + 91 - HUNGER_WIDTH,
				screen.height - ForgeIngameGui.left_height);
	}
}
