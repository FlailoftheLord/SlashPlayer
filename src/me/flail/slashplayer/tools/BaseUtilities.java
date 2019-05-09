package me.flail.slashplayer.tools;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.flail.slashplayer.SlashPlayer;

public class BaseUtilities {
	protected SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	protected ItemStack addTag(ItemStack item, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(plugin, "SlashPlayer-GuiItemTag");

		PersistentDataType<String, String> data = PersistentDataType.STRING;

		meta.getPersistentDataContainer().set(key, data, tag);

		item.setItemMeta(meta);
		return item;
	}

	protected ItemStack removeTag(ItemStack item, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(plugin, "SlashPlayer-GuiItemTag");

		meta.getPersistentDataContainer().remove(key);

		item.setItemMeta(meta);
		return item;
	}

	protected boolean hasTag(ItemStack item, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(plugin, "SlashPlayer-GuiItemTag");

		PersistentDataType<String, String> data = PersistentDataType.STRING;

		try {
			return meta.getPersistentDataContainer().has(key, data);
		} catch (Throwable t) {
			return false;
		}
	}

}
