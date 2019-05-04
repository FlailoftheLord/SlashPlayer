package me.flail.slashplayer.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

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
		Player player = event.getPlayer();
		User user = new User(player.getUniqueId());
		plugin.players.add(user);
		user.setup(plugin.verbose);
	}

}
