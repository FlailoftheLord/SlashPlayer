package me.flail.SlashPlayer.Executables;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;

public class Executables implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void executables(InventoryClickEvent event) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		Inventory inv = event.getInventory();

		InventoryHolder holder = event.getInventory().getHolder();

		if (((holder != null) && (holder instanceof Player)) || (holder == null)) {

			Player subject = null;

			if (holder != null) {
				subject = (Player) holder;
			}

			int headerSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			ItemStack pInfo = inv.getItem(headerSlot - 1);

			String loreUid = "";

			Player pInfoPlayer = null;

			if ((pInfo != null) && pInfo.hasItemMeta() && pInfo.getItemMeta().hasLore()) {

				List<String> lore = pInfo.getItemMeta().getLore();

				loreUid = ChatColor.stripColor(lore.get(0));

				pInfoPlayer = plugin.server.getPlayer(UUID.fromString(loreUid));

			} else {

			}

			if ((pInfoPlayer != null) && pInfoPlayer.equals(subject)) {

				String invTitle = chat.m(config.getString("PlayerMenuTitle").replace("%player%", subject.getName()));

				if (inv.getName().toLowerCase().startsWith(invTitle)) {

					Player operator = (Player) event.getWhoClicked();

					Player player = subject;

					int operatorRank = Tools.playerRank(operator);
					int playerRank = Tools.playerRank(player);

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						operator.closeInventory();
					}

					String pUuid = player.getUniqueId().toString();

					String cantUseExe = messages.getString("AccessDenied").toString();

					String lowRank = messages.getString("RankTooLow").toString();

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
								plugin.logAction(
										operator.getName() + " used " + exe.toUpperCase() + " on " + player.getName());

								new Executioner(plugin).execute(player, operator, exe, "slashplayer", closeInv, false);

							}

							plugin.savePlayerData();
						}

					}

				}
				event.setCancelled(true);
			}

		}

	}

}
