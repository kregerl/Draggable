package com.loucaskreger.draggableui.init;

import com.loucaskreger.draggableui.DraggableUI;

import net.minecraft.util.ResourceLocation;

public enum RegistryNames {
	HUNGER_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "hunger_widget")),
	HEALTH_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "health_widget")),
	EXP_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "experience_widget")),
	HOTBAR_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "hotbar_widget")),
	EXPERIENCE_LEVEL_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "experience_level_widget")),
	OFFHAND_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "offhand_widget")),
	SELECTED_ITEM_WIDGET(new ResourceLocation(DraggableUI.MOD_ID, "selected_item_widget"));

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
}
