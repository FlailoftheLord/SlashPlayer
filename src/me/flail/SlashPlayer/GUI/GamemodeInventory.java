package me.flail.SlashPlayer.GUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class GamemodeInventory {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	public Inventory gamemodeInv(Player player) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		ItemStack pHead = new PlayerInfoInventory().pHead(player, false);

		String gmInvTitle = guiConfig.getString("GamemodeInventory.Title");

		Inventory gmInv = Bukkit.createInventory(player, 36, chat.m(gmInvTitle).replace("%player%", player.getName()));

		int slot = 0;

		while (slot <= gmInv.getSize()) {

			ConfigurationSection sCheck = guiConfig.getConfigurationSection("GamemodeInventory." + slot);

			if (sCheck != null) {

				String item = sCheck.getString("Item").toUpperCase();

				ItemStack slotItem;

				ItemMeta itemMeta;

				if (item != null) {

					List<String> lore = new ArrayList<>();

					List<String> itemLore = sCheck.getStringList("Lore");

					String itemName = sCheck.getString("Name");

					slotItem = new ItemStack(Material.getMaterial(item));
					itemMeta = slotItem.getItemMeta();

					itemMeta.addEnchant(Enchantment.MENDING, slot, true);

					itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

					if (itemLore != null) {

						for (String s : itemLore) {
							lore.add(chat.m(s).replace("%player%", player.getName()));

						}

						itemMeta.setLore(lore);

					}

					if (itemName != null) {

						itemMeta.setDisplayName(chat.m(itemName).replace("%player%", player.getName()));

					}

					slotItem.setItemMeta(itemMeta);

					gmInv.setItem(slot - 1, slotItem);

				}

			} else {

				boolean fillSpace = plugin.getConfig().getBoolean("FillEmptySpace");

				if (fillSpace) {

					String fI = guiConfig.getString("FillerItem").toUpperCase();

					if (Material.matchMaterial(fI) != null) {

						ItemStack fillItem = new ItemStack(Material.getMaterial(fI));

						ItemMeta fMeta = fillItem.getItemMeta();

						fMeta.setDisplayName(" ");

						fillItem.setItemMeta(fMeta);

						if ((slot > 0) && (slot <= gmInv.getSize())) {

							gmInv.setItem(slot - 1, fillItem);

						}

					} else {

						ItemStack fillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

						ItemMeta fMeta = fillItem.getItemMeta();

						fMeta.setDisplayName(" ");

						fillItem.setItemMeta(fMeta);

						if ((slot > 0) && (slot <= gmInv.getSize())) {

							gmInv.setItem(slot - 1, fillItem);

						}

					}

				}

			}

			slot += 1;
		}

		int hSlot = guiConfig.getInt("GamemodeInventory.Header.Slot");

		if ((hSlot <= gmInv.getSize()) && (hSlot >= 1)) {
			gmInv.setItem(hSlot - 1, pHead);
		} else {
			gmInv.setItem(4, pHead);
		}

		return gmInv;

	}

}
