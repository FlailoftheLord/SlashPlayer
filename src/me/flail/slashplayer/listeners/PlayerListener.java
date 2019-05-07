package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.user.User;

public class PlayerListener implements Listener {
	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerLogin(PlayerLoginEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		if (user.isBanned()) {
			String banMsg = user.getBanMessage();
			event.disallow(Result.KICK_BANNED, banMsg);
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		plugin.players.add(user);
		user.setup(plugin.verbose);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerLeave(PlayerQuitEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		user.logout();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDisconnect(PlayerKickEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		user.logout();
	}

}
