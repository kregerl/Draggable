package com.loucaskreger.draggableui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.WidgetManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(DraggableUI.MOD_ID)
public class DraggableUI {

	public static final String MOD_ID = "draggableui";
	public static final Logger LOGGER = LogManager.getLogger();
	public static IForgeRegistry<DraggableWidget> registry;

	public DraggableUI() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupCommon);
		bus.addListener(this::setupClient);
		bus.addListener(this::newRegistryEvent);

		WidgetManager.init();
		WidgetRegistry.WIDGETS.register(bus);

	}

	private void setupCommon(final FMLCommonSetupEvent event) {
		
	}

	private void setupClient(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(GuiRenderer.key);
		WidgetManager.INSTANCE.loadWidgets();
	}

	private void newRegistryEvent(final RegistryEvent.NewRegistry event) {
		@SuppressWarnings("unused")
		IForgeRegistry<DraggableWidget> registry = new RegistryBuilder<DraggableWidget>().setType(DraggableWidget.class)
				.setName(new ResourceLocation(DraggableUI.MOD_ID, "widgets")).create();
	}

}
