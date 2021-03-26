package com.loucaskreger.draggableui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.WidgetManager;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DraggableUI.MOD_ID)
public class EventSubscriber {

	public static final KeyBinding key = new KeyBinding(DraggableUI.MOD_ID + ".key.press", GLFW_KEY_K,
			DraggableUI.MOD_ID + ".key.categories");
	public static boolean shouldRenderDefaults = true;

	@SubscribeEvent
	public static void onClientTick(final ClientTickEvent event) {
		if (key.isPressed()) {
			ForgeIngameGui.left_height = 39;
			ForgeIngameGui.right_height = 39;
			shouldRenderDefaults = false;
			DraggableScreen.open();
		}
	}

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
