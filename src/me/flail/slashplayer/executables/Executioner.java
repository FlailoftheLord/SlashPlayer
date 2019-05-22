package me.flail.slashplayer.executables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Executioner extends Logger {
	private User subject;
	private User operator;
	private Exe executable;

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
		Map<String, String> exePlaceholders = new HashMap<>();
		exePlaceholders.put("%executable%", exe.toString());

		Message accessDenied = new Message("AccessDenied").placeholders(exePlaceholders);

		if (subject.isOnline() && operator.isOnline()) {
			switch (exe) {
			case BACKBUTTON:
				break;
			case BAN:
				if (operator.hasPermission("slashplayer.ban")) {
					subject.ban(plugin.config.getLong("BanTime"));
					if (plugin.config.getBoolean("Broadcast.Ban")) {

					}
				}

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
					break;
				}

				accessDenied.send(operator, null);
				break;
			case FLY:
				break;
			case FREEZE:
				break;
			case FRIEND:
				break;
			case GAMEMODE:
				if (operator.hasPermission("slashplayer.gamemode")) {
					Gui gmGui = new Gui(plugin.loadedGuis.get("GamemodeGui.yml")).setHeader(subject);

					gmGui.open(operator, subject);
					return true;
				}

				accessDenied.send(operator, null);

				break;
			case GAMEMODEADVENTURE:
				this.gamemode(subject, operator, "a");
				break;
			case GAMEMODECREATIVE:
				this.gamemode(subject, operator, "c");
				break;
			case GAMEMODESPECTATOR:
				this.gamemode(subject, operator, "spectator");
				break;
			case GAMEMODESURVIVAL:
				this.gamemode(subject, operator, "s");
				break;
			case HEAL:
				if (operator.hasPermission("slashplayer.heal")) {
					subject.heal(true);
					new Message("Healed").send(subject, operator);
					new Message("HealedPlayer").placeholders(subject.commonPlaceholders()).send(operator, null);
					break;
				}

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

			this.log(" User: " + operator.name() + " ran executable: " + exe.toString() + " on " + subject.name());

			return true;
		}

		if (exe.equals(Exe.WHITELIST)) {
			whitelist(subject, operator, accessDenied);
		}

		return false;
	}

	private void whitelist(User subject, User operator, Message denyMsg) {
		if (operator.hasPermission("slashplayer.whitelist")) {
			if (plugin.server.hasWhitelist()) {
				subject.offlinePlayer().setWhitelisted(!plugin.server.getWhitelistedPlayers().contains(subject.offlinePlayer()));
			}
			return;
		}

		if (denyMsg != null) {
			denyMsg.send(operator, null);
			return;
		}

		new Message("NoPermission").send(operator, null);
	}

	private void gamemode(User subject, User operator, String mode) {
		switch (mode) {
		case "a":
			if (operator.hasPermission("slashplayer.gamemode.adventure")) {
				subject.player().setGameMode(GameMode.ADVENTURE);
				new Message("GamemodeChanged.Adventure").send(subject, operator);
				new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, null);
				break;
			}

			new Message("GamemodeAccessDenied").placeholders(subject.commonPlaceholders()).send(operator, null);
			break;
		case "c":
			if (operator.hasPermission("slashplayer.gamemode.creative")) {
				subject.player().setGameMode(GameMode.CREATIVE);
				new Message("GamemodeChanged.Creative").send(subject, operator);
				new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, null);
				break;
			}

			new Message("GamemodeAccessDenied").placeholders(subject.commonPlaceholders()).send(operator, null);
			break;
		case "s":
			if (operator.hasPermission("slashplayer.gamemode.survival")) {
				subject.player().setGameMode(GameMode.SURVIVAL);
				new Message("GamemodeChanged.Survival").send(subject, operator);
				new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, null);
				break;
			}

			new Message("GamemodeAccessDenied").placeholders(subject.commonPlaceholders()).send(operator, null);
			break;
		case "spectator":
			if (operator.hasPermission("slashplayer.gamemode.spectator")) {
				subject.player().setGameMode(GameMode.SPECTATOR);
				new Message("GamemodeChanged.Spectator").send(subject, operator);
				new Message("PlayerGamemodeChanged").placeholders(subject.commonPlaceholders()).send(operator, null);
				break;
			}

			new Message("GamemodeAccessDenied").placeholders(subject.commonPlaceholders()).send(operator, null);
			break;
		}
	}

}
