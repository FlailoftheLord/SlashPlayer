package me.flail.slashplayer.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.tools.Logger;

public class GeneratedGui extends Logger {

	private String name;
	private Map<Integer, ItemStack> guiSet = new HashMap<>();

	public GeneratedGui(String guiName, Map<Integer, ItemStack> itemSet) {
		name = guiName;
		guiSet.clear();
		guiSet.putAll(itemSet);
	}

	public String name() {
		if (!name.endsWith(".yml")) {
			name = name.concat(".yml");
		}
		return name;
	}

	public Set<Integer> slots() {
		return guiSet.keySet();
	}

	public ItemStack get(Integer slot) {
		return guiSet.get(slot);
	}

	public boolean create() {
		return plugin.loadedGuis.add(this);
	}

}
