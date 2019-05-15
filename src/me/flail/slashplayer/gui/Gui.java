package me.flail.slashplayer.gui;

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

	public Gui setHeader(User subject) {
		data = data.setHeader(subject);
		return this;
	}

	public void open(User operator, User subject) {
		if (subject != null) {
			setHeader(subject);
		}
		Inventory inventory = this.updateItemPlaceholders(inv, subject.commonPlaceholders());

		operator.player().openInventory(inventory);
		plugin.openGuis.put(operator, this);
	}

}
