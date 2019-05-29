package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.user.User;

public class FreezeListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void freezeListen(PlayerMoveEvent moveEvent, PlayerInteractEvent interactEvent, AsyncPlayerChatEvent chatEvent) {
		User subject;
		Message freezeMove = new Message("FreezeMove");
		Message freezeInteract = new Message("FreezeInteract");
		Message freezeOther = new Message("FreezeOther");

	}

}
