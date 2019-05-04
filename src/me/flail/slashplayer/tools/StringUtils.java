package me.flail.slashplayer.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;

public class StringUtils extends BaseUtilities {

	protected String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	protected String formatTime(Date date) {
		return new SimpleDateFormat("MMMMMMMMMMMMMM dd, yyyy @ HH mm:ss").format(date);
	}

}
