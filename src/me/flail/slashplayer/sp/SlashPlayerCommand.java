package me.flail.slashplayer.sp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class SlashPlayerCommand extends Logger {
	private CommandSender sender;
	private Command command;
	private String[] args;


	public SlashPlayerCommand(CommandSender sender, Command command, String[] args) {
		this.sender = sender;
		this.command = command;
		this.args = args;
	}

	public boolean run() {
		Message noPermission = new Message("NoPermission");
		Message defaultUsage = Message.construct("%prefix% &7Use &e/slashplayer help &7for help on commands.");
		Message invalidPlayer = new Message("InvalidPlayer");

		if (command.getName().equalsIgnoreCase("slashplayer")) {
			if (!(sender instanceof Player)) {
				console("&cYou must use SlashPlayer commands in-game!");
				return true;
			}
			User operator = new User(((Player) sender).getUniqueId());

			if ((args.length > 0) && args[0].equalsIgnoreCase("test")) {
				if (operator.hasPermission("slashplayer.op")) {
					String s = operator.player().getLocation().getBlockY() + "";
					operator.player().sendMessage(s);

				}

				return true;
			}

			if (!operator.hasPermission("slashplayer.command") && !operator.hasPermission("slashplayer.rank")
					&& !operator.hasPermission("slashplayer.report")) {
				noPermission.send(operator, null);

				return true;
			}

			switch (args.length) {
			case 0:
				if (operator.hasPermission("slashplayer.command")) {
					new Gui(plugin.loadedGuis.get("PlayerListGui.yml")).open(operator, null);
					break;
				}

				noPermission.send(operator, null);
				break;
			case 1:
				switch (args[0].toLowerCase()) {
				case "rank":
					new Message("RankCheck").send(operator, null);
					break;
				case "help":
					plugin.sendHelp(operator, command.getName());
					break;
				case "reload":
					if (operator.hasPermission("slashplayer.op")) {
						plugin.reload();
						new Message("ReloadMessage").send(operator, null);
						break;
					}
					noPermission.send(operator, null);
					break;
				case "report":
					if (operator.hasPermission("slashplayer.report")) {
						Message reportUsage = Message
								.construct("%prefix% &cProper usage&8: &7/slashplayer report <player> [<reason>]");

						reportUsage.send(operator, null);
						break;
					}

					break;
				case "reports":
					if (operator.isStaff()) {
						Gui reportList = new Gui(plugin.loadedGuis.get("ReportGui.yml"));

						reportList.open(operator, null);
						break;
					}

					noPermission.send(operator, operator);
					break;
				case "unban":
					if (operator.hasPermission("slashplayer.ban")) {
						Message usage = Message
								.construct("%prefix% &cProper usage&8: &7/slashplayer unban [player-name]");

						usage.send(operator, null);
						break;
					}

					break;
				case "opengui":
					Message usage = Message
					.construct("%prefix% &cProper usage&8: &7/slashplayer opengui <gui-name> [player-name]");

					usage.send(operator, null);
					break;
				case "whitelist":
					usage = Message.construct("%prefix% &cProper usage&8: &7/slashplayer whitelist [<player-name>]");

					usage.send(operator, null);
					break;
				default:
					plugin.userGui(operator, args);
				}

				break;
			case 2:
				switch (args[0].toLowerCase()) {
				case "rank":
					Player player = plugin.server.getPlayer(args[1]);
					if (player != null) {
						User user = new User(player.getUniqueId());

						if (operator.hasPermission("slashplayer.rank")) {
							Message rankMsg = new Message("RankCheckOther");
							rankMsg = rankMsg.placeholders(user.commonPlaceholders());

							rankMsg.send(operator, null);
							break;
						}

						noPermission.send(operator, null);
						break;
					}

					invalidPlayer.replace("%player%", args[1]).send(operator, operator);
					break;
				case "help":
					plugin.sendHelp(operator, command.getName());

					break;
				case "whitelist":
					if (operator.hasPermission("slashplayer.whitelist")) {

						if (!plugin.server.hasWhitelist()) {
							new Message("WhitelistNotOn").send(operator, null);
							break;
						}
						User offlineUser = SlashPlayer.offlinePlayer(args[1]);
						offlineUser.offlinePlayer().setWhitelisted(true);
						break;
					}

					noPermission.send(operator, null);
					break;
				case "unban":
					if (operator.hasPermission("slashplayer.ban")) {
						User offlineUser = SlashPlayer.offlinePlayer(args[1]);
						offlineUser.unban();

						new Message("UnbanPlayer").placeholders(offlineUser.commonPlaceholders()).send(operator, operator);
						break;
					}

					noPermission.send(operator, null);
					break;
				case "report":
					if (operator.hasPermission("slashplayer.report")) {
						Message reportUsage = Message
								.construct("%prefix% &cProper usage&8: &7/slashplayer report " + args[1] + " [<reason>]");

						reportUsage.send(operator, null);
						break;
					}
					break;
				case "opengui":
					Message usage = Message
					.construct("%prefix% &cProper usage&8: &7/slashplayer opengui " + args[1] + " [player-name]");

					usage.send(operator, null);
					break;
				default:

					defaultUsage.send(operator, null);
				}

			case 3:
				switch (args[0].toLowerCase()) {
				case "opengui":
					for (String s : plugin.guiFiles) {
						if (s.equals(args[1])) {
							if (plugin.loadedGuis.containsKey(args[1])) {
								Gui gui = new Gui(plugin.loadedGuis.get(args[1]));
								User subject = User.fromName(args[2]);

								gui.open(operator, subject);
							}

							break;
						}
					}

					break;
				case "report":
					Message explainItBoi = new Message("ReportPrompt");

					explainItBoi.send(operator, null);
					break;
				}

				break;
			default:
				switch (args[0].toLowerCase()) {
				case "report":
					if (operator.hasPermission("slashplayer.report")) {

						if (args.length > 3) {
							User subject = User.fromName(args[1]);
							if (operator.id().equals(subject.id()) && !operator.hasPermission("slashplayer.op")) {
								operator.sendMessage("%prefix% &cYou can't report yourself!");

								break;
							}

							if (!operator.id().equals(subject.id())) {
								if (((operator.rank() < subject.rank())
										&& plugin.config.getBoolean("EqualsCanExecute", false))
										|| (operator.rank() <= subject.rank())) {

									new Message("ReportDeny").placeholders(subject.commonPlaceholders()).send(operator, operator);
									break;
								}

							}

							String reportReason = this.convertArray(args, 2);

							subject.report(operator, reportReason);

							new Message("ReportSuccess").placeholders(subject.commonPlaceholders()).send(operator, operator);

							Message reportedMsg = new Message("PlayerReported").placeholders(subject.commonPlaceholders());
							for (User user : plugin.players.values()) {
								if (user.isStaff()) {
									reportedMsg.send(user, operator);
								}
							}

							reportedMsg.log(subject, operator);
							reportedMsg.console(subject, operator);

						}

						break;
					}

					noPermission.send(operator, operator);
					break;
				default:

				}

			}

			return true;
		}

		if (command.getName().equals("ouch")) {
			if (sender instanceof Player) {
				User user = new User(((Player) sender).getUniqueId());
				user.ouch();
				return true;
			}
		}

		return sender != null;
	}

}
