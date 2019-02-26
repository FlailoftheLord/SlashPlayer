package me.flail.SlashPlayer.Executables;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.GUI.GamemodeInventory;
import me.flail.SlashPlayer.Utilities.ExeHandler;
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

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration guiConfig = plugin.getGuiConfig();
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
								operator.sendMessage(chat.msg(messages.get("TeleportPlayer").toString(), target,
										operator, exe, command));

								operator.teleport(target);

								break;
							case "summon":
								operator.sendMessage(chat.msg(messages.get("SummonPlayer").toString(), target, operator,
										exe, command));

								target.sendMessage(
										chat.msg(messages.get("Summoned").toString(), target, operator, exe, command));

								target.teleport(operator);

								break;
							case "heal":
								operator.sendMessage(chat.msg(messages.get("HealPlayer").toString(), target, operator,
										exe, command));

								target.sendMessage(
										chat.msg(messages.get("Healed").toString(), target, operator, exe, command));

								for (PotionEffect eff : target.getActivePotionEffects()) {
									target.removePotionEffect(eff.getType());
								}
								target.setHealth(20);

								break;
							case "feed":
								operator.sendMessage(chat.msg(messages.get("FeedPlayer").toString(), target, operator,
										exe, command));
								target.sendMessage(
										chat.msg(messages.get("Fed").toString(), target, operator, exe, command));

								target.setFoodLevel(26);
								break;
							case "fly":

								FlyControl flyControl = new FlyControl();

								if (flyControl.fly(target)) {
									operator.sendMessage(chat.msg(messages.get("PlayerFlyOn").toString(), target,
											operator, exe, command));
									target.sendMessage(
											chat.msg(messages.get("FlyOn").toString(), target, operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(messages.get("PlayerFlyOff").toString(), target,
											operator, exe, command));
									target.sendMessage(chat.msg(messages.get("FlyOff").toString(), target, operator,
											exe, command));

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
												p.sendMessage(chat.msg(messages.get("KickBroadcast").toString()
														.replace("%reason%", reason), target, operator, exe, command));
											}
										}
									} else {
										operator.sendMessage(chat.msg(
												messages.get("PlayerKicked").toString().replace("%reason%", reason),
												target, operator, exe, command));
									}

									target.kickPlayer(chat.msg(reason, target, operator, exe, command));

								} else {
									operator.sendMessage(chat.msg(messages.get("PlayerExempt").toString(), target,
											operator, exe, command));

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

									target.sendMessage(chat.msg(messages.get("Killed").toString(), target, operator,
											exe, command));

									plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

										target.setGameMode(gm);

									}, 20);

								} else {
									operator.sendMessage(chat.msg(messages.get("CantKillPlayer").toString(), target,
											operator, exe, command));

								}

								break;
							case "clearinventory":

							case "openinventory":

							case "enderchest":

							case "whitelist":

							case "freeze":

							case "unfreeze":

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

			if (closeInv) {
				operator.closeInventory();
			}

		}

		return true;
	}

}
