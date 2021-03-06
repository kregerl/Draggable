package com.loucaskreger.draggableui.client.gui.screen;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.loucaskreger.draggableui.client.gui.widget.ContainerScreenWidget;
import com.loucaskreger.draggableui.client.gui.widget.DeathHistoryWidget;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.RightClickWidget;
import com.loucaskreger.draggableui.client.gui.widget.StaticWidget;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Vec2i;
import com.loucaskreger.draggableui.util.WidgetManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.common.util.INBTSerializable;

public class DraggableScreen extends Screen implements INBTSerializable<CompoundNBT> {

	private static final Minecraft mc = Minecraft.getInstance();

	// Movable widgets
	public List<DraggableWidget> widgets;
	// static objects that cant be moved
	public List<StaticWidget> staticWidgets;
	private RightClickWidget rightClickMenu;

	public DraggableScreen(ITextComponent titleIn) {
		super(titleIn);
		this.widgets = new ArrayList<DraggableWidget>();
		this.staticWidgets = new ArrayList<StaticWidget>();

		this.height = mc.getMainWindow().getScaledHeight();
		this.width = mc.getMainWindow().getScaledWidth();

		this.rightClickMenu = new RightClickWidget(new BoundingBox2D(0, 0, 70, 90), this);

		// Bottom Bound
		this.staticWidgets.add(
				new StaticWidget(new BoundingBox2D(new Vec2i(0, 0).add(0, this.height + 1), this.width, 10), this));
		// Top Bound
		this.staticWidgets
				.add(new StaticWidget(new BoundingBox2D(new Vec2i(0, 0).subtract(0, 6), this.width, 5), this));
		// Left Bound
		this.staticWidgets
				.add(new StaticWidget(new BoundingBox2D(new Vec2i(0, 0).subtract(6, 0), 5, this.height), this));
		// Right Bound
		this.staticWidgets
				.add(new StaticWidget(new BoundingBox2D(new Vec2i(0, 0).add(this.width + 1, 0), 5, this.height), this));

//		int crosshairSize = 9;
//		// Vertical crosshair bound
//		this.staticWidgets.add(new BoundingBox2D(
//				new Vec2i(this.width % 2 != 0 ? (this.width / 2) : (this.width / 2) - 1, (this.height / 2) - 4), 1,
//				crosshairSize));
//		// Horizontal crosshair bound
//		this.staticWidgets.add(new BoundingBox2D(
//				new Vec2i(this.width % 2 != 0 ? (this.width / 2) - 4 : (this.width / 2) - 5, (this.height / 2)),
//				crosshairSize, 1));

	}

	public DraggableScreen(ITextComponent title, DraggableWidget... widgets) {
		this(title);
		this.widgets.clear();
		for (DraggableWidget widget : widgets) {
			widget.setScreen(this);
			widget.setEnabled(true);
			widget.setShouldMoveToDefaultPos(false);
			this.widgets.add(widget);
		}

	}

	public DraggableScreen(ITextComponent title, List<DraggableWidget> widgets) {
		this(title, listToArray(widgets));
	}

	private static DraggableWidget[] listToArray(List<DraggableWidget> widgets) {
		DraggableWidget[] widgetsArr = new DraggableWidget[widgets.size()];
		for (int i = 0; i < widgets.size(); i++) {
			widgetsArr[i] = widgets.get(i);
		}
		return widgetsArr;
	}

	public DraggableScreen(ContainerScreen<?> previousScreen, ITextComponent title, DraggableWidget... widgets) {
		this(title, widgets);
		this.widgets.add(0, new ContainerScreenWidget(() -> previousScreen));
	}

	public DraggableScreen() {
		this(new StringTextComponent("test"));
	}

	public DraggableScreen(ContainerScreen<?> previousScreen) {
		this(new StringTextComponent("test"));
		this.widgets.add(new ContainerScreenWidget(() -> previousScreen));
	}

	@Override
	public void tick() {
		super.tick();
		this.widgets.forEach(i -> i.tick());
		this.widgets.forEach(i -> {
//			System.out.println(i.getState().isChanged());
//			if (!i.getState().isChanged()) {
//				this.undoManager.addState(i);
//			}
		});
	}

