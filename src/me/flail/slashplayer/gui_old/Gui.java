package me.flail.slashplayer.gui_old;

import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.gui.GeneratedGui;
import me.flail.slashplayer.user.User;

public class Gui extends BaseGui {

	protected Gui(User user) {
		super(user, 54, "SlashPlayer GUI");
	}

	protected Gui(User user, int size) {
		super(user, size, "SlashPlayer GUI");
	}

	public Gui(User user, int size, String title) {
		super(user, size, title);
	}

	public Gui setItem(int slot, ItemStack item) {
		inv.setItem(slot, item);
		return this;
	}

	public User owner() {
		return user;
	}

	public void setOwner(User user) {
		this.user = user;
	}

	public void open(String guiName) {
		for (GeneratedGui ui : plugin.loadedGuis) {
			if (ui.name().equalsIgnoreCase(guiName)) {
				for (Integer slot : ui.slots()) {
					this.setItem(slot.intValue(), ui.get(slot));
				}
			}
		}

		user.player().openInventory(inv);
		plugin.openGuis.add(this);
	}

}
