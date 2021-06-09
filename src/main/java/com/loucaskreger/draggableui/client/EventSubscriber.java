package com.loucaskreger.draggableui.client;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.util.WidgetManager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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

//	@SubscribeEvent
//	public static void onEntityDeath(final LivingDeathEvent event) {
//		LivingEntity entity = event.getEntityLiving();
//		if (entity instanceof PlayerEntity) {
//			System.out.println("Here " + ((PlayerEntity) entity).toString());
//		}
//	}
}
