package com.loucaskreger.draggableui.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;

public class ExperienceWidget extends DraggableWidget {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final int WIDTH = 182;
	private static final int HEIGHT = 5;

	private int scaledWidth;
	private int scaledHeight;

	public ExperienceWidget(int screenWidth, int screenHeight) {
		super((screenWidth / 2) - 91, screenHeight - 29, WIDTH, HEIGHT, true, "Exp");

		this.scaledWidth = screenWidth / 2;
		this.scaledHeight = screenHeight / 2;
	}

	@Override
	public void render(int mouseX, int mouseY, Screen screen) {
		super.render(mouseX, mouseY, screen);
		this.renderExpBar(this.getPos().x, this.getPos().y, screen);
	}

	public void renderExpBar(int x, int y, Screen screen) {
		mc.getProfiler().startSection("expBar");
		mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
		int i = mc.player.xpBarCap();
		if (i > 0) {
			int j = 182;
			int k = (int) (mc.player.experience * 183.0F);
			screen.blit(x, y, 0, 64, 182, 5);
			if (k > 0) {
				screen.blit(x, y, 0, 69, k, 5);
			}
		}

		mc.getProfiler().endSection();
		if (mc.player.experienceLevel > 0) {
			mc.getProfiler().startSection("expLevel");
			String s = "" + mc.player.experienceLevel;
			int i1 = (this.scaledWidth - mc.fontRenderer.getStringWidth(s)) / 2;
			int j1 = this.scaledHeight - 31 - 4;
			mc.fontRenderer.drawString(s, (float) (i1 + 1), (float) j1, 0);
			mc.fontRenderer.drawString(s, (float) (i1 - 1), (float) j1, 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) (j1 + 1), 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) (j1 - 1), 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) j1, 8453920);
			mc.getProfiler().endSection();
		}

	}
}
