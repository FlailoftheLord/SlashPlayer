package me.flail.slashplayer.tools;

import org.bukkit.ChatColor;

public class StringUtils extends BaseUtilities {

	protected String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
