package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class InventoryBackupListener extends Logger implements Listener {

	protected InventoryBackupListener() {
	}

	public static void load() {

		plugin.server.getPluginManager().registerEvents(new InventoryBackupListener(), plugin);
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		User user = new User(event.getEntity());
		user.manualInventoryBackup(event.getDrops());

	}

	public static class InventoryBackups {
		public static boolean death = true;
		public static boolean login = true;
		public static boolean logout = true;
		public static boolean worldChange = true;

	}

}
