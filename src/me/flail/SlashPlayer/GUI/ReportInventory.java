package me.flail.SlashPlayer.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class ReportInventory {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools tools = new Tools();

	private ItemStack pHead(Player player) {

		String pUuid = player.getUniqueId().toString();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration reports = plugin.getReportedPlayers();

		String nameFormat = tools.msg(guiConfig.getString("ReportGui.ReportList.Name"), player, player, "ReportGui",
				"report");
		List<String> lore = guiConfig.getStringList("ReportGui.ReportList.Lore");

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		// ItemMeta headMeta = head.getItemMeta();

		List<String> skullLore = new ArrayList<>();
		skullLore.add(tools.m("&8" + pUuid));

		String reasonHeader = tools.msg(guiConfig.get("ReportGui.ReportList.Reason.Name").toString(), player, player,
				"ReportPlayer", "report");
		String reasonColor = guiConfig.get("ReportGui.ReportList.Reason.Color").toString();

		String reportReason = reports.get(pUuid + ".Reason").toString();

		for (String s : lore) {
			skullLore.add(tools.msg(s.replaceAll("%reporter%", reports.get(pUuid + ".Reporter").toString()), player,
					player, "ReportList", "report"));
		}

		skullLore.add(" ");
		skullLore.add(reasonHeader);

		while (reportReason.length() > 16) {
			int lastIndex = reportReason.lastIndexOf(" ", 16);

			if (lastIndex > -1) {
				skullLore.add(tools.m("  " + reasonColor + reportReason.substring(0, lastIndex)));

				reportReason = reportReason.substring(lastIndex + 1, reportReason.length());

			} else {
				skullLore.add(tools.m("  " + reasonColor + reportReason));
				break;
			}

		}

		if (reportReason.length() <= 16) {
			skullLore.add(tools.m("  " + reasonColor + reportReason));
		}

		skullLore.add(" ");
		skullLore.add(tools.m("&8Shift Click to remove."));

		headMeta.setOwningPlayer(player);

		headMeta.setDisplayName(nameFormat + tools.m(" &a&l(Online)"));

		headMeta.setLore(skullLore);

		head.setItemMeta(headMeta);

		return head;
	}

	private ItemStack offlinepHead(OfflinePlayer player, String reporter) {

		String pUuid = player.getUniqueId().toString();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration reports = plugin.getReportedPlayers();

		String nameFormat = tools
				.m(guiConfig.getString("ReportGui.ReportList.Name").replaceAll("%player%", player.getName()));
		List<String> lore = guiConfig.getStringList("ReportGui.ReportList.Lore");

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		// ItemMeta headMeta = head.getItemMeta();

		List<String> skullLore = new ArrayList<>();
		skullLore.add(tools.m("&8" + pUuid));

		String reasonHeader = tools.m(guiConfig.get("ReportGui.ReportList.Reason.Name").toString());
		String reasonColor = guiConfig.get("ReportGui.ReportList.Reason.Color").toString();
		String reportReason = reports.get(pUuid + ".Reason").toString();

		for (String s : lore) {
			skullLore.add(tools.m(s.replaceAll("%reporter%", reports.get(pUuid + ".Reporter").toString())));
		}

		skullLore.add(" ");
		skullLore.add(reasonHeader);

		while (reportReason.length() > 16) {
			int lastIndex = reportReason.lastIndexOf(" ", 16);

			if (lastIndex > -1) {
				skullLore.add(tools.m("  " + reasonColor + reportReason.substring(0, lastIndex)));

				reportReason = reportReason.substring(lastIndex + 1, reportReason.length());

			} else {
				skullLore.add(tools.m("  " + reasonColor + reportReason));
				break;
			}

		}

		if (reportReason.length() <= 16) {
			skullLore.add(tools.m("  " + reasonColor + reportReason));
		}

		skullLore.add(" ");
		skullLore.add(tools.m("&8Shift Click to remove."));

		headMeta.setOwningPlayer(player);

		headMeta.setDisplayName(nameFormat + tools.m("&c&l (Offline)"));

		headMeta.setLore(skullLore);

		head.setItemMeta(headMeta);

		return head;

	}

	public Inventory reportInv(Player operator) {

		FileConfiguration config = plugin.getConfig();
		FileConfiguration guiConfig = plugin.getGuiConfig();
		FileConfiguration reportedPlayerConfig = plugin.getReportedPlayers();

		boolean fillEmptySpace = config.getBoolean("FillEmptySpace");

		String fillerItem = guiConfig.getString("FillerItem");

		String invTitle = tools.msg(guiConfig.getString("ReportGui.InventoryTitle"), operator, operator, "ReportPlayer",
				"report");

		Inventory reportInv = Bukkit.createInventory(operator, 45, invTitle);

		Set<String> reportedPlayers = reportedPlayerConfig.getKeys(false);

		int slot = 0;

		for (String uuid : reportedPlayers) {

			UUID pUuid = UUID.fromString(uuid);

			Player p = plugin.players.get(pUuid);

			if ((p != null)) {

				reportInv.setItem(slot, pHead(p));

			} else {

				OfflinePlayer offlineP = plugin.server.getOfflinePlayer(pUuid);

				reportInv.setItem(slot, offlinepHead(offlineP, operator.getName()));

			}

			slot += 1;

		}

		if (fillEmptySpace) {

			for (int emptySlot = reportInv.firstEmpty(); !(emptySlot >= reportInv.getSize())
					&& (emptySlot >= 0); emptySlot += 1) {

				if (Material.matchMaterial(fillerItem) != null) {

					ItemStack fillItem = new ItemStack(Material.matchMaterial(fillerItem));

					ItemMeta fMeta = fillItem.getItemMeta();

					fMeta.setDisplayName(" ");

					fillItem.setItemMeta(fMeta);

					reportInv.setItem(emptySlot, fillItem);

				} else {

					ItemStack fillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

					ItemMeta fMeta = fillItem.getItemMeta();

					fMeta.setDisplayName(" ");

					fillItem.setItemMeta(fMeta);

					reportInv.setItem(emptySlot, fillItem);

				}

			}

		}

		return reportInv;

	}

}
