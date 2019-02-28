package me.flail.SlashPlayer.Executables;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.GUI.GamemodeInventory;
import me.flail.SlashPlayer.Runnables.BanControl;
import me.flail.SlashPlayer.Utilities.ExeHandler;
import me.flail.SlashPlayer.Utilities.FileManager;
import me.flail.SlashPlayer.Utilities.InventoryManager;
import me.flail.SlashPlayer.Utilities.Tools;

public class Executioner {

	private SlashPlayer plugin;

	public Executioner(SlashPlayer instance) {
		plugin = instance;
	}

	public boolean execute(OfflinePlayer targetPlayer, Player operator, String executable, String configSection,
			boolean closeInv, boolean offline) {

		Tools chat = new Tools();
		ExeHandler handler = new ExeHandler();

		String command = "slashplayer";

		String pUuid = targetPlayer.getUniqueId().toString();

		FileManager manager = plugin.manager;

		InventoryManager invManager = new InventoryManager();

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();

		if (!executable.isEmpty() && (executable != null)) {

			String cantUseExe = messages.getString("AccessDenied").toString();

			boolean verbose = config.getBoolean("ConsoleVerbose");

			if (operator.hasPermission("slashplayer." + handler.exeType(executable))) {

				if (targetPlayer.isOnline()) {
					Player target = targetPlayer.getPlayer();

					int operatorRank = Tools.playerRank(operator);
					int playerRank = Tools.playerRank(target);

					boolean equalsCanExecute = config.getBoolean("EqualsCanExecute");

					if ((operatorRank > playerRank) || ((operatorRank >= playerRank) && equalsCanExecute)) {

						String exe = handler.exeType(executable);

						if (!exe.equals("nothing")) {

							switch (exe) {

							case "teleport":
								operator.sendMessage(
										chat.msg(manager.getMessage("TeleportPlayer"), target, operator, exe, command));

								operator.teleport(target);

								break;
							case "summon":
								operator.sendMessage(
										chat.msg(manager.getMessage("SummonPlayer"), target, operator, exe, command));

								target.sendMessage(
										chat.msg(manager.getMessage("Summoned"), target, operator, exe, command));

								target.teleport(operator);

								break;
							case "heal":
								operator.sendMessage(
										chat.msg(manager.getMessage("HealPlayer"), target, operator, exe, command));

								target.sendMessage(
										chat.msg(manager.getMessage("Healed"), target, operator, exe, command));

								for (PotionEffect eff : target.getActivePotionEffects()) {
									target.removePotionEffect(eff.getType());
								}
								target.setHealth(20);

								break;
							case "feed":
								operator.sendMessage(
										chat.msg(manager.getMessage("FeedPlayer"), target, operator, exe, command));
								target.sendMessage(chat.msg(manager.getMessage("Fed"), target, operator, exe, command));

								target.setFoodLevel(26);
								break;
							case "fly":

								FlyControl flyControl = new FlyControl();

								if (flyControl.fly(target)) {
									operator.sendMessage(chat.msg(manager.getMessage("PlayerFlyOn"), target, operator,
											exe, command));
									target.sendMessage(
											chat.msg(manager.getMessage("FlyOn"), target, operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("PlayerFlyOff"), target, operator,
											exe, command));
									target.sendMessage(
											chat.msg(manager.getMessage("FlyOff"), target, operator, exe, command));

								}

								break;
							case "gamemode":
								GamemodeInventory gmInv = new GamemodeInventory();
								operator.closeInventory();
								operator.openInventory(gmInv.gamemodeInv(target));
								closeInv = false;

								break;
							case "kick":

								if (!target.hasPermission("slashplayer.exempt.kick")) {
									String reason = messages.get("KickMessage").toString();

									if (config.getBoolean("Broadcast.Kick")) {
										for (Player p : plugin.players.values()) {
											if (p.hasPermission("slashplayer.notify")) {
												p.sendMessage(
														chat.msg(
																plugin.manager.getMessage("KickBroadcast").toString()
																		.replace("%reason%", reason),
																target, operator, exe, command));
											}
										}
									} else {
										operator.sendMessage(chat.msg(
												messages.get("PlayerKicked").toString().replace("%reason%", reason),
												target, operator, exe, command));
									}

									target.kickPlayer(chat.msg(reason, target, operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("PlayerExempt"), target, operator,
											exe, command));

								}

								break;
							case "kill":

								if (!target.hasPermission("slashplayer.exempt.kill")) {
									GameMode gm = target.getGameMode();
									target.setGameMode(GameMode.SURVIVAL);

									target.closeInventory();
									for (PotionEffect eff : target.getActivePotionEffects()) {
										target.removePotionEffect(eff.getType());
									}
									target.setFlying(false);
									target.setHealth(1);
									target.damage(42, operator);

									target.sendMessage(
											chat.msg(manager.getMessage("Killed"), target, operator, exe, command));

									plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

										target.setGameMode(gm);

									}, 20);

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("CantKillPlayer"), target,
											operator, exe, command));

								}

								break;
							case "clearinventory":
								if (!target.hasPermission("slashplayer.exempt.clearinventory")) {

									invManager.clearInventory(target);

									target.sendMessage(chat.msg(manager.getMessage("InventoryCleared"), target,
											operator, exe, command));

									operator.sendMessage(chat.msg(manager.getMessage("PlayerInventoryCleared"), target,
											operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("CantClearInv"), target, operator,
											exe, command));
								}

								break;
							case "restoreinventory":

								invManager.restoreInventory(target);
								target.sendMessage(chat.msg(manager.getMessage("InventoryRestored"), target, operator,
										exe, command));

								operator.sendMessage(chat.msg(manager.getMessage("PlayerInventoryRestored"), target,
										operator, exe, command));

								break;
							case "openinventory":
								invManager.openPlayerInventory(operator, target);

								closeInv = false;
								break;
							case "enderchest":
								invManager.openPlayerEnderchest(operator, target);

								closeInv = false;
								break;
							case "whitelist":
								if (plugin.server.hasWhitelist()) {
									if (target.isWhitelisted()) {
										target.setWhitelisted(false);

										operator.sendMessage(chat.msg(manager.getMessage("PlayerUnWhitelisted"), target,
												operator, exe, command));

									} else {
										target.setWhitelisted(true);

										operator.sendMessage(chat.msg(manager.getMessage("PlayerWhitelisted"), target,
												operator, exe, command));
									}

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("WhitelistNotOn"), target,
											operator, exe, command));
								}

								break;
							case "freeze":
								if (!target.hasPermission("slashplayer.exempt.freeze")) {
									pData.set(pUuid + ".IsFrozen", true);

									target.sendMessage(
											chat.msg(manager.getMessage("Frozen"), target, operator, exe, command));

									operator.sendMessage(chat.msg(manager.getMessage("FreezePlayer"), target, operator,
											exe, command));

									if (plugin.getConfig().getBoolean("Freeze.AdventureMode")) {
										target.setGameMode(GameMode.ADVENTURE);
									}

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("PlayerExempt"), target, operator,
											exe, command));
								}

								break;
							case "unfreeze":
								pData.set(pUuid + ".IsFrozen", false);

								if (plugin.getConfig().getBoolean("Freeze.AdventureMode")) {
									target.setGameMode(plugin.server.getDefaultGameMode());
								}

								target.sendMessage(
										chat.msg(manager.getMessage("Unfrozen"), target, operator, exe, command));

								operator.sendMessage(
										chat.msg(manager.getMessage("UnfreezePlayer"), target, operator, exe, command));

								break;
							case "mute":

							case "unmute":

							case "ban":
								if (!target.hasPermission("slashplayer.exempt.ban")) {
									BanControl banControl = new BanControl();
									int defaultTime = plugin.config.getInt("BanTime");

									banControl.banPlayer(targetPlayer, null, defaultTime);

									String banBroadcast = plugin.manager.getMessage("BanBroadcast");
									boolean broadcast = plugin.config.getBoolean("Broadcast.Ban");
									if (broadcast) {
										for (Player p : plugin.players.values()) {
											if (p.hasPermission("slashplayer.notify")) {
												p.sendMessage(chat.msg(banBroadcast, target, operator, exe, command));

											}
										}
									}

									operator.sendMessage(
											chat.msg(manager.getMessage("BanPlayer"), target, operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(manager.getMessage("PlayerExempt"), target, operator,
											exe, command));

								}

								break;
							case "unban":

							}

							plugin.logAction(operator.getName() + " ran executable: " + exe.toUpperCase() + " on "
									+ target.getName());

							if (verbose) {
								plugin.console.sendMessage(chat.m("%prefix% " + operator.getName() + " ran executable: "
										+ exe.toUpperCase() + " on " + target.getName()));
							}

						} else {
							operator.sendMessage(chat.m("%prefix% &cInvalid Executable! Check console for details."));

							plugin.console.sendMessage(chat.m("%prefix% &cInvalid Executable&8: &7" + executable
									+ " &cCheck your &7GuiConfig.yml &cfile, and make sure they are all setup correctly."));
							return false;
						}

					} else {
						String lowRank = messages.getString("RankTooLow").toString();
						operator.sendMessage(chat.m(lowRank));
					}

				} else {

				}

			} else {
				operator.sendMessage(chat.m(cantUseExe.replace("%executable%", handler.exeType(executable))));
			}

			plugin.savePlayerData(pData);

			if (closeInv) {
				operator.closeInventory();
			}

		}

		return true;
	}

}
