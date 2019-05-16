package me.flail.slashplayer.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class GuiListener extends Logger implements Listener {
	private SlashPlayer plugin = SlashPlayer.instance;

	@EventHandler(priority = EventPriority.HIGH)
	public void invClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();

			User user = new User(player.getUniqueId());
			if (plugin.openGuis.containsKey(user.uuid())) {
				event.setResult(Result.DENY);

				new GuiControl().processClick(user, event.getCurrentItem());
			}

		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void invClose(InventoryCloseEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		plugin.openGuis.remove(user.uuid());
	}

}
