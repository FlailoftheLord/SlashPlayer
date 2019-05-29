package me.flail.slashplayer.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.user.User;

public class FreezeListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void freezeListen(PlayerMoveEvent moveEvent, PlayerInteractEvent interactEvent, AsyncPlayerChatEvent chatEvent,
			InventoryOpenEvent invOpen, InventoryCloseEvent invClose) {
		User subject;

		subject = new User(moveEvent.getPlayer().getUniqueId());
		moveEvent(moveEvent, subject);

		subject = new User(interactEvent.getPlayer().getUniqueId());
		interactEvent(interactEvent, subject);

		subject = new User(chatEvent.getPlayer().getUniqueId());
		chatEvent(chatEvent, subject);

	}

	private void moveEvent(PlayerMoveEvent event, User subject) {
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
				Player player = subject.player();
				if ((fromX != toX) || (fromZ != toZ)) {
					event.setCancelled(true);
					freezeMove.send(subject, null);
				}

				if (fromY != toY) {
					Block floor = from.getBlock().getRelative(0, -2, 0);

				}

			}

		}

	}

	private void interactEvent(PlayerInteractEvent event, User subject) {
		Message freezeInteract = new Message("FreezeInteract");

	}

	private void chatEvent(AsyncPlayerChatEvent event, User subject) {
		Message freezeOther = new Message("FreezeOther");


	}

}
