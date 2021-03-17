package com.loucaskreger.draggableui.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.EventSubscriber;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.ExperienceWidget;
import com.loucaskreger.draggableui.client.gui.widget.HealthWidget;
import com.loucaskreger.draggableui.client.gui.widget.HotbarWidget;
import com.loucaskreger.draggableui.client.gui.widget.HungerWidget;
import com.loucaskreger.draggableui.util.BoundingBox;
import com.loucaskreger.draggableui.util.Vec2i;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class DraggableScreen extends Screen {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final int HEIGHT = 165;
	private static final int WIDTH = 247;

	// Save data in .minecraft/something
	// FMLPaths.GAMEDIR.get().resolve("mymod/data.nbt")
	public List<DraggableWidget> widgets;

	public BoundingBox interiorBounds;

	public DraggableScreen(ITextComponent titleIn) {
		super(titleIn);
		this.height = mc.getMainWindow().getScaledHeight();
		this.width = mc.getMainWindow().getScaledWidth();
		this.interiorBounds = new BoundingBox(new Vec2i(0, 0), null, this.width, this.height);

		this.widgets = new ArrayList<DraggableWidget>();
		this.widgets.add(new DraggableWidget(0, 0, WIDTH, HEIGHT, true, "Test", this) {
			@Override
			public void render(int mouseX, int mouseY, Screen screen) {
				super.render(mouseX, mouseY, screen);
				RenderSystem.pushMatrix();
				RenderSystem.enableBlend();
				screen.getMinecraft().getTextureManager()
						.bindTexture(new ResourceLocation(DraggableUI.MOD_ID, "textures/gui/widget.png"));
				screen.blit(this.getPos().x, this.getPos().y, 0, 0, this.width, this.height);
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();
			}
		});
//		this.widgets.add(new HotbarWidget(this));
//		this.widgets.add(new HealthWidget(this));
		this.widgets.add(new HungerWidget(this));
		this.widgets.add(new ExperienceWidget(this));
	}

	public DraggableScreen() {
		this(new StringTextComponent("test"));
	}

	@Override
	public void init() {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int scrollDelta) {
		this.widgets.forEach(i -> i.mouseClicked(mouseX, mouseY));
		return super.mouseClicked(mouseX, mouseY, scrollDelta);

	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		this.widgets.forEach(i -> {
			i.setSelected(false);
			i.mouseReleased();
		});
		return super.mouseReleased(mouseX, mouseY, scrollDelta);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_,
			double p_mouseDragged_8_) {
		// When cursor hits side of wall rotate widget
		this.widgets.forEach(i -> {
			i.mouseDragged((int) Math.round(mouseX), (int) Math.round(mouseY));

		});
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.widgets.forEach(i -> {
			i.setEnabled(true);
			i.getBoundingBox().setVisible(true);
			i.render(mouseX, mouseY, this);
		});

		super.render(mouseX, mouseY, partialTicks);
	}

	public static void open() {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen());
	}

	@Override
	public void onClose() {
		super.onClose();
//		saveState()
		ForgeIngameGui.renderHotbar = true;
		ForgeIngameGui.renderHealth = true;
		ForgeIngameGui.renderExperiance = true;
		EventSubscriber.shouldRenderFood = true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}
