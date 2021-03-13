package com.loucaskreger.draggableui.util;

import java.io.File;
import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

public class WidgetLoader {

	public static WidgetLoader INSTANCE;

	public static void init() {
		INSTANCE = new WidgetLoader();
	}

	public void loadWidgets() {
		Path dirPath = FMLPaths.GAMEDIR.get().resolve("draggable/savedState");
		File path = new File(dirPath.toString());
		path.mkdirs();
	}

}
