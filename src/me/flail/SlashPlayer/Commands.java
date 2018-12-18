package me.flail.SlashPlayer;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.GUI.PlayerListInventory;
import me.flail.SlashPlayer.GUI.ReportInventory;
import me.flail.SlashPlayer.Listeners.BanTimer;
import me.flail.SlashPlayer.Listeners.MuteTimer;

public class Commands implements CommandExecutor {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String cmd = label.toLowerCase(Locale.ENGLISH);

		for (Player p : plugin.players.values()) {

			String pName = p.getName().toLowerCase();
			if (cmd.equals(pName) || pName.startsWith(cmd) || cmd.startsWith(pName)) {

				if (sender instanceof Player) {
					Player operator = (Player) sender;

					if (operator.hasPermission("slashplayer.command")) {

						Inventory pInv = new PlayerInfoInventory().playerInfo(p);

						operator.openInventory(pInv);
						break;

					}

				}

			}

		}

		FileConfiguration reportedPlayers = plugin.getReportedPlayers();

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		if (cmd.equals("slashplayer") || cmd.equals("player")) {

			if (sender instanceof Player) {

				Player operator = (Player) sender;

				if (operator.hasPermission("slashplayer.command")) {

					if (args.length == 0) {

						Inventory pListInv = new PlayerListInventory().playerList();

						operator.openInventory(pListInv);
					} else if (args.length == 1) {

						boolean playerIsOnline = false;

						for (Player player : plugin.players.values()) {

							if (args[0].equalsIgnoreCase(player.getName())) {

								Inventory pInv = new PlayerInfoInventory().playerInfo(player);

								operator.openInventory(pInv);
								playerIsOnline = true;
								break;

							} else {
								playerIsOnline = false;
								continue;
							}

						}

						if (!playerIsOnline) {

							for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {

								if (args[0].equalsIgnoreCase(p.getName())) {

									Inventory pInv = new PlayerInfoInventory().playerInfo(p.getPlayer());

									operator.openInventory(pInv);
									break;

								}

							}

						}

						if (args[0].equalsIgnoreCase("reload")) {

							plugin.loadGuiConfig();
							plugin.loadPlayerData();
							plugin.reloadConfig();

							plugin.server.getScheduler().cancelTasks(plugin);
							new BanTimer().runTaskTimer(plugin, 100, 1200);
							new MuteTimer().runTaskTimer(plugin, 100, 1200);

							operator.sendMessage(chat.m("%prefix% &areloaded all plugin files!"));

						} else {

							operator.sendMessage(chat.msg("", operator, operator, "help", "help"));

						}

					} else if (args.length == 2) {

						if (args[0].equalsIgnoreCase("unban")) {

							if (operator.hasPermission("slashplayer.ban")
									|| operator.hasPermission("slashplayer.command.all")) {

								String pUuid;

								boolean playerBanned = false;

								for (org.bukkit.OfflinePlayer p : Bukkit.getOfflinePlayers()) {

									if (p.getName().equalsIgnoreCase(args[1])) {

										pUuid = p.getUniqueId().toString();

										if (pUuid != null) {

											ConfigurationSection cs = pData.getConfigurationSection(pUuid);

											if (cs != null) {

												boolean isBanned = cs.getBoolean("IsBanned");

												if (isBanned) {

													pData.set(pUuid + ".IsBanned", false);

													operator.sendMessage(chat.m("%prefix% &eunbanned %player%")
															.replace("%player%", args[1]));

													plugin.savePlayerData();

													break;
												} else {
													playerBanned = false;
												}
											} else {
												playerBanned = false;
											}
										} else {
											playerBanned = false;
										}
									} else {
										playerBanned = false;
									}
								}
								if (!playerBanned) {
									operator.sendMessage(chat.m("%prefix% &cThat player is not banned!"));
								}

							} else {
								operator.sendMessage(chat.m("%prefix% &cYou dont have permission to use this!"));
							}

						} else {

							operator.sendMessage(chat.m("%prefix% &cProper Usage&8: &7/player [unban] [player]"));

						}

					} else {

						operator.sendMessage(chat
								.m("%prefix% &cPlayer is not online! Proper usage&8: &7/player [player:unban:reload]"));
						operator.sendMessage(chat.m("&cto unban a player type /player unban [player]"));

					}

				} else {

					operator.sendMessage(chat.m("%prefix% &cYou dont have permission to use this!"));

				}

			} else {
				plugin.console.sendMessage(chat.m("&cPlease use this command in game!"));
			}

		}

		if (cmd.equals("report") || cmd.equals("reportplayer") || cmd.equals("spr")) {

			if (sender instanceof Player) {

				Player player = (Player) sender;

				if (player.hasPermission("slashplayer.report")) {

					if (args.length >= 2) {

						boolean validPlayer = false;

						String reportedPlayer = args[0].toLowerCase();

						for (Player p : plugin.players.values()) {

							String pName = p.getName().toLowerCase();
							if (reportedPlayer.equalsIgnoreCase(pName) || pName.startsWith(reportedPlayer)) {

								String pUuid = p.getUniqueId().toString();

								String reportMsg = "";

								for (int i = 1; i < args.length; i += 1) {

									String arg = args[i];

									reportMsg = reportMsg + " " + arg;

								}

								if (reportMsg.length() > 100) {

									player.sendMessage(chat.m("%prefix% &cPlease keep your report short and concise."));
									validPlayer = true;

								} else if (reportMsg.length() < 6) {
									player.sendMessage(
											chat.m("%prefix% &cyou must give a reason for reporting this player!"));
									validPlayer = true;
								} else {

									reportedPlayers.set(pUuid + ".Uuid", pUuid);
									reportedPlayers.set(pUuid + ".Name", p.getName());
									reportedPlayers.set(pUuid + ".Reporter", player.getName());
									reportedPlayers.set(pUuid + ".Reason", reportMsg.trim().toString());

									String reportSuccess = chat.msg(messages.getString("ReportSuccess"), player, p,
											"ReportPlayer", cmd);

									player.sendMessage(reportSuccess);

									validPlayer = true;
									break;
								}

							} else {
								validPlayer = false;
								continue;
							}

						}

						if (!validPlayer) {

							String invalidPlayer = chat.msg(
									messages.getString("InvalidPlayer").replaceAll("%arg%", reportedPlayer), player,
									player, "ReportPlayer", cmd);

							player.sendMessage(invalidPlayer);

						}

					} else {

						String usage = chat.msg(messages.getString("CommandUsage"), player, player, "ReportPlayer",
								cmd);
						player.sendMessage(usage);

					}

					plugin.saveReportedPlayers();

				} else {

					String noPermission = chat.m(messages.getString("NoPermission"));

					player.sendMessage(noPermission);

				}

			}

		}

		if (cmd.equals("reports")) {

			if (sender instanceof Player) {
				Player operator = (Player) sender;
				operator.openInventory(new ReportInventory().reportInv(operator));

			}

		}

		return true;
	}

}
