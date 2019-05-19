package me.flail.slashplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class CommandListener extends Logger implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void cmdProcess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		String[] cmdAliases = { "/sp ", "/player " };
		String[] reportAliases = { "/report ", "/reports " };


		for (String alias : cmdAliases) {
			if (msgCheck(message, alias, "starts")) {
				message = replaceText(message, alias, "/slashplayer ");
				break;
			}
		}

		for (String alias : reportAliases) {
			if (msgCheck(message, alias, "starts")) {
				message = replaceText(message, alias, "/slashplayer " + alias.replace("/", ""));
				break;
			}
		}

		for (User user : plugin.players) {
			String name = user.name();
			if (this.msgCheck(message, "/" + name + " ", "starts")) {
				message = this.replaceText(message, "/" + name + " ", "/slashplayer " + name + " ");
				break;
			}
		}

		event.setMessage(message);
	}

}
