package me.flail.SlashPlayer.GUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class PlayerInfoInventory {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	public ItemStack pHead(OfflinePlayer player) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration pData = plugin.getPlayerData();

		String hColor = guiConfig.getString("Header.NameColor");

		ItemStack header = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta headerM = (SkullMeta) header.getItemMeta();

		String pUuid = player.getUniqueId().toString();

		boolean isFrozen = pData.getBoolean(pUuid + ".IsFrozen");

		String frozenStat = "false";

		if (isFrozen) {
			frozenStat = "true";
		} else {
			frozenStat = "false";
		}

		boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

		String muteStat = "false";

		if (isMuted) {
			muteStat = "true";
		} else {
			muteStat = "false";
		}

		List<String> hLore = new ArrayList<>();

		List<String> hL = guiConfig.getStringList("Header.Info");

		String pHealth = 20 + "";

		if (player.isOnline()) {
			pHealth = player.getPlayer().getHealth() + "";
		}

		String playerGamemode = pData.getString(pUuid + ".Gamemode");

		if (player.isOnline()) {
			playerGamemode = player.getPlayer().getGameMode().toString();
		}

		headerM.setOwningPlayer(player);

		if (hColor != null) {
			headerM.setDisplayName(chat.m(hColor + player.getName()));
		} else {
			headerM.setDisplayName(chat.m("&a" + player.getName()));
		}

		hLore.add(chat.m("&7" + pUuid));

		if (hL != null) {

			for (String l : hL) {
				hLore.add(chat.m(l).replace("%player_gamemode%", playerGamemode).replace("%frozen_status%", frozenStat)
						.replace("%mute_status%", muteStat).replace("%player_health%", pHealth));
			}
			headerM.setLore(hLore);
		}

		header.setItemMeta(headerM);

		return header;

	}

	public Inventory playerInfo(OfflinePlayer player) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		Inventory pInfo = null;

		if (player != null) {

			String pInfoTitle = chat
					.m(config.getString("PlayerMenuTitle").replace("%player%", player.getName()) + "  &c&l(Offline)");

			if (player.isOnline()) {
				pInfoTitle = chat.m(
						config.getString("PlayerMenuTitle").replace("%player%", player.getName()) + "  &a&l(Online)");
			}

			if (player.isOnline()) {
				pInfo = Bukkit.createInventory(player.getPlayer(), 45, pInfoTitle);
			} else {
				pInfo = Bukkit.createInventory(null, 45, pInfoTitle);
			}

			int slot = 0;

			while (slot <= pInfo.getSize()) {

				ConfigurationSection sCheck = guiConfig.getConfigurationSection("PlayerInfo." + slot);

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

						pInfo.setItem(slot - 1, slotItem);

					}

				} else {

					boolean fillSpace = config.getBoolean("FillEmptySpace");

					if (fillSpace) {

						String fI = guiConfig.getString("FillerItem").toUpperCase();

						if (Material.matchMaterial(fI) != null) {

							ItemStack fillItem = new ItemStack(Material.getMaterial(fI));

							ItemMeta fMeta = fillItem.getItemMeta();

							fMeta.setDisplayName(" ");

							fillItem.setItemMeta(fMeta);

							if ((slot > 0) && (slot <= pInfo.getSize())) {

								pInfo.setItem(slot - 1, fillItem);

							}

						} else {

							ItemStack fillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

							ItemMeta fMeta = fillItem.getItemMeta();

							fMeta.setDisplayName(" ");

							fillItem.setItemMeta(fMeta);

							if ((slot > 0) && (slot <= pInfo.getSize())) {

								pInfo.setItem(slot - 1, fillItem);

							}

						}

					}

				}

				slot += 1;
			}

			int hSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			if ((hSlot <= pInfo.getSize()) && (hSlot >= 1)) {
				pInfo.setItem(hSlot - 1, pHead(player));
			} else {
				pInfo.setItem(4, pHead(player));
			}

		}

		return pInfo;

	}

}
