package com.loucaskreger.draggableui;

import com.loucaskreger.draggableui.util.WidgetManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DraggableUI.MOD_ID)
public class EventSubscriber {

	public static boolean shouldRenderDefaults = true;

	@SubscribeEvent
	public static void onGUIRender(final RenderGameOverlayEvent.Pre event) {
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL)
				&& (!WidgetManager.INSTANCE.widgets.isEmpty() || !shouldRenderDefaults)) {
			ForgeIngameGui.renderHotbar = false;
			ForgeIngameGui.renderHealth = false;
			ForgeIngameGui.renderExperiance = false;
			ForgeIngameGui.renderFood = false;
		}
	}

}
