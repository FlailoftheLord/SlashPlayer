package me.flail.SlashPlayer.GUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities;

public class PlayerListInventory {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Utilities chat = new Utilities();

	public ItemStack playerSkull(Player p) {

		ItemStack pSkull = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta sMeta = (SkullMeta) pSkull.getItemMeta();

		sMeta.setOwningPlayer(p);

		sMeta.setDisplayName(chat.m("&a" + p.getName()));

		List<String> sLore = new ArrayList<>();

		sLore.add(chat.m("&7click to manage"));
		sLore.add(chat.m("&a" + p.getName()));

		sMeta.setLore(sLore);

		pSkull.setItemMeta(sMeta);

		return pSkull;

	}

	public Inventory playerList() {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		String invTitle = config.getString("PlayerListTitle");

		Inventory pList = Bukkit.createInventory(null, 54, chat.m(invTitle));

		int startSlot = 0;

		Iterator<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers().iterator();

		Player p;

		try {

			while (onlinePlayers.hasNext()) {

				p = onlinePlayers.next();

				pList.setItem(startSlot, playerSkull(p));
				startSlot += 1;

			}

			boolean emptySpace = config.getBoolean("FillEmptySpace");

			if (emptySpace) {

				for (int emptySlot = pList.firstEmpty() + 1; emptySlot <= pList.getSize(); emptySlot++) {

					String fI = guiConfig.getString("FillerItem").toUpperCase();

					if (Material.matchMaterial(fI) != null) {

						ItemStack fillItem = new ItemStack(Material.getMaterial(fI));

						ItemMeta fMeta = fillItem.getItemMeta();

						fMeta.setDisplayName(" ");

						fillItem.setItemMeta(fMeta);

						pList.setItem(emptySlot - 1, fillItem);

					} else {

						ItemStack fillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

						ItemMeta fMeta = fillItem.getItemMeta();

						fMeta.setDisplayName(" ");

						fillItem.setItemMeta(fMeta);

						pList.setItem(emptySlot - 1, fillItem);

					}

				}

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			e.fillInStackTrace();
		}

		return pList;

	}

}
