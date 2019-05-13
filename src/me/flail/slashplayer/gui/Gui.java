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
		inv.setItem(slot - 1, item);
	}

	public void open(User operator, User subject) {
		if (subject != null) {
			data = data.setHeader(subject);
		}
		operator.player().openInventory(data.generatedInv());
		plugin.openGuis.put(operator, this);
	}

}
