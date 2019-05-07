package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.tools.Logger;

public class GuiListener extends Logger implements Listener {
	private SlashPlayer plugin = SlashPlayer.instance;

	@EventHandler(priority = EventPriority.HIGH)
	public void invClick(InventoryInteractEvent event) {

	}

	@EventHandler
	public void invClose(InventoryCloseEvent event) {

	}

}
