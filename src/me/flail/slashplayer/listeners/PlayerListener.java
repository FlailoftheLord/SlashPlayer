package me.flail.slashplayer.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
			user.logout();
			return;
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		user.setup(plugin.verbose);
		plugin.players.add(user);
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

	@EventHandler
	public void playerInteract(PlayerInteractAtEntityEvent event) {
		User operator = new User(event.getPlayer().getUniqueId());
		if (event.getRightClicked() instanceof Player) {
			ItemStack item = operator.player().getInventory().getItemInMainHand();
			if ((item == null) || (item.getType() == Material.AIR)) {
				Player player = (Player) event.getRightClicked();
				if (operator.player().hasPermission("slashplayer.command")) {
					User subject = new User(player.getUniqueId());

				}

			}

		}

	}

}
