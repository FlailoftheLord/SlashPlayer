package me.flail.slashplayer.gui;

import org.bukkit.Bukkit;
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

	protected BaseGui(User user, int size, String title) {
		this.user = user;
		inv = Bukkit.createInventory(null, size, chat(title));
	}

}
