package me.flail.slashplayer.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.slashplayer.SlashPlayer;

public class BaseUtilities extends LegacyUtils {
	protected SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	protected ItemStack addTag(ItemStack item, String key, String tag) {
		return addLegacyTag(item, key, tag);
	}

	protected ItemStack removeTag(ItemStack item, String key, String tag) {
		return removeLegacyTag(item, key, tag);
	}

	protected String getTag(ItemStack item, String key) {
		return getLegacyTag(item, key);
	}

	protected boolean hasTag(ItemStack item, String key) {
		return hasLegacyTag(item, key);
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
