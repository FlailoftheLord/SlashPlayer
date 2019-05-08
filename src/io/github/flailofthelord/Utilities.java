package io.github.flailofthelord;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Utilities {
	protected Class<? extends JavaPlugin> plugin;

	public Utilities(Class<? extends JavaPlugin> mainPlugin) {
		plugin = mainPlugin;
	}

	public static String chat(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String placeholders(String message, Map<String, String> placeholders) {
		for (String s : placeholders.keySet()) {
			message = message.replace(s, placeholders.get(s));
		}
		return chat(message);
	}

}
