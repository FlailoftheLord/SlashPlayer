package me.flail.SlashPlayer.Listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.GUI.ReportInventory;
import me.flail.SlashPlayer.Utilities.Tools;

@SuppressWarnings("deprecation")
public class ReportGui implements Listener {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();

	private Tools tools = new Tools();

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {

		FileConfiguration guiConfig = manager.getFile("GuiConfig");

		String reportInvTitle = tools.m(guiConfig.get("ReportGui.InventoryTitle").toString());

		Inventory inv = event.getInventory();

		String invTitle = tools.m(inv.getTitle());

		InventoryHolder holder = inv.getHolder();

		if (holder instanceof Player) {

			Player invOwner = (Player) holder;

			Player player = (Player) event.getWhoClicked();

			if (invOwner.equals(player)) {

				if (invTitle.equals(reportInvTitle)) {
					event.setCancelled(true);

					SlotType slotType = event.getSlotType();

					if (slotType.equals(SlotType.OUTSIDE)) {
						player.closeInventory();
						event.setCancelled(true);
					}

					ItemStack eventItem = event.getCurrentItem();

					if ((eventItem != null) && eventItem.hasItemMeta()) {

						ItemMeta eventItemMeta = eventItem.getItemMeta();
						List<String> lore = eventItemMeta.getLore();

						if (lore != null) {

							String pUuid = ChatColor.stripColor(lore.get(0));

							Player p = plugin.players.get(UUID.fromString(pUuid));

							if (player.hasPermission("slashplayer.command")) {

								ClickType click = event.getClick();

								if (click.isShiftClick() && click.isLeftClick()) {

									if (player.hasPermission("slashplayer.staff")) {

										FileConfiguration reportedPlayers = manager.getFile("ReportedPlayers");

										reportedPlayers.set(pUuid, null);

										manager.saveFile(reportedPlayers);

										player.closeInventory();
										player.openInventory(new ReportInventory().reportInv(player));

									} else {
										FileConfiguration messages = manager.getFile("Messages");
										String noPermission = messages.getString("NoPermission");

										player.closeInventory();
										player.sendMessage(
												tools.msg(noPermission, p, player, "Reports", "slashplayer"));
									}

								} else if (click.isLeftClick() && !click.isShiftClick()) {
									if (p != null) {

										player.closeInventory();
										Inventory pInfoInv = new PlayerInfoInventory().playerInfo(p);
										player.openInventory(pInfoInv);

									}

								}

							}

						}

					}

				}

			}

		}

	}

}
