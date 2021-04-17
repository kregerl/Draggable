package com.loucaskreger.draggableui.init;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.ExperienceLevelWidget;
import com.loucaskreger.draggableui.client.gui.widget.ExperienceWidget;
import com.loucaskreger.draggableui.client.gui.widget.HealthWidget;
import com.loucaskreger.draggableui.client.gui.widget.HotbarWidget;
import com.loucaskreger.draggableui.client.gui.widget.HungerWidget;
import com.loucaskreger.draggableui.client.gui.widget.LinkableWidget;
import com.loucaskreger.draggableui.client.gui.widget.LinkingWidget;
import com.loucaskreger.draggableui.client.gui.widget.OffhandWidget;
import com.loucaskreger.draggableui.client.gui.widget.SelectedItemTextWidget;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class WidgetRegistry {

	public static final DeferredRegister<DraggableWidget> WIDGETS = DeferredRegister.create(DraggableWidget.class,
			DraggableUI.MOD_ID);

	public static final RegistryObject<DraggableWidget> HUNGER_WIDGET = WIDGETS
			.register(RegistryNames.HUNGER_WIDGET.getPath(), HungerWidget::new);

	public static final RegistryObject<DraggableWidget> HEALTH_WIDGET = WIDGETS
			.register(RegistryNames.HEALTH_WIDGET.getPath(), HealthWidget::new);

//	Linking Widgets ----------------------------------------------------------------------------------------------
	public static final RegistryObject<LinkingWidget> EXP_WIDGET = WIDGETS.register(RegistryNames.EXP_WIDGET.getPath(),
			ExperienceWidget::new);

	public static final RegistryObject<LinkingWidget> HOTBAR_WIDGET = WIDGETS
			.register(RegistryNames.HOTBAR_WIDGET.getPath(), HotbarWidget::new);

//	Linkable Widgets ---------------------------------------------------------------------------------------------
	public static final RegistryObject<LinkableWidget> EXPERIENCE_LEVEL_WIDGET = WIDGETS
			.register(RegistryNames.EXPERIENCE_LEVEL_WIDGET.getPath(), ExperienceLevelWidget::new);

	public static final RegistryObject<LinkableWidget> OFFHAND_WIDGET = WIDGETS
			.register(RegistryNames.OFFHAND_WIDGET.getPath(), OffhandWidget::new);

	public static final RegistryObject<LinkableWidget> SELECTED_ITEM_WIDGET = WIDGETS
			.register(RegistryNames.SELECTED_ITEM_WIDGET.getPath(), SelectedItemTextWidget::new);

}
