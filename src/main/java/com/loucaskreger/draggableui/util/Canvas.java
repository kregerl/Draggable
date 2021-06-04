package com.loucaskreger.draggableui.util;

import com.loucaskreger.draggableui.DraggableUI;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class Canvas implements INBTSerializable<CompoundNBT> {

	private static final Minecraft mc = Minecraft.getInstance();
	private ResourceLocation texture;
	private BoundingBox2D boundingBox;
	private Color4f color;

	public Canvas(BoundingBox2D boundingBox, Color4f color, ResourceLocation texture) {
		this.boundingBox = boundingBox;
		this.color = color;
		this.texture = texture;
	}

	public Canvas(BoundingBox2D boundingBox, Color4f color) {
		this(boundingBox, color, new ResourceLocation(DraggableUI.MOD_ID, "textures/gui/background.png"));
	}

	public Canvas(BoundingBox2D boundingBox) {
		this(boundingBox, Color4f.BLANK);
	}

	public void render(AbstractGui screen) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.color4f(this.color.red, this.color.green, this.color.blue, this.color.alpha);
		screen.blit(this.boundingBox.getPos().x, this.boundingBox.getPos().y, 0, 0, this.boundingBox.getWidth(),
				this.boundingBox.getHeight());
		RenderSystem.color4f(Color4f.WHITE.red, Color4f.WHITE.green, Color4f.WHITE.blue, Color4f.WHITE.alpha);
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}

	public void render(int xPos, int yPos, AbstractGui screen) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.color4f(this.color.red, this.color.green, this.color.blue, this.color.alpha);
		screen.blit(xPos, yPos, 0, 0, this.boundingBox.getWidth(), this.boundingBox.getHeight());
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}

	public ResourceLocation getTexture() {
		return this.texture;
	}

	private static final String TEXTURE_KEY = "texture";
	private static final String BOX_KEY = "boundingbox";
	private static final String COLOR_KEY = "color";

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(TEXTURE_KEY, this.texture.toString());
		nbt.put(BOX_KEY, this.boundingBox.serializeNBT());
		nbt.put(COLOR_KEY, this.color.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.texture = new ResourceLocation(nbt.getString(TEXTURE_KEY));
		BoundingBox2D box = BoundingBox2D.read(nbt.getCompound(BOX_KEY));
		this.boundingBox.setPos(box.getPos());
		this.boundingBox.setWidth(box.getWidth());
		this.boundingBox.setHeight(box.getHeight());
		this.color.deserializeNBT(nbt.getCompound(COLOR_KEY));

	}
}
