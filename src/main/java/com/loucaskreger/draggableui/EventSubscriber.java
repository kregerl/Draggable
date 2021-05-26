package com.loucaskreger.draggableui;

import com.loucaskreger.draggableui.util.WidgetManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DraggableUI.MOD_ID, value = Dist.CLIENT)
public class EventSubscriber {

	public static boolean renderDefaults = true;

	@SubscribeEvent
	public static void onGUIRender(final RenderGameOverlayEvent.Pre event) {
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL)
				&& (!WidgetManager.INSTANCE.widgets.isEmpty() || !renderDefaults)) {
			ForgeIngameGui.renderHotbar = false;
			ForgeIngameGui.renderHealth = false;
			ForgeIngameGui.renderExperiance = false;
			ForgeIngameGui.renderFood = false;
			ForgeIngameGui.left_height = 39;
			ForgeIngameGui.right_height = 39;
		}
	}

}
