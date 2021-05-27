package com.loucaskreger.draggableui.client.gui.widget;

import java.util.List;

import javax.annotation.Nullable;

import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Canvas;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;

public class StaticWidget {

	private boolean mouseRightClicked;
	private BoundingBox2D boundingBox;
	private Canvas canvas;
	private Button button;
	private DraggableScreen screen;
	@Nullable
	private DraggableWidget selectedWidget;

	public StaticWidget(BoundingBox2D boundingBox, DraggableScreen screen) {
		this.boundingBox = boundingBox;
		this.screen = screen;
		this.mouseRightClicked = false;
		this.canvas = new Canvas(this.boundingBox, new Color4f(1.0f, 0.0f, 1.0f, 1.0f));
		this.selectedWidget = null;
		this.button = new Button(0, 0, 0, 0, "", button -> this.pressed(button)) {
			@Override
			public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
				super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
				Minecraft minecraft = Minecraft.getInstance();
				FontRenderer fontrenderer = minecraft.fontRenderer;
				minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				int i = this.getYImage(this.isHovered());
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2,
						this.height);
				this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
				int j = getFGColor();
				this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2,
						this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		};
	}

	public StaticWidget(Vec2i pos, int width, int height, DraggableScreen screen) {
		this(new BoundingBox2D(pos, width, height), screen);
	}

	public StaticWidget(int x, int y, int width, int height, DraggableScreen screen) {
		this(new BoundingBox2D(x, y, width, height), screen);
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton, DraggableScreen screen) {
		if (mouseButton == 1
				&& !(Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox()) && this.mouseRightClicked)) {
			this.selectedWidget = isOverWidget(mouseX, mouseY, screen);

			System.out.println(isOverWidget(mouseX, mouseY, screen) != null);
			this.mouseRightClicked = true;

			this.boundingBox.setPos(new Vec2i(mouseX - 5, mouseY - this.boundingBox.getHeight() + 5));
//			this.button = new Button(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
//					this.getBoundingBox().getWidth(), 20, "Click Me", button -> System.out.println("Here"));
			this.button.x = this.getBoundingBox().getPos().x;
			this.button.y = this.getBoundingBox().getPos().y;
			this.button.setWidth(this.getBoundingBox().getWidth());
			this.button.setHeight(20);
			this.button.setMessage("Click Me");
			this.button.visible = true;
		}
	}

	public void mouseMoved(double mouseX, double mouseY) {
		if (!Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.boundingBox) && this.mouseRightClicked) {
			this.mouseRightClicked = false;
		}
	}

	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		if (this.mouseRightClicked) {
			this.canvas.render(this.boundingBox.getPos().x, this.boundingBox.getPos().y, screen);
		}
	}

	@Nullable
	public DraggableWidget isOverWidget(double mouseX, double mouseY, List<DraggableWidget> widgets) {
		for (DraggableWidget widget : widgets) {
			if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), widget.getBoundingBox())) {
				return widget;
			}
		}
		return null;
	}

	public DraggableWidget isOverWidget(double mouseX, double mouseY, DraggableScreen screen) {
		return isOverWidget(mouseX, mouseY, screen.widgets);
	}

	public void pressed(Button button) {
		if (this.selectedWidget != null) {
			this.screen.widgets.remove(this.selectedWidget);
		}
		this.mouseRightClicked = false;
		this.button.visible = false;
	}

	public BoundingBox2D getBoundingBox() {
		return this.boundingBox;
	}

	public boolean isMouseClicked() {
		return this.mouseRightClicked;
	}

	@Nullable
	public Button getButton() {
		return this.button;
	}

}
