package me.flail.slashplayer.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class PlayerListener extends Logger implements Listener {
	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerLogin(PlayerLoginEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		if (user.isBanned()) {
			String banMsg = user.getBanMessage().toSingleString();
			event.disallow(Result.KICK_BANNED, banMsg);
			user.logout();
			return;
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		User user = new User(event.getPlayer().getUniqueId());
		user.setup(plugin.verbose);
		plugin.players.put(user.uuid(), user);
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
		if ((event.getRightClicked() instanceof Player) && operator.hasPermission("slashplayer.interactopen")) {
			ItemStack item = operator.player().getInventory().getItemInMainHand();
			if ((item == null) || (item.getType() == Material.AIR) || (item.getType() == Material.GOLDEN_AXE)) {
				Player player = (Player) event.getRightClicked();
				User subject = new User(player.getUniqueId());

				new GuiControl().openModerationGui(operator, subject);
			}

		}

	}

	@EventHandler
	public void playerDamage(EntityDamageByEntityEvent event) {
		Entity damaged = event.getEntity();
		Entity damager = event.getDamager();

		if (damaged instanceof Player) {
			int range = plugin.config.getInt("FriendProtectionRange", 3);

			if ((damager instanceof LivingEntity) && damager.hasMetadata("SlashPlayerFrend")) {
				event.setCancelled(true);
				return;
			}

			if (range > 0) {
				List<Entity> nearby = damaged.getNearbyEntities(range, range, range);
				if (!nearby.isEmpty() && (nearby.get(0) != null)) {
					for (Entity e : nearby) {
						if ((e instanceof LivingEntity) && e.hasMetadata("SlashPlayerFrend-" + damaged.getName())) {
							event.setCancelled(true);
							damaged.setInvulnerable(true);

							damager.remove();
							return;
						}

					}

				}
			}

		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		int range = plugin.config.getInt("FriendProtectionRange", 3);
		List<Entity> nearby = player.getNearbyEntities(range, range, range);
		for (Entity e : nearby) {
			if ((e instanceof LivingEntity) && e.hasMetadata("SlashPlayerFrend-" + player.getName())) {
				player.setInvulnerable(true);
				return;
			}

		}

		player.setInvulnerable(false);
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());
		if (subject.isMuted()) {
			if (subject.hasPermission("slashplayer.exempt")) {
				return;
			}
			event.setCancelled(true);

		}

	}


}
