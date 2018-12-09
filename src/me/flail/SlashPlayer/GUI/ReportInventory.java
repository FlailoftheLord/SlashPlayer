package me.flail.SlashPlayer.GUI;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;

public class ReportInventory {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools tools = new Tools();

	private ItemStack pHead(Player player) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		String nameFormat = tools.msg(guiConfig.getString("ReportGui.ReportList.Name"), player, player, "ReportGui",
				"report");
		List<String> lore = guiConfig.getStringList("ReportGui.ReportList.Lore");

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		headMeta.setOwningPlayer(player);

		headMeta.setDisplayName(nameFormat);

		return head;
	}

	public Inventory reportInv(Player operator) {

		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration guiConfig = plugin.getGuiConfig();

		String fillerItem = guiConfig.getString("FillerItem");

		String invType = guiConfig.getString("ReportGui.InventoryType").toUpperCase();
		String invTitle = tools.msg(guiConfig.getString("ReportGui.InventoryTitle"), operator, operator, "ReportPlayer",
				"report");

		Inventory reportInv = Bukkit.createInventory(operator, InventoryType.valueOf(invType), invTitle);

		for (int slot = 0; slot <= reportInv.getSize(); slot++) {

			for (Player p : plugin.players.values()) {

				if (p != null) {

					reportInv.setItem(slot, pHead(p));

				} else {
					continue;
				}

			}

		}

		return reportInv;

	}

}
