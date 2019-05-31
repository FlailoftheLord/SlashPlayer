package me.flail.slashplayer.executables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;
import me.flail.slashplayer.user.User.KickReason;

public class Executioner extends Logger {
	private User subject;
	private User operator;
	private Exe executable;
	private String logMsg;

	/**
	 * @param subject
	 *                       the poor unfortunate of this calamity
	 * @param executable
	 *                       whatever calamity to make befall this sorry subject
	 */
	public Executioner(User subject, Exe executable) {
		this.subject = subject;
		this.executable = executable;
	}

	/**
	 * @param operator
	 *                     the glorified butcher
	 * @return true if operation was sucessful
	 */
	public boolean execute(User operator) {
		this.operator = operator;

		return execute(executable, subject, this.operator);
	}

	private boolean execute(Exe exe, User subject, User operator) {
		logMsg = " User: " + operator.name() + " ran executable: " + exe.toString() + " on " + subject.name();

		Map<String, String> exePlaceholders = new HashMap<>();
		exePlaceholders.put("%executable%", exe.toString());
		exePlaceholders.put("%operator%", operator.name());

		Message accessDenied = new Message("AccessDenied").placeholders(exePlaceholders);

		if (subject.isOnline() && operator.isOnline()) {
			switch (exe) {
			case BACKBUTTON:
				if (plugin.openGuis.get(operator.uuid()).data().dataFile().name().equals("GamemodeGui.yml")) {
					new GuiControl().openModerationGui(operator, subject);
					break;
				}

				new GuiControl().playerListGui(operator);
				break;
			case BAN:
				if (operator.hasPermission("slashplayer.ban")) {
					if (subject.ban(plugin.config.getLong("BanTime"))) {
						// console("was properly banned");
					}

					if (plugin.config.getBoolean("Broadcast.Ban")) {
						new Message("BanBroadcast").broadcast(subject, operator);
					} else {
						new Message("BanPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
					}

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case BURN:
				if (operator.hasPermission("slashplayer.burn")) {
					subject.burn(true, Integer.MAX_VALUE);
					logAction("a");

					new Message("Burning").send(subject, operator);
					new Message("BurntPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case CLEARINVENTORY:
				break;
			case ENDERCHEST:
				if (operator.hasPermission("slashplayer.enderchest")) {
					operator.player().openInventory(subject.player().getEnderChest());

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case FEED:
				if (operator.hasPermission("slashplayer.feed")) {
					subject.feed(22);
					new Message("Fed").send(subject, operator);
					new Message("FedPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case FLY:
				if (operator.hasPermission("slashplayer.fly")) {
					subject.toggleFly(operator);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case FREEZE:
				if (operator.hasPermission("slashplayer.freeze")) {
					logAction("a");

					if (subject.freeze()) {

						new Message("Frozen").send(subject, operator);
						new Message("FreezePlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
						break;
					}

					new Message("Unfrozen").send(subject, operator);
					new Message("UnfreezePlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case FRIEND:
				if (operator.hasPermission("slashplayer.friend")) {
					subject.spawnNewFriend();

					logAction("a");
					new Message("SpawnedMob").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case GAMEMODE:
				if (operator.hasPermission("slashplayer.gamemode")) {
					new GuiControl().openGamemodeGui(operator, subject);
					return true;
				}

				accessDenied.send(operator, operator);
				break;
			case GAMEMODEADVENTURE:
				this.gamemode(subject, operator, "Adventure", accessDenied);
				break;
			case GAMEMODECREATIVE:
				this.gamemode(subject, operator, "Creative", accessDenied);
				break;
			case GAMEMODESPECTATOR:
				this.gamemode(subject, operator, "Spectator", accessDenied);
				break;
			case GAMEMODESURVIVAL:
				this.gamemode(subject, operator, "Survival", accessDenied);
				break;
			case HEAL:
				if (operator.hasPermission("slashplayer.heal")) {
					subject.heal(true);
					new Message("Healed").send(subject, operator);
					new Message("HealedPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case KICK:
				if (operator.hasPermission("slashplayer.kick")) {
					subject.kick(KickReason.CUSTOM);
					logAction("a");

					if (plugin.config.getBoolean("Broadcast.Kick")) {
						new Message("KickBroadcast").broadcast(subject, operator);
						break;
					}

					new Message("PlayerKicked").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case KILL:
				if (operator.hasPermission("slashplayer.kill")) {
					if (subject.hasPermission("slashplayer.exempt")) {
						new Message("CantKillPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);

						logAction("d");
						break;
					}

					Message killMsg = new Message("Killed").placeholders(subject.commonPlaceholders());
					subject.kill(killMsg);

					new Message("KilledPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case MUTE:
				if (operator.hasPermission("slashplayer.mute")) {
					subject.mute(plugin.config.getLong("MuteTime", 300L));
					logAction("a");

					new Message("Muted").send(subject, operator);

					if (plugin.config.getBoolean("Broadcast.Mute")) {
						new Message("MuteBroadcast").broadcast(subject, operator);
						break;
					}

					new Message("PlayerMuted").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case OPENINVENTORY:
				if (operator.hasPermission("slashplayer.openinventory")) {
					operator.player().openInventory(subject.player().getInventory());

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case REPORT:
				Message useCmdToReport = Message.construct("%prefix% &cYou must use the &7/report &ccommand to report players!");

				useCmdToReport.send(operator, null);
				break;
			case RESTOREINVENTORY:
				break;
			case SUMMON:
				break;
			case TELEPORT:
				break;
			case TOGGLEFLY:
				if (operator.hasPermission("slashplayer.fly")) {
					subject.toggleFly(operator);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case UNBAN:
				;
			case UNFREEZE:
				if (operator.hasPermission("slashplayer.freeze")) {
					logAction("a");

					if (subject.freeze()) {

						new Message("Frozen").send(subject, operator);
						new Message("FreezePlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
						break;
					}

					new Message("Unfrozen").send(subject, operator);
					new Message("UnfreezePlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case UNMUTE:
				if (operator.hasPermission("slashplayer.mute")) {
					logAction("a");
					if (subject.isMuted()) {
						subject.unmute();
						new Message("Unmuted").send(subject, operator);

						new Message("PlayerUnmuted").placeholders(subject.commonPlaceholders()).send(operator, operator);
						break;
					}

					new Message("PlayerNotMuted").placeholders(subject.commonPlaceholders()).send(operator, operator);
					break;
				}

				logAction("d");
				accessDenied.send(operator, operator);
				break;
			case WHITELIST:
				whitelist(subject, operator, accessDenied);
				break;
			}

			return true;
		}

		if (exe.equals(Exe.WHITELIST)) {
			whitelist(subject, operator, accessDenied);
			return true;
		}

		if (exe.equals(Exe.UNBAN)) {
			if (operator.hasPermission("slashplayer.ban")) {
				subject.unban();
				logAction("a");

				new Message("UnbanPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);
				return true;
			}

			logAction("d");
			accessDenied.send(operator, operator);
			return true;
		}

		operator.closeGui();
		new Message("InvalidPlayer").placeholders(subject.commonPlaceholders()).send(operator, operator);

		return false;
	}

	private void whitelist(User subject, User operator, Message denyMsg) {
		if (operator.hasPermission("slashplayer.whitelist")) {
			if (plugin.server.hasWhitelist()) {
				subject.offlinePlayer().setWhitelisted(!plugin.server.getWhitelistedPlayers().contains(subject.offlinePlayer()));
				logAction("a");

				if (subject.offlinePlayer().isWhitelisted()) {
					new Message("PlayerWhitelisted").placeholders(subject.commonPlaceholders()).send(operator, operator);
					return;
				}
				new Message("PlayerUnWhitelisted").placeholders(subject.commonPlaceholders()).send(operator, operator);
				return;
			}

			new Message("WhitelistNotOn").send(operator, operator);
			return;
		}

		if (denyMsg != null) {
			denyMsg.send(operator, operator);
			return;
		}

		logAction("d");
		new Message("NoPermission").send(operator, operator);
	}

	private void gamemode(User subject, User operator, String mode, Message denyMsg) {
		if (operator.hasPermission("slashplayer.gamemode." + mode.toLowerCase())) {
			subject.setGamemode(GameMode.valueOf(mode.toUpperCase()));
			new Message("GamemodeChanged." + mode).send(subject, operator);
			new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, operator);

			new GuiControl().openModerationGui(operator, subject);

			logAction("a");
			return;
		}

		logAction("d");
		denyMsg.send(operator, operator);
	}

	private void logAction(String result) {
		switch (result) {
		case "a":
			logMsg = logMsg.concat(" (ALLOWED)");
			break;
		case "d":
			logMsg = logMsg.concat(" (DENIED)");
		}

		this.console(logMsg);
		this.log(logMsg);
	}

}
