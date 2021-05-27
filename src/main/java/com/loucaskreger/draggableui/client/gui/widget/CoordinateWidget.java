package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.util.Canvas;
import com.loucaskreger.draggableui.util.WidgetType;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class CoordinateWidget extends DraggableWidget implements ITickableWidget {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final ResourceLocation TEXTURE = new ResourceLocation(DraggableUI.MOD_ID,
			"textures/gui/background.png");
	private static final int TEXT_COLOR = 0xFFFFFF;
	private static final int PADDING = 6;
	private boolean displayNetherCoords;
	private int percision;
	private String position;
	private String netherPos;
	private Canvas canvas;

	public CoordinateWidget() {
		super(0, 0, 0, 12);
		this.position = "";
		this.percision = 2;
		this.displayNetherCoords = false;
		this.type = WidgetType.ANY_HUD;
		this.canvas = new Canvas(this.getBoundingBox());
	}

	@Override
	public void tick() {
		super.tick();
		Vec3d pos = mc.player.getPositionVec();
		this.position = String.format(
				"X: %." + this.percision + "f Y: %." + this.percision + "f Z: %." + this.percision + "f", pos.x, pos.y,
				pos.z);
		if (this.displayNetherCoords) {
			this.netherPos = String.format(
					"X: %." + this.percision + "f Y: %." + this.percision + "f Z: %." + this.percision + "f", pos.x / 8,
					pos.y, pos.z / 8);
			this.getBoundingBox().setHeight(26);
		}
		this.getBoundingBox().setWidth(mc.fontRenderer.getStringWidth(this.position) + PADDING);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		FontRenderer fontRenderer = mc.fontRenderer;
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		int x = this.getBoundingBox().getPos().x;
		int y = this.getBoundingBox().getPos().y;

		this.canvas.render(screen);
		fontRenderer.drawString(this.position,
				x + (this.getBoundingBox().getWidth() / 2) - (fontRenderer.getStringWidth(this.position) / 2), y + 2,
				TEXT_COLOR);

		if (this.displayNetherCoords) {
			fontRenderer.drawString(this.netherPos,
					x + (this.getBoundingBox().getWidth() / 2) - (fontRenderer.getStringWidth(this.position) / 2),
					y + 14, TEXT_COLOR);
		}
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}

}
