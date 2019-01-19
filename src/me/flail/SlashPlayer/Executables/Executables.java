package me.flail.SlashPlayer.Executables;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import me.flail.SlashPlayer.GUI.GamemodeInventory;

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

		if ((holder != null) && (holder instanceof Player)) {

			Player subject = (Player) holder;

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

				if (inv.getTitle().equalsIgnoreCase(invTitle)) {

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

								boolean verbose = config.getBoolean("ConsoleVerbose");

								boolean equalsCanExecute = config.getBoolean("EqualsCanExecute");

								// Write the logs boi :>
								plugin.logAction(
										operator.getName() + " used " + exe.toUpperCase() + " on " + player.getName());

								switch (exe.toLowerCase()) {

								case "teleporttoplayer":

									if (operator.hasPermission("slashplayer.teleport")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											operator.teleport(player);

											operator.sendMessage(chat.msg(messages.getString("TeleportPlayer"), player,
													operator, exe, "teleport"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

										if (verbose) {
											plugin.console.sendMessage(chat.msg(
													"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
													player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "summonplayer":

									if (operator.hasPermission("slashplayer.summon")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											player.teleport(operator);

											player.sendMessage(chat.msg(messages.getString("Summoned"), player,
													operator, exe, "summon"));

											operator.sendMessage(chat.msg(messages.getString("SummonPlayer"), player,
													operator, exe, "summon"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "setgamemode":

									if (operator.hasPermission("slashplayer.gamemode")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											Inventory gmInv = new GamemodeInventory().gamemodeInv(player);
											operator.openInventory(gmInv);

										} else {
											operator.closeInventory();
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										if (closeInv != false) {
											operator.closeInventory();
										}
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									break;

								case "healplayer":

									if (operator.hasPermission("slashplayer.heal")) {

										player.setHealth(20);

										player.sendMessage(chat.msg(messages.getString("Healed"), player, operator, exe,
												"slashplayer"));

										operator.sendMessage(chat.msg(messages.getString("HealPlayer"), player,
												operator, exe, "heal"));

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "feedplayer":

									if (operator.hasPermission("slashplayer.feed")) {

										player.setFoodLevel(20);

										player.sendMessage(chat.msg(messages.getString("Fed"), player, operator, exe,
												"slashplayer"));
										operator.sendMessage(chat.msg(messages.getString("FeedPlayer"), player,
												operator, exe, "feed"));

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "freezeplayer":

									if (operator.hasPermission("slashplayer.freeze")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											pData.set(pUuid + ".IsFrozen", true);

											if (config.getBoolean("Freeze.AdventureMode")) {

												pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

												player.setGameMode(GameMode.ADVENTURE);

											}

											player.sendMessage(chat.msg(messages.getString("Frozen"), player, operator,
													exe, "slashplayer"));

											operator.sendMessage(chat.msg(messages.getString("FreezePlayer"), player,
													operator, exe, "slashplayer"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "unfreezeplayer":

									if (operator.hasPermission("slashplayer.freeze")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											pData.set(pUuid + ".IsFrozen", false);

											if (config.getBoolean("Freeze.AdventureMode")) {

												String pGm = pData.getString(pUuid + ".Gamemode");

												GameMode gm = GameMode.valueOf(pGm.toUpperCase());

												player.setGameMode(gm);

											}

											player.sendMessage(chat.msg(messages.getString("Unfrozen"), player,
													operator, exe, "slashplayer"));

											operator.sendMessage(chat.msg(messages.getString("UnfreezePlayer"), player,
													operator, exe, "slashplayer"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "kickplayer":

									if (operator.hasPermission("slashplayer.kick")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											boolean broadcast = config.getBoolean("Broadcast.Kick");

											if (broadcast) {
												String broadcastKick = chat.msg(messages.getString("KickBroadcast"),
														player, operator, exe, "slahsplayer");
												plugin.server.broadcast(broadcastKick, "slashplayer.notify");

											}

											player.kickPlayer(chat.msg(messages.getString("KickMessage"), player,
													operator, exe, "kick"));

											operator.sendMessage(chat.msg(messages.getString("PlayerKicked"), player,
													operator, exe, "kick"));

											pData.set(pUuid + ".IsOnline", false);

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "kick"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "banplayer":

									if (operator.hasPermission("slashplayer.ban")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											int banDuration = config.getInt("BanTime");

											player.kickPlayer(chat.msg(messages.getString("Banned"), player, operator,
													exe, "ban"));

											pData.set(pUuid + ".IsBanned", true);

											pData.set(pUuid + ".BanDuration", banDuration);

											boolean broadcast = config.getBoolean("Broadcast.Ban");

											if (broadcast) {
												String broadcastBan = chat.msg(messages.getString("BanBroadcast"),
														player, operator, exe, "slahsplayer");
												plugin.server.broadcast(broadcastBan, "slashplayer.notify");

											}

											operator.sendMessage(chat.msg(messages.getString("BanPlayer"), player,
													operator, exe, "ban"));
											operator.sendMessage(chat.msg(messages.getString("UnbanPrompt"), player,
													operator, exe, "player"));

											pData.set(pUuid + ".IsOnline", false);

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {

										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));

									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}

									break;

								case "unbanplayer":

									if (operator.hasPermission("slashplayer.ban")) {

										if ((((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank))) {

											pData.set(pUuid + ".IsBanned", false);

											operator.sendMessage(chat.msg(messages.getString("UnbanPlayer"), player,
													operator, exe, "unban"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.closeInventory();
										operator.sendMessage(
												chat.msg(cantUseExe, player, operator, exe, "slashplayer"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "togglefly":

									if (operator.hasPermission("slashplayer.fly")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											boolean isFlying = pData.getBoolean(pUuid + ".IsFlying");

											if (isFlying) {

												pData.set(pUuid + ".IsFlying", false);

												player.setFlying(false);
												player.setAllowFlight(false);

												player.sendMessage(chat.msg(messages.getString("FlyOff"), player,
														operator, "TogglFly", "fly"));

												operator.sendMessage(chat.msg(messages.getString("PlayerFlyOff"),
														player, operator, "ToggleFly", "fly"));

											} else {

												pData.set(pUuid + ".IsFlying", true);

												player.setAllowFlight(true);
												player.setFlying(true);

												player.sendMessage(chat.msg(messages.getString("FlyOn"), player,
														operator, "ToggleFly", "fly"));

												operator.sendMessage(chat.msg(messages.getString("PlayerFlyOn"), player,
														operator, "ToggleFly", "fly"));
											}

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "fly"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "muteplayer":

									if (operator.hasPermission("slashplayer.mute")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											pData.set(pUuid + ".IsMuted", true);

											int muteTime = config.getInt("MuteTime");

											pData.set(pUuid + ".MuteDuration", muteTime);

											boolean broadcast = config.getBoolean("Broadcast.Ban");

											player.sendMessage(chat.msg(messages.getString("Muted"), player, operator,
													"MutePlayer", "mute"));

											if (broadcast) {
												String broadcastMute = chat.msg(messages.getString("MuteBroadcast"),
														player, operator, exe, "slahsplayer");
												plugin.server.broadcast(broadcastMute, "slashplayer.notify");

											}

											operator.sendMessage(chat.msg(messages.getString("MutePlayer"), player,
													operator, "MutePlayer", "mute"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "mute"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "unmuteplayer":

									if (operator.hasPermission("slashplayer.mute")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

											if (isMuted) {

												pData.set(pUuid + ".IsMuted", false);

												pData.set(pUuid + ".MuteDuration", null);

												player.sendMessage(chat.msg(messages.getString("Unmuted"), player,
														operator, "UnmutePlayer", "mute"));

												operator.sendMessage(chat.msg(messages.getString("UnmutePlayer"),
														player, operator, "UnmutePlayer", "mute"));

											} else {

												String notMuted = messages.getString("NotMuted");

												operator.sendMessage(
														chat.msg(notMuted, player, operator, "UnmutePlayer", "mute"));

											}

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "mute"));
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "killplayer":

									if (operator.hasPermission("slahsplayer.kill")) {

										if (((operatorRank >= playerRank) && equalsCanExecute)
												|| (operatorRank > playerRank)) {

											player.damage(696969, operator);
											player.sendMessage(chat.msg(messages.getString("Killed"), player, operator,
													"KillPlayer", "kill"));
											operator.sendMessage(chat.msg(messages.getString("KillPlayer"), player,
													operator, "KillPlayer", "kill"));

										} else {
											operator.sendMessage(
													chat.msg(lowRank, player, operator, exe, "slashplayer"));
										}

									} else {
										operator.sendMessage(cantUseExe);
									}

									if (verbose) {
										plugin.console.sendMessage(chat.msg(
												"%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
												player, operator, exe, "slashplayer"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								}

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
