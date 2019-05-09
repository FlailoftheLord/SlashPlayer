package me.flail.slashplayer.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

/**
 * Dummy GUI class
 * 
 * @author FlailoftheLord
 */
public class BaseGui extends Logger {
	protected Inventory inv;
	protected User user;
	private String title;

	protected BaseGui(User user, int size, String title) {
		this.user = user;
		this.title = title;
		inv = Bukkit.createInventory(null, size, chat(title));
	}

	protected BaseGui setType(InventoryType type) {
		inv = Bukkit.createInventory(user.player(), type, title);
		return this;
	}

	protected BaseGui setSubject(User user) {
		inv = Bukkit.createInventory(user.player(), inv.getSize(), title);
		return this;
	}

}
