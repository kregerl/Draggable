package com.loucaskreger.draggableui.init;

import com.loucaskreger.draggableui.DraggableUI;

import net.minecraft.util.ResourceLocation;

public enum RegistryNames {
	HUNGER_WIDGET(createRegistryName("hunger_widget")), HEALTH_WIDGET(createRegistryName("health_widget")),
	EXP_WIDGET(createRegistryName("experience_widget")), HOTBAR_WIDGET(createRegistryName("hotbar_widget")),
	EXPERIENCE_LEVEL_WIDGET(createRegistryName("experience_level_widget")),
	OFFHAND_WIDGET(createRegistryName("offhand_widget")),
	SELECTED_ITEM_WIDGET(createRegistryName("selected_item_widget")),
	COORDINATE_WIDGET(createRegistryName("coordinate_widget")),
	CONTAINER_SCREEN_WIDGET(createRegistryName("container_screen_widget")),
	DEATH_HISTORY_WIDGET(createRegistryName("death_history_widget"));

	private ResourceLocation location;

	RegistryNames(ResourceLocation resourceLocation) {
		this.location = resourceLocation;
	}

	public String getPath() {
		return location.getPath();
	}

	public String getNamespace() {
		return location.getNamespace();
	}

	public ResourceLocation getResourceLocation() {
		return this.location;
	}

	private static ResourceLocation createRegistryName(String id) {
		return new ResourceLocation(DraggableUI.MOD_ID, id);
	}
}
