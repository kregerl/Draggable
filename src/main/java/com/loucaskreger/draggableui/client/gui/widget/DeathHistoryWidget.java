package com.loucaskreger.draggableui.client.gui.widget;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.DeathHistoryManager;
import com.loucaskreger.draggableui.util.WidgetType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;

public class DeathHistoryWidget extends DraggableWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public DeathHistoryWidget() {
		super(0, 0, 100, 100);
		this.type = WidgetType.ANY_HUD;
	}

	@Override
	public void init() {
		super.init();
		DeathHistoryManager.init();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		FontRenderer fontRenderer = mc.fontRenderer;
		Deque<BlockPos> deaths = DeathHistoryManager.INSTANCE != null ? DeathHistoryManager.INSTANCE.getRecentDeaths()
				: new LinkedList<BlockPos>();
		Iterator<BlockPos> it = deaths.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			BlockPos next = it.next();
			fontRenderer.drawString(next.toString(), this.getBoundingBox().getPos().x,
					this.getBoundingBox().getPos().y + index * 10, Color4f.WHITE.toIntegerColor());
		}

	}
}
