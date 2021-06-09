package com.loucaskreger.draggableui.util;

import java.util.Deque;
import java.util.LinkedList;
import net.minecraft.util.math.BlockPos;

public class DeathHistoryManager {

	public static DeathHistoryManager INSTANCE;
	private Deque<BlockPos> deaths;

	public static void init() {
		INSTANCE = new DeathHistoryManager();
	}

	public DeathHistoryManager() {
		this.deaths = new LinkedList<BlockPos>();
	}

	public void addDeath(BlockPos pos) {
		if (this.deaths.size() == 5) {
			this.deaths.removeLast();
		}
		this.deaths.push(pos);
		this.deaths.forEach(i -> System.out.println(i.toString()));
	}

	public Deque<BlockPos> getRecentDeaths() {
		return this.deaths;
	}

}
