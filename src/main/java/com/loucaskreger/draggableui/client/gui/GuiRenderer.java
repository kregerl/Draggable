package com.loucaskreger.draggableui.client.gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.client.EventSubscriber;
import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.ITickableWidget;
import com.loucaskreger.draggableui.util.DeathHistoryManager;
import com.loucaskreger.draggableui.util.WidgetManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DraggableUI.MOD_ID)
public class GuiRenderer {

	public static Deque<ItemStack> recentCrafts = new LinkedList<ItemStack>();
	private static boolean isDead = false;

	public static final KeyBinding key = new KeyBinding(DraggableUI.MOD_ID + ".key.press", GLFW_KEY_K,
			DraggableUI.MOD_ID + ".key.categories");
	protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
	private static final Minecraft mc = Minecraft.getInstance();
	private static int remainingHighlightTicks = 0;
	private static ItemStack highlightingItemStack = ItemStack.EMPTY;

	@SubscribeEvent
	public static void renderGameOverlayPost(final RenderGameOverlayEvent.Post event) {
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL) && mc.currentScreen == null) {
//			if (WidgetManager.INSTANCE.isDirty()) {
//				WidgetManager.INSTANCE.loadWidgets();
//			}
			List<DraggableWidget> widgets = WidgetManager.INSTANCE.getWidgets();
			for (DraggableWidget widget : widgets) {
				if (widget instanceof ITickableWidget) {
					widget.tick();
				}
				widget.render(0, 0, 0, mc.ingameGUI);
			}
		}
	}

	@SubscribeEvent
	public static void onClientTick(final ClientTickEvent event) {
		if (mc.player != null) {
			ItemStack itemstack = mc.player.getHeldItemMainhand();
			if (itemstack.isEmpty()) {
				remainingHighlightTicks = 0;
			} else if (!highlightingItemStack.isEmpty() && itemstack.getItem() == highlightingItemStack.getItem()
					&& (itemstack.getDisplayName().equals(highlightingItemStack.getDisplayName())
							&& itemstack.getHighlightTip(itemstack.getDisplayName().getUnformattedComponentText())
									.equals(highlightingItemStack.getHighlightTip(
											highlightingItemStack.getDisplayName().getUnformattedComponentText())))) {
				if (remainingHighlightTicks > 0) {
					--remainingHighlightTicks;
				}
			} else {
				remainingHighlightTicks = 40;
			}

			highlightingItemStack = itemstack;

			// If this is not 0, player died.
			if (mc.player.deathTime > 0 && !isDead && DeathHistoryManager.INSTANCE != null) {
				isDead = true;
				DeathHistoryManager.INSTANCE.addDeath(mc.player.getPosition());
			} else if (mc.player.deathTime == 0) {
				isDead = false;
			}
		}

		if (key.isPressed()) {
			EventSubscriber.renderDefaults = false;
			WidgetManager.INSTANCE.loadWidgets();
//			DraggableScreen.open(new StringTextComponent("Test"), WidgetManager.INSTANCE.getWidgets());
			DraggableScreen.open(mc.playerController.getCurrentGameType());
		}
	}

	@SubscribeEvent
	public static void drawScreen(final GuiScreenEvent.DrawScreenEvent.Post event) {
		Screen screen = event.getGui();
		if (screen instanceof ContainerScreen) {
			ContainerScreen<?> inv = ((ContainerScreen<?>) screen);
			// guiLeft
//			ObfuscationReflectionHelper.setPrivateValue(ContainerScreen.class, inv, 200, "field_147003_i");
//			System.out.println(inv.getGuiLeft());
			DraggableScreen.open(mc.playerController.getCurrentGameType(), inv);
		}
	}

	@SubscribeEvent
	public static void onItemCrafted(final PlayerEvent.ItemCraftedEvent event) {
		recentCrafts.addFirst(event.getCrafting());
	}

	public static class Expbar {
		public static void renderExpBar(int x, int y, int screenWidth, int screenHeight, AbstractGui screen) {
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			mc.getProfiler().startSection("expBar");
			mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
			int i = mc.player.xpBarCap();
			if (i > 0) {
				int j = 182;
				int k = (int) (mc.player.experience * 183.0F);
				screen.blit(x, y, 0, 64, j, 5);
				if (k > 0) {
					screen.blit(x, y, 0, 69, k, 5);
				}
			}
			RenderSystem.disableBlend();
			RenderSystem.popMatrix();

		}

		public static void renderExpBarLevel(int x, int y) {
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			mc.getProfiler().endSection();
			if (mc.player.experienceLevel > 0) {
				mc.getProfiler().startSection("expLevel");
				String s = "" + mc.player.experienceLevel;
				int i1 = x;

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

	public static class Hotbar {
		public static void renderHotbar(int x, int y, float partialTicks, int screenWidth, int screenHeight,
				AbstractGui screen, boolean shouldRenderSelector) {
			PlayerEntity playerentity = getRenderViewPlayer();
			if (playerentity != null) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
				HandSide handside = playerentity.getPrimaryHand().opposite();
				int i = screenWidth / 2;
				int j = screen.getBlitOffset();
				// x and y (k,l)
//				int k = 182;
//				int l = 91;
				screen.setBlitOffset(-90);
				screen.blit(x, /* this.scaledHeight */ y, 0, 0, 182, 22);
				if (shouldRenderSelector) {
					screen.blit(x - 1 + playerentity.inventory.currentItem * 20, /* this.scaledHeight */y - 1, 0, 22,
							24, 22);
				}

				screen.setBlitOffset(j);
				RenderSystem.enableRescaleNormal();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();

				for (int i1 = 0; i1 < 9; ++i1) {
					int j1 = x + 1 + i1 * 20 + 2;
					int k1 = y + 3; // y = this.scaledHeight
					renderHotbarItem(j1, k1, screenWidth, screenHeight, partialTicks, playerentity,
							playerentity.inventory.mainInventory.get(i1));
				}

				if (mc.gameSettings.attackIndicator == AttackIndicatorStatus.HOTBAR) {
					float f = mc.player.getCooledAttackStrength(0.0F);
					if (f < 1.0F) {
						int j2 = y;// this.scaledHeight
						int k2 = x + 91 + 6;
						if (handside == HandSide.RIGHT) {
							k2 = i - 91 - 22;
						}

						mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
						int l1 = (int) (f * 19.0F);
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						screen.blit(k2, j2, 0, 94, 18, 18);
						screen.blit(k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
					}
				}

				RenderSystem.disableRescaleNormal();
				RenderSystem.disableBlend();
			}
		}

		public static void renderOffhandSlot(ItemStack itemstack, int x, int y, HandSide handside, AbstractGui screen) {
			if (!itemstack.isEmpty()) {
				mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
				screen.blit(x, y - 1, 24, 22, 29, 24);
			}
		}

		public static void renderOffhandItem(ItemStack itemstack, int screenWidth, int screenHeight, float partialTicks,
				PlayerEntity playerentity, HandSide handside, int x, int y) {
			if (!itemstack.isEmpty()) {
				int i2 = y + 3;
				renderHotbarItem(x, i2, screenWidth, screenHeight, partialTicks, playerentity, itemstack);
			}
		}

		public static void renderSelectedItem(ItemStack stack, int x, int y, int scaledWidth, int scaledHeight) {
			mc.getProfiler().startSection("selectedItemName");
			if (remainingHighlightTicks > 0 && !highlightingItemStack.isEmpty()) {
				ITextComponent itextcomponent = (new StringTextComponent(""))
						.appendSibling(highlightingItemStack.getDisplayName())
						.applyTextStyle(highlightingItemStack.getRarity().color);
				if (highlightingItemStack.hasDisplayName()) {
					itextcomponent.applyTextStyle(TextFormatting.ITALIC);
				}
				String s = itextcomponent.getFormattedText();
				s = highlightingItemStack.getHighlightTip(s);
				int i = x;
				int j = y;
				if (j <= 0) {
					j -= j - 1;
					if (y == 0) {
						j += 22;
					}
				}
				if (!mc.playerController.shouldDrawHUD()) {
					j += 14;
				}

				int k = (int) ((float) remainingHighlightTicks * 256.0F / 10.0F);
				if (k > 255) {
					k = 255;
				}

				if (k > 0) {
					RenderSystem.pushMatrix();
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					AbstractGui.fill(i - 2, j - 2, i + mc.fontRenderer.getStringWidth(s) + 2, j + 9 + 2,
							mc.gameSettings.getChatBackgroundColor(0));
					FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
					if (font == null) {
						mc.fontRenderer.drawStringWithShadow(s, (float) i, (float) y, 16777215 + (k << 24));
					} else {
						i = (i + font.getStringWidth(s)) / 2;
						font.drawStringWithShadow(s, (float) i, (float) y, 16777215 + (k << 24));
					}
					RenderSystem.disableBlend();
					RenderSystem.popMatrix();
				}
			}

			mc.getProfiler().endSection();
		}

		private static void renderHotbarItem(int x, int y, int width, int height, float partialTicks,
				PlayerEntity player, ItemStack stack) {
			if (!stack.isEmpty()) {
				float f = (float) stack.getAnimationsToGo() - partialTicks;
				if (f > 0.0F) {
					RenderSystem.pushMatrix();
					float f1 = 1.0F + f / 5.0F;
					RenderSystem.translatef((float) (x + 8), (float) (y + 12), 0.0F);
					RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
					RenderSystem.translatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
				}

				mc.getItemRenderer().renderItemAndEffectIntoGUI(player, stack, x, y);
				if (f > 0.0F) {
					RenderSystem.popMatrix();
				}

				mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, x, y);
			}
		}

		private static PlayerEntity getRenderViewPlayer() {
			return !(mc.getRenderViewEntity() instanceof PlayerEntity) ? null : (PlayerEntity) mc.getRenderViewEntity();
		}
	}

}
