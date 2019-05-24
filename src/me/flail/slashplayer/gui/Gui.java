package me.flail.slashplayer.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Gui extends Logger {
	private Inventory inv;
	private GeneratedGui data;


	public Gui(GeneratedGui data) {
		this.data = data;
		inv = data.generatedInv();
	}

	public GeneratedGui data() {
		return data;
	}

	@Override
	public Gui clone() {
		return new Gui(data);
	}

	public void setItem(int slot, ItemStack item) {
		inv.setItem(slot, item);
	}

	public ItemStack getHeader() {
		int headerSlot = data.dataFile().getNumber("HeaderSlot") - 1;
		if (headerSlot > -1) {

			return inv.getItem(headerSlot);
		}
		return null;
	}

	public Gui setHeader(User subject) {
		int headerSlot = data.dataFile().getNumber("HeaderSlot") - 1;
		inv.setItem(headerSlot, subject.headerItem());
		return this;
	}

	public Gui setTitle(String title) {
		inv = Bukkit.createInventory(null, data.guiSet.size(), chat(title));

		for (Integer i : data.slots()) {
			inv.setItem(i.intValue(), data.get(i));
		}

		return this;
	}

	public void open(User operator, User subject) {
		if (subject != null) {
			this.setHeader(subject);
			inv = this.updateItemPlaceholders(inv, subject.commonPlaceholders());
		}

		plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			operator.player().openInventory(inv);
			plugin.openGuis.put(operator.uuid(), this);
		}, 2L);


	}

}
