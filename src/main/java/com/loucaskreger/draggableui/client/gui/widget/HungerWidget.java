package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HungerWidget extends DraggableWidget {

	private static final int WIDTH = 81;
	private static final int HEIGHT = 9;

	public HungerWidget(DraggableScreen screen) {
		super((screen.width / 2) + 91 - WIDTH, screen.height - ForgeIngameGui.left_height, WIDTH, HEIGHT, true,
				"Hunger", screen);
	}

	@Override
	public void render(int mouseX, int mouseY, Screen screen) {
		super.render(mouseX, mouseY, screen);
		Minecraft mc = screen.getMinecraft();
		IngameGui ingameGui = mc.ingameGUI;
		if (ingameGui instanceof ForgeIngameGui) {
			ForgeIngameGui gui = (ForgeIngameGui) ingameGui;
			gui.renderFood(2 * (this.getPos().x - 91 + WIDTH), this.getPos().y + ForgeIngameGui.right_height);
		}
	}
}
