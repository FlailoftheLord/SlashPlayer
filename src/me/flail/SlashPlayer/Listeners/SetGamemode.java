package me.flail.SlashPlayer.Listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;

public class SetGamemode implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	private ConsoleCommandSender console = plugin.console;

	@EventHandler
	public void setGamemode(InventoryClickEvent event) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration messages = plugin.getMessages();

		Inventory inv = event.getInventory();

		InventoryHolder holder = event.getInventory().getHolder();

		if ((holder instanceof Player) && inv.getType().equals(InventoryType.CHEST)) {

			Player subject = (Player) holder;

			int headerSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			ItemStack pInfo = inv.getItem(headerSlot - 1);

			String loreUid = "";

			Player pInfoPlayer = null;

			if ((pInfo != null) && pInfo.hasItemMeta() && pInfo.getItemMeta().hasLore()) {

				List<String> lore = pInfo.getItemMeta().getLore();

				loreUid = ChatColor.stripColor(lore.get(0));

				pInfoPlayer = plugin.players.get(UUID.fromString(loreUid));

			}

			if ((subject != null) && (pInfoPlayer != null) && pInfoPlayer.equals(subject)) {

				String invTitle = chat
						.m(guiConfig.getString("GamemodeInventory.Title").replace("%player%", subject.getName()));

				if (inv.getTitle().equalsIgnoreCase(invTitle)) {

					Player operator = (Player) event.getWhoClicked();

					Player player = subject;

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						operator.closeInventory();

						operator.openInventory(new PlayerInfoInventory().playerInfo(player));
					}

					ItemStack item = event.getCurrentItem();

					ItemMeta iM;

					if ((item != null) && item.hasItemMeta()) {

						if (item.hasItemMeta()) {

							iM = item.getItemMeta();

							if (iM.hasEnchant(Enchantment.MENDING)) {

								int slot = iM.getEnchantLevel(Enchantment.MENDING);
								ConfigurationSection cs = guiConfig
										.getConfigurationSection("GamemodeInventory." + slot);

								if (cs != null) {

									String mode = cs.getString("Mode");

									if ((mode != null) && (mode != "")) {

										String changedGamemode = chat.msg(messages.getString("PlayerGamemodeChanged"),
												player, operator, "SetGamemode", "gamemode");

										String gamemodeUpdated = chat.msg(messages.getString("GamemodeChanged"), player,
												operator, "SetGamemode", "gamemode");

										String playerExempt = chat.msg(messages.getString("PlayerExempt"), player,
												operator, "SetGamemode", "gamemode");

										if (mode.equalsIgnoreCase("backbutton")) {

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else if (mode.equalsIgnoreCase("survival")) {

											if (operator.hasPermission("slahsplayer.gamemode.survival")) {

												player.setGameMode(GameMode.SURVIVAL);

												player.sendMessage(gamemodeUpdated);

												operator.sendMessage(changedGamemode);

												operator.closeInventory();

												operator.openInventory(new PlayerInfoInventory().playerInfo(player));

											} else {
												player.sendMessage(playerExempt.replaceAll("%mode%", mode));
												player.closeInventory();
											}

										} else if (mode.equalsIgnoreCase("adventure")) {

											player.sendMessage("hi");

											if (operator.hasPermission("slashplayer.gamemode.adventure")
													&& !player.hasPermission("slasplayer.exempt")) {

												player.setGameMode(GameMode.ADVENTURE);

												player.sendMessage(gamemodeUpdated);

												operator.sendMessage(changedGamemode);

												operator.closeInventory();

												operator.openInventory(new PlayerInfoInventory().playerInfo(player));

											} else {

												player.sendMessage(playerExempt.replaceAll("%mode%", mode));
												player.closeInventory();

											}

										} else if (mode.equalsIgnoreCase("creative")) {

											if (operator.hasPermission("slashplayer.gamemode.creative")) {

												player.setGameMode(GameMode.CREATIVE);

												player.sendMessage(gamemodeUpdated);

												operator.sendMessage(changedGamemode);
												operator.closeInventory();

												operator.openInventory(new PlayerInfoInventory().playerInfo(player));

											} else {
												player.sendMessage(playerExempt.replaceAll("%mode%", mode));
												player.closeInventory();
											}

										} else if (mode.equalsIgnoreCase("spectator")) {

											if (operator.hasPermission("slashplayer.gamemode.spectator")) {

												player.setGameMode(GameMode.SPECTATOR);

												player.sendMessage(gamemodeUpdated);

												operator.sendMessage(changedGamemode);

												operator.closeInventory();

												operator.openInventory(new PlayerInfoInventory().playerInfo(player));

											} else {
												player.sendMessage(playerExempt.replaceAll("%mode%", mode));
												player.closeInventory();
											}

										} else {
											console.sendMessage(
													chat.m("&cInvalid Gamemode Specified in &o&nGuiConfig.yml"));
											console.sendMessage(chat.m("&c" + mode + "is not a valid Mode!"));
										}

									} else {
										return;
									}

								}

							}

						}

					}

					event.setCancelled(true);
				}

			}

		}

	}

}
