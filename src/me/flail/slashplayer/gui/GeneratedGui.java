package me.flail.slashplayer.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.tools.Logger;

public class GeneratedGui extends Logger {

	private Map<Gui, Map<Integer, ItemStack>> guiSet = new HashMap<>();

	public GeneratedGui(Gui gui, Map<Integer, ItemStack> itemSet) {
		Map<Integer, ItemStack> items = new HashMap<>();
		guiSet.clear();
		items.clear();
		items.putAll(itemSet);
		guiSet.put(gui, items);
	}


	public boolean create() {
		return plugin.loadedGuis.add(this);
	}

}
