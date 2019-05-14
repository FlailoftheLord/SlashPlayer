package me.flail.slashplayer.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StringUtils extends BaseUtilities {

	protected String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	protected String formatTime(Date date) {
		return new SimpleDateFormat("MMMMMMMMMMMMMM dd, yyyy @ HH mm:ss").format(date);
	}

	/**
	 * Converts a string, by translating the following placeholders with their counterparts defined in
	 * the provided Map of placeholders.
	 * 
	 * @param message
	 * @param placeholders
	 *                         Formatted as
	 *                         <code>{@literal Map<String placeholder, String value>}</code>
	 * @return the new String.
	 */
	public String placeholders(String message, Map<String, String> placeholders) {
		for (String p : placeholders.keySet()) {
			message = message.replace(p, placeholders.get(p));
		}
		return message;
	}

	public ItemStack itemPlaceholders(ItemStack item, Map<String, String> placeholders) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();

		List<String> newLore = new ArrayList<>();
		for (String line : lore) {
			newLore.add(this.placeholders(line, placeholders));
		}
		meta.setLore(newLore);
		meta.setDisplayName(this.placeholders(meta.getDisplayName(), placeholders));

		item.setItemMeta(meta);

		return item;
	}

}
