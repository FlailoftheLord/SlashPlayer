package me.flail.slashplayer.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.slashplayer.executables.Executables.Exe;

public class CommonUtilities extends BaseUtilities {

	protected String chat(String message) {
		message = message.toString();
		message = message.replace("%prefix%",
				plugin.config.get("Prefix", "").toString()).replace("%website%", plugin.config.get("Website", "").toString());

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
		if (!placeholders.isEmpty() && (message != null)) {
			for (String p : placeholders.keySet()) {
				message = message.replace(p, placeholders.get(p));
			}
		}
		return chat(message);
	}

	public ItemStack itemPlaceholders(ItemStack item, Map<String, String> placeholders) {
		ItemMeta meta = item.getItemMeta();
		if (meta.hasLore()) {
			List<String> lore = meta.getLore();

			List<String> newLore = new ArrayList<>();
			for (String line : lore) {
				newLore.add(this.placeholders(line, placeholders));
			}
			meta.setLore(newLore);
			meta.setDisplayName(this.placeholders(meta.getDisplayName(), placeholders));

			item.setItemMeta(meta);
		}

		return item;
	}

	public Inventory updateItemPlaceholders(Inventory inv, Map<String, String> placeholders) {
		if (inv != null) {
			for (ItemStack item : inv.getContents()) {
				item = this.itemPlaceholders(item, placeholders);
			}
		}

		return inv;
	}

	public ItemStack createItem(DataFile file, String itemKey) {
		String itemType = file.getValue(itemKey + ".Item").toUpperCase().replaceAll("[0-9]", "");
		String itemName = chat(file.getValue(itemKey + ".Name"));
		List<String> itemLore = file.getList(itemKey + ".Lore");
		Exe exe = Exe.get(file.getValue(itemKey + ".Execute"));
		boolean glowing = file.getBoolean(itemKey + ".Glowing");
		boolean unbreakable = file.getBoolean(itemKey + ".Unbreakable");
		boolean closeAfterClick = file.getBoolean(itemKey + ".CloseInventory");
		// int durability = file.getNumber(itemKey + ".Durability");

		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();

		Material type = Material.matchMaterial(itemType);
		if (type != null) {
			item.setType(type);
		}

		List<String> lore = new ArrayList<>();

		for (String line : itemLore) {
			lore.add(chat(line));
		}
		meta.setLore(lore);

		meta.setDisplayName(itemName);
		meta.setUnbreakable(unbreakable);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (glowing) {
			meta.addEnchant(Enchantment.MENDING, 69, glowing);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		item.setItemMeta(meta);



		item = this.addTag(item, "close-after-click", closeAfterClick + "");
		item = this.addTag(item, "execute", exe.toString());

		return item;
	}

	public boolean msgCheck(String message, String text, String type) {
		switch (type.toLowerCase()) {
		case "starts":
			return message.startsWith(text.toLowerCase());
		case "ends":
			return message.endsWith(text.toLowerCase());
		case "contains":
			return message.contains(text.toLowerCase());
		default:
			return false;

		}
	}

	public String replaceText(String message, String text, String replacement) {
		return message = message.replaceAll("(?i)" + Pattern.quote(text), replacement);
	}

	public String convertArray(String[] values) {
		StringBuilder builder = new StringBuilder();
		for (String s : values) {
			builder.append(s + " ");
		}

		return builder.toString();
	}

}
