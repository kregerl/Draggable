package com.loucaskreger.draggableui.client.gui.widget;

import java.util.Arrays;
import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.init.RegistryNames;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HotbarWidget extends LinkingWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public HotbarWidget() {
		super(0, 0, DefaultWidgetConstants.HOTBAR_WIDTH, DefaultWidgetConstants.HOTBAR_HEIGHT);
		this.defaultPosition = DefaultWidgetConstants.getHotbarPos();
		this.hasDefaultLinks = true;
		this.defaultLinks.addAll(Arrays.asList(WidgetRegistry.OFFHAND_WIDGET, WidgetRegistry.SELECTED_ITEM_WIDGET));
	}

	@Override
	public void init() {
		super.init();
		if (!this.linkedWidgets.isEmpty()
				&& this.linkedWidgets.containsKey(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())) {
			((SelectedItemTextWidget) this.linkedWidgets.get(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())
					.get()).setHotbarCenterX(this.getCenterPos().x);
		}
	}

	// Rotate 90 degrees
	// RenderSystem.rotatef(90, 1, 1, 0);
	// Override render method to

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			mc.gameSettings.heldItemTooltips = false;
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			GuiRenderer.Hotbar.renderHotbar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
					partialTicks, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), screen,
					screen == mc.ingameGUI);
			RenderSystem.popMatrix();
			RenderSystem.disableBlend();

		}
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		if (!this.linkedWidgets.isEmpty()
				&& this.linkedWidgets.containsKey(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())) {
			((SelectedItemTextWidget) this.linkedWidgets.get(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())
					.get()).setHotbarCenterX(this.getBoundingBox().getPos().x + this.getBoundingBox().getWidth() / 2);
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		ForgeIngameGui.renderHotbar = false;
		mc.gameSettings.heldItemTooltips = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.linked && !this.linkedWidgets.isEmpty()) {
			if (!(mc.player.getHeldItemOffhand() == ItemStack.EMPTY)
					&& this.linkedWidgets.containsKey(RegistryNames.OFFHAND_WIDGET.getResourceLocation())) {
				this.linkedWidgets.get(RegistryNames.OFFHAND_WIDGET.getResourceLocation()).get().setEnabled(true);
			} else if (this.linkedWidgets.containsKey(RegistryNames.OFFHAND_WIDGET.getResourceLocation())) {
				this.linkedWidgets.get(RegistryNames.OFFHAND_WIDGET.getResourceLocation()).get().setEnabled(false);
			}

			if (!(mc.player.getHeldItemMainhand().isEmpty())
					&& this.linkedWidgets.containsKey(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())) {
				this.linkedWidgets.get(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation()).get().setEnabled(true);
			} else if (this.linkedWidgets.containsKey(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation())) {
				this.linkedWidgets.get(RegistryNames.SELECTED_ITEM_WIDGET.getResourceLocation()).get()
						.setEnabled(false);
			}
		}
	}
}
