package me.flail.slashplayer.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class GeneratedGui extends Logger {

	private DataFile file;
	private Map<Integer, ItemStack> guiSet = new HashMap<>();

	public GeneratedGui(DataFile gui, Map<Integer, ItemStack> itemSet) {
		file = gui;
		guiSet.clear();
		guiSet.putAll(itemSet);
	}

	public String name() {
		return file.name();
	}

	public Set<Integer> slots() {
		return guiSet.keySet();
	}

	public ItemStack get(Integer slot) {
		return guiSet.get(slot);
	}

	public boolean create() {
		return false;
	}

	public Inventory generatedInv() {
		Inventory inv = Bukkit.createInventory(null, guiSet.size() - 1, chat(file.getValue("Title")));


		return inv;
	}

	public GeneratedGui setHeader(User user) {
		guiSet.put(Integer.valueOf(file.getNumber("HeaderSlot")), user.headerItem());

		return this;
	}

}
