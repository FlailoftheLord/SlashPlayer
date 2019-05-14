package me.flail.slashplayer.tools;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.flail.slashplayer.SlashPlayer;

public class BaseUtilities {
	protected SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	protected ItemStack addTag(ItemStack item, String key, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		PersistentDataType<String, String> data = PersistentDataType.STRING;

		meta.getPersistentDataContainer().set(nkey, data, tag);

		item.setItemMeta(meta);
		return item;
	}

	protected ItemStack removeTag(ItemStack item, String key, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		meta.getPersistentDataContainer().remove(nkey);

		item.setItemMeta(meta);
		return item;
	}

	protected String getTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		PersistentDataType<String, String> data = PersistentDataType.STRING;
		if (hasTag(item, key)) {
			return meta.getPersistentDataContainer().get(nkey, data);
		}

		return "null";
	}

	protected boolean hasTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, "SlashPlayer-" + key);

		PersistentDataType<String, String> data = PersistentDataType.STRING;

		try {
			return meta.getPersistentDataContainer().has(nkey, data);
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * Grabs the color code which modifies the substring <code>before</code> in the string
	 * <code>string</code>
	 * 
	 * @param string
	 * @param before
	 */
	public String getColor(String string, String before) {
		String first = string.split(before)[0];
		char c = first.charAt(first.lastIndexOf("&") + 1);
		return "&" + c;
	}

	public ItemStack fillerItem(DataFile file) {
		ItemStack item = new ItemStack(Material.AIR);
		String filler = new DataFile("GuiConfig.yml").getValue("FillerItem").replaceAll("[0-9]", "").toUpperCase();

		if (file.hasValue("Format.FillerItem")) {
			filler = file.getValue("Format.FillerItem").replaceAll("[0-9]", "").toUpperCase();
		}

		if (Material.matchMaterial(filler) != null) {
			item.setType(Material.matchMaterial(filler));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(" ");
			item.setItemMeta(meta);
		}

		return item;
	}

}
