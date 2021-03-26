package com.loucaskreger.draggableui.client.gui;

import java.util.List;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.ExperienceWidget;
import com.loucaskreger.draggableui.util.Vec2i;
import com.loucaskreger.draggableui.util.WidgetManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DraggableUI.MOD_ID)
public class GuiRenderer {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void renderGameOverlayPost(final RenderGameOverlayEvent.Post event) {
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL) && mc.currentScreen == null) {
			if (WidgetManager.INSTANCE.isDirty()) {
				WidgetManager.INSTANCE.loadWidgets();
			}
			List<DraggableWidget> widgets = WidgetManager.INSTANCE.getWidgets();
			for (DraggableWidget widget : widgets) {
				widget.render(0, 0, 0, mc.ingameGUI);
			}
		}
	}

	public static void renderExpBar(int x, int y, int screenWidth, int screenHeight, AbstractGui screen) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
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
			int i1 = x + (182 / 2) - mc.fontRenderer.getStringWidth(s) / 2;
			int j1 = y - 6;
			mc.fontRenderer.drawString(s, (float) (i1 + 1), (float) j1, 0);
			mc.fontRenderer.drawString(s, (float) (i1 - 1), (float) j1, 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) (j1 + 1), 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) (j1 - 1), 0);
			mc.fontRenderer.drawString(s, (float) i1, (float) j1, 8453920);
			mc.getProfiler().endSection();
		}
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();

	}

}
