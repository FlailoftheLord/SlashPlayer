package me.flail.SlashPlayer;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.flail.SlashPlayer.ControlCenter.BanControl;
import me.flail.SlashPlayer.ControlCenter.MuteControl;
import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.GUI.PlayerListInventory;
import me.flail.SlashPlayer.GUI.ReportInventory;
import me.flail.SlashPlayer.Utilities.PlayerEventHandler;
import me.flail.SlashPlayer.Utilities.Tools;

public class Commands implements CommandExecutor {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();

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

						plugin.logAction(operator.getName() + "opened the SlashPlayer gui for " + pName);

						Inventory pInv = new PlayerInfoInventory().playerInfo(p);

						operator.openInventory(pInv);
						return true;

					}

				}

			}

		}

		FileConfiguration reportedPlayers = manager.getFile("ReportedPlayers");

		FileConfiguration pData = manager.getFile("PlayerData");

		FileConfiguration messages = manager.getFile("Messages");

		if (cmd.equals("slashplayer") || cmd.equals("player")) {

			if (sender instanceof Player) {

				Player operator = (Player) sender;

				if (operator.hasPermission("slashplayer.command")) {

					if (args.length == 0) {

						Inventory pListInv = new PlayerListInventory().playerList();

						operator.openInventory(pListInv);
					} else if (args.length == 1) {

						if (args[0].equalsIgnoreCase("reload")) {

							for (Player p : plugin.server.getOnlinePlayers()) {
								plugin.players.put(p.getUniqueId(), p);
							}

							BanControl bans = new BanControl();
							bans.loadList();

							MuteControl mutes = new MuteControl();
							mutes.saveList();
							mutes.loadList();

							for (Player player : plugin.players.values()) {
								PlayerEventHandler pHandler = new PlayerEventHandler();
								pHandler.setData(player);
							}

							manager.loadFile("PlayerData");
							manager.loadFile("GuiConfig");
							manager.loadFile("Messages");
							plugin.reloadConfig();

							plugin.server.getScheduler().cancelTasks(plugin);
							plugin.startTasks();

							operator.sendMessage(chat.m("%prefix% &areloaded all plugin files!"));

						} else if (args[0].equalsIgnoreCase("help")) {

							List<String> helpMessage = messages.getStringList("HelpMessage");

							for (String msg : helpMessage) {
								operator.sendMessage(chat.msg(msg, operator, operator, "SlashPlayer-Help", cmd));
							}

						} else {

							boolean playerIsOnline = false;

							for (Player player : plugin.players.values()) {

								if (args[0].equalsIgnoreCase(player.getName())) {

									plugin.logAction(
											operator.getName() + " opened the SlashPlayer gui for " + player.getName());

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

								for (OfflinePlayer p : plugin.server.getOfflinePlayers()) {

									if (args[0].equalsIgnoreCase(p.getName())) {

										plugin.logAction(operator.getName()
												+ " opened the SlashPlayer gui for OfflinePlayer: " + p.getName());

										Inventory pInv = new PlayerInfoInventory().playerInfo(p);

										operator.openInventory(pInv);
										break;

									}

								}

							}

						}

					} else if (args.length == 2) {

						if (args[0].equalsIgnoreCase("unban")) {

							if (operator.hasPermission("slashplayer.ban") || operator.hasPermission("slashplayer.unban")
									|| operator.hasPermission("slashplayer.command.all")) {

								boolean playerBanned = false;

								for (String uuid : plugin.banList.keySet()) {
									OfflinePlayer p = plugin.server.getOfflinePlayer(UUID.fromString(uuid));

									if (p.getName().equalsIgnoreCase(args[1])) {

										BanControl bans = new BanControl();

										bans.unbanPlayer(p);
										operator.sendMessage(chat.msg(plugin.manager.getMessage("UnbanPlayer"), p,
												operator, "Unban", label));

										playerBanned = true;
										break;
									} else {
										playerBanned = false;
									}
								}
								if (!playerBanned) {
									operator.sendMessage(chat.m("%prefix% &cThat player is not banned!"));
								}

							} else {
								operator.sendMessage(chat.m("%prefix% &cYou don't have permission to use this!"));
							}

						} else if (args[0].equalsIgnoreCase("rank")) {

							for (Player p : plugin.players.values()) {

								if (p.getName().equalsIgnoreCase(args[1].trim())) {

									operator.sendMessage(chat
											.m("%prefix% &3" + p.getName() + "s&a Rank is &7" + Tools.playerRank(p)));

								}

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
									break;

								} else if (reportMsg.length() < 6) {
									player.sendMessage(
											chat.m("%prefix% &cyou must give a reason for reporting this player!"));
									validPlayer = true;
									break;
								} else {

									reportedPlayers.set(pUuid + ".Uuid", pUuid);
									reportedPlayers.set(pUuid + ".Name", p.getName());
									reportedPlayers.set(pUuid + ".Reporter", player.getName());
									reportedPlayers.set(pUuid + ".Reason", reportMsg.trim().toString());

									String reportSuccess = chat.msg(messages.getString("ReportSuccess"), p, player,
											"ReportPlayer", cmd);

									player.sendMessage(reportSuccess);

									for (Player op : plugin.players.values()) {

										if (op.hasPermission("slashplayer.staff")) {

											String playerReported = chat.msg(messages.getString("PlayerReported")
													.replaceAll("%reporter%", player.getName()), p, player,
													"ReportPlayer", cmd);

											op.sendMessage(playerReported.replaceAll("%reason%", reportMsg));

										}

									}

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

						manager.saveFile(reportedPlayers);

					} else {

						String usage = chat.msg(messages.getString("CommandUsage"), player, player, "ReportPlayer",
								cmd);
						player.sendMessage(usage);

					}

				} else {

					String noPermission = chat.msg(messages.getString("NoPermission"), player, player, "ReportPlayer",
							cmd);

					player.sendMessage(noPermission);

				}

			}

		}

		if (cmd.equals("reports")) {

			if (sender instanceof Player) {

				Player operator = (Player) sender;
				if (operator.hasPermission("slashplayer.command") && operator.hasPermission("slashplayer.staff")) {
					operator.openInventory(new ReportInventory().reportInv(operator));
				} else {

					String noPermission = chat.msg(messages.getString("NoPermission"), operator, operator, "Reports",
							cmd);

					operator.sendMessage(noPermission);

				}

			}

		}

		if (cmd.equals("spunban")) {

			Tools tools = new Tools();

			if (!(sender instanceof Player)) {

				if (args.length == 1) {

					boolean validPlayer = false;

					for (String player : pData.getKeys(false)) {

						String pName = pData.get(player + ".Name").toString();

						if (pName.equalsIgnoreCase(args[0]) || pName.toLowerCase().startsWith(args[0].toLowerCase())) {

							pData.set(player + ".IsBanned", false);

							plugin.console.sendMessage(
									tools.m("%prefix% &aUnbanned player &c%player%!").replaceAll("%player%", pName));
							validPlayer = true;

							manager.saveFile(pData);
							break;

						}

					}

					if (!validPlayer) {
						plugin.console.sendMessage(
								tools.m("%prefix% &cInvalid player &7%player%!").replaceAll("%player%", args[0]));

					}
				} else {
					plugin.console.sendMessage(tools.m("%prefix% &cProper usage /spunban [player-name]"));
				}

			} else {
				sender.sendMessage(tools.m("%prefix% &cthis command is for console only!"));
			}

		}

		if (command.getName().equalsIgnoreCase("ouch")) {
			if (sender instanceof Player) {
				((Player) sender).damage(1);
			}

			sender.sendMessage(new Tools().m("&c&l:'("));
		}

		return true;
	}

}
