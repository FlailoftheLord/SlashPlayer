package me.flail.slashplayer.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class GuiListener extends Logger implements Listener {
	private SlashPlayer plugin = SlashPlayer.instance;

	@EventHandler(priority = EventPriority.HIGH)
	public void invClick(InventoryClickEvent event) {
		try {

			if (event.getWhoClicked() instanceof Player) {
				Player player = (Player) event.getWhoClicked();

				User user = new User(player.getUniqueId());
				if (plugin.openGuis.containsKey(user.uuid())) {
					event.setCancelled(true);

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						user.closeGui();
						return;
					}

					Gui gui = plugin.openGuis.get(user.uuid());

					new GuiControl().processClick(user, gui, event.getCurrentItem());
				}

			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void invClose(InventoryCloseEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		plugin.openGuis.remove(user.uuid());
	}

}