	@Override
	public void init() {
		this.widgets.forEach(i -> i.init());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
//		Need to add undo/redo state after widgets have registered mouse clicks otherwise none are selected
		if (mouseButton == 0) {
			this.widgets.forEach(i -> i.mouseClicked(mouseX, mouseY, mouseButton));
		}
		this.rightClickMenu.mouseClicked(mouseX, mouseY, mouseButton, this);

//		if (mouseButton == 1 && this.rightClickMenu.getButton() != null) {
//			this.addButton(this.rightClickMenu.getButton());
//		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);

	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		this.widgets.forEach(i -> {
			i.mouseReleased(mouseX, mouseY, scrollDelta);
		});
		this.staticWidgets.forEach(i -> {
			i.getBoundingBox().drawBoundingBoxOutline();
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
	public void mouseMoved(double mouseX, double mouseY) {
		// Used for hover detection
		this.widgets.forEach(i -> i.mouseMoved(mouseX, mouseY));
		this.rightClickMenu.mouseMoved(mouseX, mouseY);
//		if (!this.rightClickMenu.isMouseClicked() && this.rightClickMenu.getButton() != null) {
//			this.buttons.remove(this.rightClickMenu.getButton());
//		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		this.widgets.forEach(i -> {
			i.getBoundingBox().setVisible(true);
			i.render(mouseX, mouseY, partialTicks, this);
		});
		this.staticWidgets.forEach(i -> {
			i.getBoundingBox().setVisible(true);
			i.getBoundingBox().drawBoundingBoxOutline();
		});
		this.rightClickMenu.render(mouseX, mouseY, partialTicks, this);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		this.widgets.forEach(i -> {
			i.onClose();
		});
		WidgetManager.INSTANCE
				.saveState(this.widgets.stream().filter(i -> i.isSerilizable()).collect(Collectors.toList()));
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

		if (keyCode == GLFW_KEY_R) {
			this.widgets.forEach(i -> {
				i.setShouldMoveToDefaultPos(true);
			});
		}

		if (keyCode == GLFW_KEY_Z && InputMappings.isKeyDown(mc.getMainWindow().getHandle(), GLFW_KEY_LEFT_CONTROL)
				&& modifiers == 2) {
//			this.undoManager.undo();
		}
		if (keyCode == GLFW_KEY_U && InputMappings.isKeyDown(mc.getMainWindow().getHandle(), GLFW_KEY_LEFT_CONTROL)
				&& modifiers == 2) {
//			this.undoManager.redo();
		}

		return true;

	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

		if (keyCode == GLFW_KEY_R) {
			this.widgets.forEach(i -> {
				i.setShouldMoveToDefaultPos(false);
			});

		}
		return true;
	}

	@Override
	public <T extends Widget> T addButton(T p_addButton_1_) {
		return super.addButton(p_addButton_1_);
	}

	public <T extends Widget> void addAllButtons(List<T> buttons) {
		buttons.forEach(button -> this.addButton(button));
	}

	public List<Widget> getButtons() {
		return this.buttons;
	}

	public static void open() {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen());
	}

	public static void open(ContainerScreen<?> screen) {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen(screen));
	}

	public static void open(ITextComponent title, DraggableWidget... widgets) {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen(title, widgets));
	}

	public static void open(ITextComponent title, List<DraggableWidget> widgets) {
		Minecraft.getInstance().displayGuiScreen(new DraggableScreen(title, widgets));
	}

	public static void open(GameType type) {
		switch (type) {
		case SURVIVAL:
		case ADVENTURE:
			DraggableScreen.open(new StringTextComponent(type.getName()), WidgetRegistry.HEALTH_WIDGET.get(),
					WidgetRegistry.HUNGER_WIDGET.get(), WidgetRegistry.HOTBAR_WIDGET.get(),
					WidgetRegistry.EXPERIENCE_LEVEL_WIDGET.get(), WidgetRegistry.OFFHAND_WIDGET.get(),
					WidgetRegistry.SELECTED_ITEM_WIDGET.get(), WidgetRegistry.DEATH_HISTORY_WIDGET.get());
			break;
		case CREATIVE:

			break;// if (WidgetManager.INSTANCE.isDirty()) {
//			WidgetManager.INSTANCE.loadWidgets();
//		}
//		WidgetManager.INSTANCE.widgets.forEach(i -> {
//			i.setScreen(this);
//			i.setEnabled(true);
//			i.setShouldMoveToDefaultPos(false);
//			this.widgets.add(i);
//		});
//		if (WidgetManager.INSTANCE.widgets.isEmpty()) {
//			IForgeRegistry<DraggableWidget> registry = GameRegistry.findRegistry(DraggableWidget.class);
//			for (DraggableWidget widget : registry) {
//				widget.setShouldMoveToDefaultPos(true);
//				widget.setScreen(this);
//				widget.setEnabled(true);
//				this.widgets.add(widget);
//			}
//
//		}
		case SPECTATOR:
			break;
		case NOT_SET:
		default:
			break;// if (WidgetManager.INSTANCE.isDirty()) {
//			WidgetManager.INSTANCE.loadWidgets();
//		}
//		WidgetManager.INSTANCE.widgets.forEach(i -> {
//			i.setScreen(this);
//			i.setEnabled(true);
//			i.setShouldMoveToDefaultPos(false);
//			this.widgets.add(i);
//		});
//		if (WidgetManager.INSTANCE.widgets.isEmpty()) {
//			IForgeRegistry<DraggableWidget> registry = GameRegistry.findRegistry(DraggableWidget.class);
//			for (DraggableWidget widget : registry) {
//				widget.setShouldMoveToDefaultPos(true);
//				widget.setScreen(this);
//				widget.setEnabled(true);
//				this.widgets.add(widget);
//			}
//
//		}

		}
	}

	public static void open(GameType type, ContainerScreen<?> previousScreen) {
		switch (type) {
		case SURVIVAL:
		case ADVENTURE:
			Minecraft.getInstance()
					.displayGuiScreen(new DraggableScreen(previousScreen, new StringTextComponent(type.getName())));
			break;
		case CREATIVE:
			Minecraft.getInstance()
					.displayGuiScreen(new DraggableScreen(previousScreen, new StringTextComponent(type.getName()),
							WidgetRegistry.HEALTH_WIDGET.get(), WidgetRegistry.HUNGER_WIDGET.get(),
							WidgetRegistry.HOTBAR_WIDGET.get(), WidgetRegistry.EXPERIENCE_LEVEL_WIDGET.get(),
							WidgetRegistry.OFFHAND_WIDGET.get(), WidgetRegistry.SELECTED_ITEM_WIDGET.get()));
			break;
		case SPECTATOR:
			break;
		case NOT_SET:
		default:
			break;

		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

}
