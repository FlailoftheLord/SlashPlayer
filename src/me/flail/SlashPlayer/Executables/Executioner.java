package me.flail.SlashPlayer.Executables;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;
import me.flail.SlashPlayer.GUI.GamemodeInventory;

public class Executioner {

	private SlashPlayer plugin;

	public Executioner(SlashPlayer instance) {
		plugin = instance;
	}

	public boolean execute(Player target, Player operator, String executable, String configSection, boolean closeInv,
			boolean offline) {

		Tools chat = new Tools();

		String command = "slashplayer";

		int operatorRank = Tools.playerRank(operator);
		int playerRank = Tools.playerRank(target);

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration guiConfig = plugin.getGuiConfig();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();

		if (!executable.isEmpty() && (executable != null)) {
			String exe = executable.toLowerCase();

			String pUuid = target.getUniqueId().toString();

			String cantUseExe = messages.getString("AccessDenied").toString();

			String lowRank = messages.getString("RankTooLow").toString();

			boolean verbose = config.getBoolean("ConsoleVerbose");

			boolean equalsCanExecute = config.getBoolean("EqualsCanExecute");

			switch (exe.toLowerCase()) {

			case "teleporttoplayer":

				if (operator.hasPermission("slashtarget.teleport")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						operator.teleport(target);

						operator.sendMessage(
								chat.msg(messages.getString("TeleportPlayer"), target, operator, exe, "teleport"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

					if (verbose) {
						plugin.console.sendMessage(
								chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.",
										target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "summonplayer":

				if (operator.hasPermission("slashtarget.summon")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						target.teleport(operator);

						target.sendMessage(chat.msg(messages.getString("Summoned"), target, operator, exe, "summon"));

						operator.sendMessage(
								chat.msg(messages.getString("SummonPlayer"), target, operator, exe, "summon"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "setgamemode":

				if (operator.hasPermission("slashtarget.gamemode")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						Inventory gmInv = new GamemodeInventory().gamemodeInv(target);
						operator.openInventory(gmInv);

					} else {
						operator.closeInventory();
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					if (closeInv != false) {
						operator.closeInventory();
					}
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				break;

			case "healplayer":

				if (operator.hasPermission("slashtarget.heal")) {

					target.setHealth(20);

					target.sendMessage(chat.msg(messages.getString("Healed"), target, operator, exe, "slashplayer"));

					operator.sendMessage(chat.msg(messages.getString("HealPlayer"), target, operator, exe, "heal"));

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "feedplayer":

				if (operator.hasPermission("slashtarget.feed")) {

					target.setFoodLevel(20);

					target.sendMessage(chat.msg(messages.getString("Fed"), target, operator, exe, "slashplayer"));
					operator.sendMessage(chat.msg(messages.getString("FeedPlayer"), target, operator, exe, "feed"));

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "freezeplayer":

				if (operator.hasPermission("slashtarget.freeze")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						pData.set(pUuid + ".IsFrozen", true);

						if (config.getBoolean("Freeze.AdventureMode")) {

							pData.set(pUuid + ".Gamemode", target.getGameMode().toString());

							target.setGameMode(GameMode.ADVENTURE);

						}

						target.sendMessage(
								chat.msg(messages.getString("Frozen"), target, operator, exe, "slashplayer"));

						operator.sendMessage(
								chat.msg(messages.getString("FreezePlayer"), target, operator, exe, "slashplayer"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "unfreezeplayer":

				if (operator.hasPermission("slashtarget.freeze")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						pData.set(pUuid + ".IsFrozen", false);

						if (config.getBoolean("Freeze.AdventureMode")) {

							String pGm = pData.getString(pUuid + ".Gamemode");

							GameMode gm = GameMode.valueOf(pGm.toUpperCase());

							target.setGameMode(gm);

						}

						target.sendMessage(
								chat.msg(messages.getString("Unfrozen"), target, operator, exe, "slashplayer"));

						operator.sendMessage(
								chat.msg(messages.getString("UnfreezePlayer"), target, operator, exe, "slashplayer"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "kickplayer":

				if (operator.hasPermission("slashtarget.kick")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						boolean broadcast = config.getBoolean("Broadcast.Kick");

						if (broadcast) {
							String broadcastKick = chat.msg(messages.getString("KickBroadcast"), target, operator, exe,
									"slahsplayer");
							plugin.server.broadcast(broadcastKick, "slashtarget.notify");

						}

						target.kickPlayer(chat.msg(messages.getString("KickMessage"), target, operator, exe, "kick"));

						operator.sendMessage(
								chat.msg(messages.getString("PlayerKicked"), target, operator, exe, "kick"));

						pData.set(pUuid + ".IsOnline", false);

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "kick"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "banplayer":

				if (operator.hasPermission("slashtarget.ban")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						int banDuration = config.getInt("BanTime");

						target.kickPlayer(chat.msg(messages.getString("Banned"), target, operator, exe, "ban"));

						pData.set(pUuid + ".IsBanned", true);

						pData.set(pUuid + ".BanDuration", banDuration);

						boolean broadcast = config.getBoolean("Broadcast.Ban");

						if (broadcast) {
							String broadcastBan = chat.msg(messages.getString("BanBroadcast"), target, operator, exe,
									"slahsplayer");
							plugin.server.broadcast(broadcastBan, "slashtarget.notify");

						}

						operator.sendMessage(chat.msg(messages.getString("BanPlayer"), target, operator, exe, "ban"));
						operator.sendMessage(
								chat.msg(messages.getString("UnbanPrompt"), target, operator, exe, "player"));

						pData.set(pUuid + ".IsOnline", false);

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {

					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));

				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}

				break;

			case "unbanplayer":

				if (operator.hasPermission("slashtarget.ban")) {

					if ((((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank))) {

						pData.set(pUuid + ".IsBanned", false);

						operator.sendMessage(
								chat.msg(messages.getString("UnbanPlayer"), target, operator, exe, "unban"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.closeInventory();
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "togglefly":

				if (operator.hasPermission("slashtarget.fly")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						boolean isFlying = pData.getBoolean(pUuid + ".IsFlying");

						if (isFlying) {

							pData.set(pUuid + ".IsFlying", false);

							target.setFlying(false);
							target.setAllowFlight(false);

							target.sendMessage(
									chat.msg(messages.getString("FlyOff"), target, operator, "TogglFly", "fly"));

							operator.sendMessage(
									chat.msg(messages.getString("PlayerFlyOff"), target, operator, "ToggleFly", "fly"));

						} else {

							pData.set(pUuid + ".IsFlying", true);

							target.setAllowFlight(true);
							target.setFlying(true);

							target.sendMessage(
									chat.msg(messages.getString("FlyOn"), target, operator, "ToggleFly", "fly"));

							operator.sendMessage(
									chat.msg(messages.getString("PlayerFlyOn"), target, operator, "ToggleFly", "fly"));
						}

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "muteplayer":

				if (operator.hasPermission("slashtarget.mute")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						pData.set(pUuid + ".IsMuted", true);

						int muteTime = config.getInt("MuteTime");

						pData.set(pUuid + ".MuteDuration", muteTime);

						boolean broadcast = config.getBoolean("Broadcast.Ban");

						target.sendMessage(
								chat.msg(messages.getString("Muted"), target, operator, "MutePlayer", "mute"));

						if (broadcast) {
							String broadcastMute = chat.msg(messages.getString("MuteBroadcast"), target, operator, exe,
									"slahsplayer");
							plugin.server.broadcast(broadcastMute, "slashtarget.notify");

						}

						operator.sendMessage(
								chat.msg(messages.getString("MutePlayer"), target, operator, "MutePlayer", "mute"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "unmuteplayer":

				if (operator.hasPermission("slashtarget.mute")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

						if (isMuted) {

							pData.set(pUuid + ".IsMuted", false);

							pData.set(pUuid + ".MuteDuration", null);

							target.sendMessage(
									chat.msg(messages.getString("Unmuted"), target, operator, "UnmutePlayer", "mute"));

							operator.sendMessage(chat.msg(messages.getString("UnmutePlayer"), target, operator,
									"UnmutePlayer", "mute"));

						} else {

							String notMuted = messages.getString("NotMuted");

							operator.sendMessage(chat.msg(notMuted, target, operator, "UnmutePlayer", "slashplayer"));

						}

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(chat.msg(cantUseExe, target, operator, exe, "slashplayer"));
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "killplayer":

				if (operator.hasPermission("slahstarget.kill")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						target.damage(696969, operator);
						target.sendMessage(
								chat.msg(messages.getString("Killed"), target, operator, "KillPlayer", "slashplayer"));
						operator.sendMessage(chat.msg(messages.getString("KillPlayer"), target, operator, "KillPlayer",
								"slashplayer"));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(cantUseExe);
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			case "clearinventory":

				if (operator.hasPermission("slashtarget.clearinventory")) {

					if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

						plugin.getPlayerData().set(pUuid + ".InventoryBackup.Inventory",
								target.getInventory().getContents());
						plugin.getPlayerData().set(pUuid + ".InventoryBackup.Armor",
								target.getInventory().getArmorContents());

						target.getInventory().clear();
						target.getInventory().setArmorContents(new ItemStack[] {});

						target.sendMessage(chat.msg(messages.getString("InventoryCleared"), target, operator,
								"ClearInventory", "slashplayer"));
						operator.sendMessage(chat.msg(messages.getString("PlayerInvCleared"), target, operator,
								"ClearInventory", "slashplayer"));
						operator.sendMessage(chat.m(
								"&aA backup of the players' inventory has been saved, simply type &7/sp restoreinv %player%"
										.replace("%player%", target.getName())));

					} else {
						operator.sendMessage(chat.msg(lowRank, target, operator, exe, "slashplayer"));
					}

				} else {
					operator.sendMessage(cantUseExe);
				}

				if (verbose) {
					plugin.console.sendMessage(
							chat.msg("%prefix% &a%operator% &chas just used &a%executable% &con &a%player%.", target,
									operator, exe, "slashplayer"));
				}

				if (closeInv != false) {
					operator.closeInventory();
				}
				break;

			}

		}

		return true;
	}

}
