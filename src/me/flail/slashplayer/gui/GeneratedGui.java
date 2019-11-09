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

		title = file.getValue("Title");
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

	public int guiSize() {
		int size = 54;

		if (!file.hasValue("Size")) {
			return size;
		}

		for (int s : new int[] { 9, 18, 27, 36, 45, 54 }) {
			if (file.getNumber("Size") <= s) {

				size = s;
				break;
			}
		}

		return size;
	}

	public ItemStack get(Integer slot) {
		return guiSet.get(slot);
	}

	public boolean create(String name) {
		plugin.loadedGuis.remove(name, this);
		return plugin.loadedGuis.put(name, this) != null;
	}

	public Inventory generatedInv(String action) {
		Inventory inv = Bukkit.createInventory(null, guiSize(), chat(title));

		for (Integer i : slots()) {
			if (i >= inv.getSize()) {
				break;
			}
			inv.setItem(i.intValue(), get(i));
		}

		return inv;
	}



	public GeneratedGui setHeader(User user) {
		guiSet.put(Integer.valueOf(file.getNumber("HeaderSlot") - 1), user.headerItem());

		return this;
	}

}
