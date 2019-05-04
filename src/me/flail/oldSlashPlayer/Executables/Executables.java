package me.flail.SlashPlayer.Executables;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.Utilities.Tools;
import me.flail.oldSlashPlayer.SlashPlayer;

public class Executables implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();

	@EventHandler
	public void executables(InventoryClickEvent event) {

		FileConfiguration guiConfig = manager.getFile("GuiConfig");

		Inventory eventInv = event.getInventory();

		if (eventInv.getType().equals(InventoryType.CHEST)) {

			Inventory inv = eventInv;

			int headerSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			ItemStack pInfo = inv.getItem(headerSlot - 1);

			String loreUid = "";

			OfflinePlayer pInfoPlayer = null;

			boolean isCorrectInventory = false;

			if ((pInfo != null) && pInfo.hasItemMeta() && pInfo.getItemMeta().hasLore()) {

				List<String> lore = pInfo.getItemMeta().getLore();

				String uid = lore.get(0);

				if (Tools.hasCode(uid, "1")) {
					loreUid = ChatColor.stripColor(uid);
					pInfoPlayer = plugin.server.getOfflinePlayer(UUID.fromString(loreUid));
					isCorrectInventory = true;
				} else {
					loreUid = ChatColor.stripColor(uid);
					pInfoPlayer = plugin.server.getOfflinePlayer(UUID.fromString(loreUid));
					isCorrectInventory = false;
				}

			}

			if ((pInfoPlayer != null)) {

				if (isCorrectInventory) {

					Player operator = (Player) event.getWhoClicked();

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						operator.closeInventory();
					}

					ItemStack item = event.getCurrentItem();

					ItemMeta iM;

					int slot;

					if ((item != null) && item.hasItemMeta()) {
						iM = item.getItemMeta();
						slot = iM.getEnchantLevel(Enchantment.MENDING);
						ConfigurationSection cs = guiConfig.getConfigurationSection("PlayerInfo." + slot);

						if (cs != null) {
							String exe = cs.getString("Execute");

							if ((exe != null) && (exe != "")) {

								boolean closeInv = cs.getBoolean("CloseInventory");

								// Write the logs boi :>
								plugin.logAction(operator.getName() + " used " + exe.toUpperCase() + " on "
										+ pInfoPlayer.getName());

								Executioner enviroment = new Executioner(plugin);

								enviroment.execute(pInfoPlayer, operator, exe, "slashplayer", closeInv, false);

							}

						}

					}

				}

				event.setCancelled(true);
			}

		}

	}

}