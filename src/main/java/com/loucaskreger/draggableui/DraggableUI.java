package com.loucaskreger.draggableui;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DraggableUI.MOD_ID)
public class DraggableUI {

	public static final String MOD_ID = "draggableui";

	public DraggableUI() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

	}

	private void setupCommon(final FMLCommonSetupEvent event) {

	}

	private void setupClient(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(EventSubscriber.key);
	}

}
