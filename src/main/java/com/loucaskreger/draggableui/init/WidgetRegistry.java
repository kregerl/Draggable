package com.loucaskreger.draggableui.init;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.client.gui.widget.ExperienceWidget;
import com.loucaskreger.draggableui.client.gui.widget.HealthWidget;
import com.loucaskreger.draggableui.client.gui.widget.HotbarWidget;
import com.loucaskreger.draggableui.client.gui.widget.HungerWidget;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class WidgetRegistry {

	public static final DeferredRegister<DraggableWidget> WIDGETS = DeferredRegister.create(DraggableWidget.class,
			DraggableUI.MOD_ID);

//	public static final RegistryObject<DraggableWidget> widget = WIDGETS.register("this_is_the_widget",
//			() -> new DraggableWidget(new Vec2i(1, 1), 100, 100));

	public static final RegistryObject<DraggableWidget> HOTBAR_WIDGET = WIDGETS.register("hotbar_widget",
			() -> new HotbarWidget());

	public static final RegistryObject<DraggableWidget> HUNGER_WIDGET = WIDGETS.register("hunger_widget",
			() -> new HungerWidget());

	public static final RegistryObject<DraggableWidget> EXP_WIDGET = WIDGETS.register("experience_widget",
			() -> new ExperienceWidget());

	public static final RegistryObject<DraggableWidget> HEALTH_WIDGET = WIDGETS.register("health_widget",
			() -> new HealthWidget());

}
