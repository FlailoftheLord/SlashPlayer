package me.flail.slashplayer.listeners;

import java.util.Locale;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class CommandListener extends Logger implements Listener {

	@EventHandler
	public void handleAliases(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage().toLowerCase(Locale.ENGLISH);

		if (message.equals("/sp") || message.equals("/player") || message.startsWith("/sp ") || message.startsWith("/player ")) {
			message = message.replaceAll("/sp", "/slashplayer").replaceAll("/player", "/slashplayer");
		}

		if (message.equals("/reports") || message.startsWith("/reports ")) {
			message = message.replace("/reports", "/slashplayer reports");
		}

		for (User user : plugin.players.values()) {
			String pName = user.name().toLowerCase();

			if (message.startsWith("/" + pName)) {
				message = message.replaceAll("(?i)" + Pattern.quote("/" + pName), "/slashplayer " + pName);
				break;
			}

			if (message.equals("/report") || message.startsWith("/report ")) {
				message = message.replace("/report", "/slashplayer report");
				break;
			}

		}

		event.setMessage(message);
	}

}
