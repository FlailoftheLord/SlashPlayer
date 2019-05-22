package me.flail.slashplayer.tools;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import me.flail.slashplayer.SlashPlayer;

public class LegacyUtils {
	protected SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	protected ItemStack addLegacyTag(ItemStack item, String key, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		meta.getCustomTagContainer().setCustomTag(nkey, ItemTagType.STRING, tag);

		item.setItemMeta(meta);
		return item;
	}

	protected ItemStack removeLegacyTag(ItemStack item, String key, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		meta.getCustomTagContainer().removeCustomTag(nkey);

		item.setItemMeta(meta);
		return item;
	}

	protected String getLegacyTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		if (hasLegacyTag(item, key)) {
			return meta.getCustomTagContainer().getCustomTag(nkey, ItemTagType.STRING);
		}

		return "null";
	}

	protected boolean hasLegacyTag(ItemStack item, String key) {
		if (item.hasItemMeta()) {

			ItemMeta meta = item.getItemMeta();
			NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

			try {
				return meta.getCustomTagContainer().hasCustomTag(nkey, ItemTagType.STRING) ? true : false;
			} catch (Throwable t) {
				return false;
			}

		}
		return false;
	}

}
