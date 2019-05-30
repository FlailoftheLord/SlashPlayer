package me.flail.slashplayer.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class FreezeListener extends Logger implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void moveEvent(PlayerMoveEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());

		Message freezeMove = new Message("FreezeMove");

		if (subject.isFrozen()) {
			Location from = event.getFrom();
			Location to = event.getTo();

			int fromX = from.getBlockX();
			int fromY = from.getBlockY();
			int fromZ = from.getBlockZ();
			int toX = to.getBlockX();
			int toY = to.getBlockY();
			int toZ = to.getBlockZ();

			if (subject.isOnline()) {
				if ((fromX != toX) || (fromZ != toZ)) {
					event.setCancelled(true);

					if (!plugin.cooldowns.contains(subject.uuid())) {
						freezeMove.send(subject, null);
						plugin.cooldown(subject, 5);
					}
				}

				if (fromY != toY) {
					if (!subject.player().isOnGround() && !(fromY > toY)) {
						event.setCancelled(true);

						if (!plugin.cooldowns.contains(subject.uuid())) {
							freezeMove.send(subject, null);
							plugin.cooldown(subject, 5);
						}
					}
				}

				return;
			}

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void invOpen(InventoryOpenEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());

		if (subject.isFrozen()) {
			String canInteract = plugin.config.get("Frozen.Interact").toString().toLowerCase();

			if (canInteract.equals("deny")) {
				if (subject.hasPermission("slashplayer.command") && plugin.openGuis.containsKey(subject.uuid())) {
					return;
				}

				event.setCancelled(true);
				Message freezeOther = new Message("FreezeOther");

				if (!plugin.cooldowns.contains(subject.uuid())) {
					freezeOther.send(subject, null);
					plugin.cooldown(subject, 5);
				}

				return;
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void invClose(InventoryCloseEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());

		if (subject.isFrozen()) {
			String canInteract = plugin.config.get("Frozen.Interact").toString().toLowerCase();

			if (canInteract.equals("deny")) {

				if (event.getInventory().getType().equals(InventoryType.CHEST)) {
					if (subject.hasPermission("slashplayer.command") && plugin.openGuis.containsKey(subject.uuid())) {
						return;
					}

					subject.player().openInventory(event.getInventory());

					Message freezeOther = new Message("FreezeOther");

					if (!plugin.cooldowns.contains(subject.uuid())) {
						freezeOther.send(subject, null);
						plugin.cooldown(subject, 5);
					}

					return;
				}

			}

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void interactEvent(PlayerInteractEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());

		if (!event.isCancelled()) {
			if (subject.isFrozen()) {
				String canInteract = plugin.config.get("Frozen.Interact").toString().toLowerCase();

				if (canInteract.equals("deny")) {
					event.setCancelled(true);
					Message freezeInteract = new Message("FreezeInteract");

					if (!plugin.cooldowns.contains(subject.uuid())) {
						freezeInteract.send(subject, null);
						plugin.cooldown(subject, 5);
					}
					return;
				}

			}

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatEvent(AsyncPlayerChatEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());

		if (!event.isCancelled()) {
			if (subject.isFrozen()) {
				String canChat = plugin.config.get("Frozen.Chat").toString().toLowerCase();
				if (canChat.equals("deny")) {
					event.setCancelled(true);
					Message freezeOther = new Message("FreezeOther");

					if (!plugin.cooldowns.contains(subject.uuid())) {
						freezeOther.send(subject, null);
						plugin.cooldown(subject, 5);
					}
					return;
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.LOW)
	public void blockBreak(BlockBreakEvent event) {
		User subject = new User(event.getPlayer().getUniqueId());
		if (subject.isFrozen()) {
			Location loc = subject.player().getLocation();
			if (event.getBlock().getLocation().getBlockY() < loc.getBlockY()) {
				event.setCancelled(true);
			}
		}
	}

}
