package me.flail.slashplayer.gui;

import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.user.User;

public class Gui extends BaseGui {

	protected Gui(User user) {
		super(user, 45, "SlashPlayer GUI");
	}

	protected Gui(User user, int size) {
		super(user, size, "SlashPlayer GUI");
	}

	protected Gui(User user, int size, String title) {
		super(user, size, title);
	}

	public Gui setItem(ItemStack item, int slot) {
		inv.setItem(slot, item);
		return this;
	}

	public void open() {
		user.player().openInventory(inv);
	}

}
