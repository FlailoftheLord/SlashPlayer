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
	protected String title;
	protected Map<Integer, ItemStack> guiSet = new HashMap<>();

	public GeneratedGui(DataFile gui, Map<Integer, ItemStack> itemSet) {
		file = gui;
		guiSet.clear();
		guiSet.putAll(itemSet);
	}

	public DataFile dataFile() {
		return file;
	}

	/**
	 * The name of the source file for this GUI's configuration.
	 */
	public String name() {
		return file.name();
	}

	/**
	 * @return The title shown for this GUI
	 */
	public String title() {
		return title;
	}

	public Set<Integer> slots() {
		return guiSet.keySet();
	}

	public ItemStack get(Integer slot) {
		return guiSet.get(slot);
	}

	public boolean create(String name) {
		plugin.loadedGuis.remove(name, this);
		return plugin.loadedGuis.put(name, this) != null;
	}

	public Inventory generatedInv() {
		title = file.getValue("Title");
		Inventory inv = Bukkit.createInventory(null, guiSet.size(), chat(title));
		for (Integer i : slots()) {
			inv.setItem(i.intValue(), get(i));
		}

		return inv;
	}



	public GeneratedGui setHeader(User user) {
		guiSet.put(Integer.valueOf(file.getNumber("HeaderSlot") - 1), user.headerItem());

		return this;
	}

}
