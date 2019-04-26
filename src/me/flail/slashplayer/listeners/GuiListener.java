package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.flail.slashplayer.SlashPlayer;

public class GuiListener implements Listener {
	private SlashPlayer plugin = SlashPlayer.instance;

	@EventHandler
	public void invClose(InventoryCloseEvent event) {

	}

}
