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

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class WidgetRegistry {

	public static final DeferredRegister<DraggableWidget> WIDGETS = DeferredRegister.create(DraggableWidget.class,
			DraggableUI.MOD_ID);

	public static final RegistryObject<DraggableWidget> HUNGER_WIDGET = WIDGETS.register("hunger_widget",
			HungerWidget::new);

	public static final RegistryObject<DraggableWidget> HEALTH_WIDGET = WIDGETS.register("health_widget",
			HealthWidget::new);

//	Linking Widgets ----------------------------------------------------------------------------------------------
	public static final RegistryObject<LinkingWidget> EXP_WIDGET = WIDGETS.register("experience_widget",
			ExperienceWidget::new);

	public static final RegistryObject<LinkingWidget> HOTBAR_WIDGET = WIDGETS.register("hotbar_widget",
			HotbarWidget::new);

//	Linkable Widgets ---------------------------------------------------------------------------------------------
	public static final RegistryObject<LinkableWidget> EXPERIENCE_LEVEL_WIDGET = WIDGETS
			.register("experience_level_widget", ExperienceLevelWidget::new);

	public static final RegistryObject<LinkableWidget> OFFHAND_WIDGET = WIDGETS.register("offhand_widget",
			OffhandWidget::new);

}
