package me.flail.SlashPlayer;

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
import me.flail.SlashPlayer.Listeners.BanTimer;
import me.flail.SlashPlayer.Listeners.MuteTimer;

public class Commands implements CommandExecutor {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Utilities chat = new Utilities();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String cmd = label.toLowerCase();

		if (cmd.equals("slashplayer") || cmd.equals("player") || cmd.equals("players") || cmd.equals("sp")) {

			if (sender instanceof Player) {

				Player operator = (Player) sender;

				FileConfiguration pData = plugin.getPlayerData();

				if (operator.hasPermission("slashplayer.command")) {

					if (args.length == 0) {

						Inventory pListInv = new PlayerListInventory().playerList();

						operator.openInventory(pListInv);
					} else if (args.length == 1) {

						boolean playerIsOnline = false;

						for (Player player : Bukkit.getOnlinePlayers()) {

							if (args[0].equalsIgnoreCase(player.getName())) {

								Inventory pInv = new PlayerInfoInventory().playerInfo(player);

								operator.openInventory(pInv);
								playerIsOnline = true;
								break;

							} else {
								playerIsOnline = false;
							}

						}

						if (!playerIsOnline) {

							for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {

								if (args[0].equalsIgnoreCase(p.getName())) {

									Inventory pInv = new PlayerInfoInventory().playerInfo((Player) p);

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

		if (cmd.equals("report") || cmd.equals("reportplayer")) {

		}

		return true;
	}

}
