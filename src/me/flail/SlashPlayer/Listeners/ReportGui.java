package me.flail.SlashPlayer.Listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;

public class ReportGui implements Listener {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		String reportInvTitle = guiConfig.get("ReportGui.InventoryTitle").toString();

		Inventory inv = event.getInventory();

		String invTitle = inv.getTitle();

		InventoryHolder holder = inv.getHolder();

		if (holder instanceof Player) {

			Player invOwner = (Player) holder;

			Player player = (Player) event.getWhoClicked();

			if (invOwner.equals(player)) {

				if (invTitle.equals(reportInvTitle)) {

				}

			}

		}

	}

}
