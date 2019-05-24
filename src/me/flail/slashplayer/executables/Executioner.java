package me.flail.slashplayer.executables;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

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
					subject.ban(plugin.config.getLong("BanTime"));

					if (plugin.config.getBoolean("Broadcast.Ban")) {
						new Message("BanBroadcast").broadcast(subject, operator);
					} else {
						new Message("BanPlayer").placeholders(subject.commonPlaceholders()).send(operator, null);
					}

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, null);
				break;
			case BURN:
				break;
			case CLEARINVENTORY:
				break;
			case ENDERCHEST:
				break;
			case FEED:
				if (operator.hasPermission("slashplayer.feed")) {
					subject.feed(22);
					new Message("Fed").send(subject, operator);
					new Message("FedPlayer").placeholders(subject.commonPlaceholders()).send(operator, null);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, null);
				break;
			case FLY:
				break;
			case FREEZE:
				break;
			case FRIEND:
				subject.spawnNewFriend();

				break;
			case GAMEMODE:
				if (operator.hasPermission("slashplayer.gamemode")) {
					new GuiControl().openGamemodeGui(operator, subject);
					logAction("a");
					return true;
				}

				logAction("d");
				accessDenied.send(operator, null);
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
					new Message("HealedPlayer").placeholders(subject.commonPlaceholders()).send(operator, null);

					logAction("a");
					break;
				}

				logAction("d");
				accessDenied.send(operator, null);
				break;
			case KICK:
				break;
			case KILL:
				break;
			case MUTE:
				break;
			case OPENINVENTORY:
				break;
			case REPORT:
				break;
			case RESTOREINVENTORY:
				break;
			case SUMMON:
				break;
			case TELEPORT:
				break;
			case TOGGLEFLY:
				break;
			case UNBAN:
				break;
			case UNFREEZE:
				break;
			case UNMUTE:
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

		operator.closeGui();
		new Message("InvalidPlayer").placeholders(subject.commonPlaceholders()).send(operator, null);

		return false;
	}

	private void whitelist(User subject, User operator, Message denyMsg) {
		if (operator.hasPermission("slashplayer.whitelist")) {
			if (plugin.server.hasWhitelist()) {
				subject.offlinePlayer().setWhitelisted(!plugin.server.getWhitelistedPlayers().contains(subject.offlinePlayer()));
				logAction("a");
			}
			return;
		}

		if (denyMsg != null) {
			denyMsg.send(operator, null);
			return;
		}

		logAction("d");
		new Message("NoPermission").send(operator, null);
	}

	private void gamemode(User subject, User operator, String mode, Message denyMsg) {
		if (operator.hasPermission("slashplayer.gamemode." + mode.toLowerCase())) {
			subject.player().setGameMode(GameMode.valueOf(mode.toUpperCase()));
			new Message("GamemodeChanged." + mode).send(subject, operator);
			new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, null);

			new GuiControl().openModerationGui(operator, subject);

			logAction("a");
			return;
		}

		logAction("d");
		denyMsg.send(operator, null);
	}

	private void logAction(String result) {
		switch (result) {
		case "a":
			logMsg = logMsg.concat(" (ALLOWED)");
			break;
		case "d":
			logMsg = logMsg.concat(" (DENIED)");
		}

		try {

			this.console(logMsg);
			this.log(logMsg);
		} catch (IOException e) {
		}
	}

}
