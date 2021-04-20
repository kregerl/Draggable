package com.loucaskreger.draggableui.client.gui.screen;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import java.util.ArrayList;
import java.util.List;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.LinkingWidget;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Vec2i;
import com.loucaskreger.draggableui.util.WidgetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class DraggableScreen extends Screen {

	private static final Minecraft mc = Minecraft.getInstance();

	// Movable widgets
	public List<DraggableWidget> widgets;
	// static objects that cant be moved
	public List<BoundingBox2D> staticWidgets;
	private WidgetUndoManager undoManager;

	public BoundingBox2D interiorBounds;

	public DraggableScreen(ITextComponent titleIn) {
		super(titleIn);
		this.widgets = new ArrayList<DraggableWidget>();
		this.staticWidgets = new ArrayList<BoundingBox2D>();
		this.undoManager = new WidgetUndoManager(this.widgets);

		this.height = mc.getMainWindow().getScaledHeight();
		this.width = mc.getMainWindow().getScaledWidth();

		if (WidgetManager.INSTANCE.isDirty()) {
			WidgetManager.INSTANCE.loadWidgets();
		}
		WidgetManager.INSTANCE.widgets.forEach(i -> {
			i.setScreen(this);
			i.setEnabled(true);
			i.setShouldMoveToDefaultPos(false);
			this.widgets.add(i);
		});
		if (WidgetManager.INSTANCE.widgets.isEmpty()) {
			IForgeRegistry<DraggableWidget> registry = GameRegistry.findRegistry(DraggableWidget.class);
			for (DraggableWidget widget : registry) {

				widget.setScreen(this);
				widget.setEnabled(true);
				this.widgets.add(widget);
			}

		}

		this.interiorBounds = new BoundingBox2D(new Vec2i(0, 0), this.width, this.height);

		// Bottom Bound
		this.staticWidgets.add(new BoundingBox2D(interiorBounds.getPos().add(0, interiorBounds.getHeight() + 1),
				interiorBounds.getWidth(), 10));
		// Top Bound
		this.staticWidgets.add(new BoundingBox2D(interiorBounds.getPos().subtract(0, 6), interiorBounds.getWidth(), 5));
		// Left Bound
		this.staticWidgets
				.add(new BoundingBox2D(interiorBounds.getPos().subtract(6, 0), 5, interiorBounds.getHeight()));
		// Right Bound
		this.staticWidgets.add(new BoundingBox2D(interiorBounds.getPos().add(interiorBounds.getWidth() + 1, 0), 5,
				interiorBounds.getHeight()));

		int crosshairSize = 9;
		// Vertical crosshair bound
		this.staticWidgets.add(new BoundingBox2D(
				new Vec2i(this.width % 2 != 0 ? (this.width / 2) : (this.width / 2) - 1, (this.height / 2) - 4), 1,
				crosshairSize));
		// Horizontal crosshair bound
		this.staticWidgets.add(new BoundingBox2D(
				new Vec2i(this.width % 2 != 0 ? (this.width / 2) - 4 : (this.width / 2) - 5, (this.height / 2)),
				crosshairSize, 1));

	}

	public DraggableScreen() {
		this(new StringTextComponent("test"));
	}

	@Override
	public void tick() {
		this.widgets.forEach(i -> i.tick());
		super.tick();
	}

	@Override
	public void init() {
		this.widgets.forEach(i -> i.init());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int scrollDelta) {
		this.widgets.forEach(i -> i.mouseClicked(mouseX, mouseY));
		return super.mouseClicked(mouseX, mouseY, scrollDelta);

	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		this.undoManager.addState();

		this.widgets.forEach(i -> {
			i.mouseReleased(mouseX, mouseY, scrollDelta);
		});
		this.staticWidgets.forEach(i -> {
			i.drawBoundingBoxOutline();
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
			i.getBoundingBox().setVisible(true);
			i.render(mouseX, mouseY, partialTicks, this);
		});
		this.staticWidgets.forEach(i -> {
			i.setVisible(true);
			i.drawBoundingBoxOutline();
		});

		super.render(mouseX, mouseY, partialTicks);
	}

	public static void open() {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen());
	}

	@Override
	public void onClose() {
		WidgetManager.INSTANCE.saveState(this.widgets);
//		GuiRenderer.drawWidgets(this.widgets);
		this.widgets.forEach(i -> i.onClose());
		super.onClose();

	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (super.keyPressed(keyCode, scanCode, modifiers) || keyCode == GLFW_KEY_K) {
			this.onClose();
		}
		this.widgets.forEach(i -> i.keyPressed(keyCode, scanCode, modifiers));

		if (keyCode == GLFW_KEY_LEFT_SHIFT) {
			for (DraggableWidget widget : this.widgets) {
				if (widget instanceof LinkingWidget) {
					widget.getBoundingBox().setColor(new Color4f(0.4f, 1, 0.2f, 1.0f));
					widget.getBoundingBox().setVisible(true);
				}
			}
		}

		if (keyCode == GLFW_KEY_R) {
			this.widgets.forEach(i -> {
				i.setShouldMoveToDefaultPos(true);
			});
		}

		if (keyCode == GLFW_KEY_Z && InputMappings.isKeyDown(mc.getMainWindow().getHandle(), GLFW_KEY_LEFT_CONTROL)
				&& modifiers == 2) {
			this.undoManager.undo();
		}
		if (keyCode == GLFW_KEY_U && InputMappings.isKeyDown(mc.getMainWindow().getHandle(), GLFW_KEY_LEFT_CONTROL)
				&& modifiers == 2) {
			this.undoManager.redo();
		}

		return true;

	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW_KEY_LEFT_SHIFT) {
			for (DraggableWidget widget : this.widgets) {
				if (widget instanceof LinkingWidget && widget.getBoundingBox().isVisible()) {
					widget.getBoundingBox().setColor(BoundingBox2D.DEFAULT_COLOR);
					widget.getBoundingBox().setVisible(false);
				}
			}
		}

		if (keyCode == GLFW_KEY_R) {
			this.widgets.forEach(i -> {
				i.setShouldMoveToDefaultPos(false);
			});

		}
		return true;
	}

}
