package me.flail.SlashPlayer.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.Utilities.Tools;
import me.flail.oldSlashPlayer.SlashPlayer;

@SuppressWarnings("deprecation")
public class PlayerListGui implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void invetnoryClick(InventoryClickEvent event) {

		FileConfiguration config = plugin.getConfig();

		Player player = (Player) event.getWhoClicked();

		String invName = event.getInventory().getTitle();

		String pListInvTitle = chat.m(config.getString("PlayerListTitle"));

		if (invName.equals(pListInvTitle)) {

			ItemStack item = event.getCurrentItem();

			if (item.hasItemMeta()) {

				String itemName = item.getItemMeta().getDisplayName();

				if (itemName != null) {

					for (Player p : plugin.players.values()) {

						String pName = ChatColor.stripColor(itemName);

						if (pName.equalsIgnoreCase(p.getName())) {

							Inventory pMenu = new PlayerInfoInventory().playerInfo(p);

							player.openInventory(pMenu);
							break;

						}

					}

				}

			}

			event.setCancelled(true);
		}

	}

}
