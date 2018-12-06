package me.flail.SlashPlayer.Listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;

public class SetGamemode implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Utilities chat = new Utilities();

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	@EventHandler
	public void setGamemode(InventoryClickEvent event) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

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

				pInfoPlayer = plugin.server.getPlayer(UUID.fromString(loreUid));

			}

			if (pInfoPlayer.equals(subject)) {

				String invTitle = chat
						.m(guiConfig.getString("GamemodeInventory.Title").replace("%player%", subject.getName()));

				if (inv.getTitle().equalsIgnoreCase(invTitle)) {

					Player operator = (Player) event.getWhoClicked();

					Player player = subject;

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

									String mode = cs.getString("Mode").toLowerCase();

									if ((mode != null) && (mode != "")) {

										if (mode.equalsIgnoreCase("backbutton")) {

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else if (mode.equals("survival")) {

											player.setGameMode(GameMode.SURVIVAL);

											player.sendMessage(
													chat.m("%prefix% &ayour gamemode has been changed by staff."));

											operator.sendMessage(chat
													.m("%prefix% &asuccessfully changed gamemode to &7Survival &afor "
															+ player.getName()));

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else if (mode.equals("adventure")) {

											player.setGameMode(GameMode.ADVENTURE);

											player.sendMessage(
													chat.m("%prefix% &ayour gamemode has been changed by staff."));

											operator.sendMessage(chat
													.m("%prefix% &asuccessfully changed gamemode to &7Adventure &afor "
															+ player.getName()));

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else if (mode.equals("creative")) {

											player.setGameMode(GameMode.CREATIVE);

											player.sendMessage(
													chat.m("%prefix% &ayour gamemode has been changed by staff."));

											operator.sendMessage(chat
													.m("%prefix% &asuccessfully changed gamemode to &7Creative &afor "
															+ player.getName()));

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else if (mode.equals("spectator")) {

											player.setGameMode(GameMode.SPECTATOR);

											player.sendMessage(
													chat.m("%prefix% &ayour gamemode has been changed by staff."));

											operator.sendMessage(chat
													.m("%prefix% &asuccessfully changed gamemode to &7Spectator &afor "
															+ player.getName()));

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else {
											console.sendMessage(
													chat.m("&cInvalid Gamemode Specified in GuiConfig.yml"));
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
