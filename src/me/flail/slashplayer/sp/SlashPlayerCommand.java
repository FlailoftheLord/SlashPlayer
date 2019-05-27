package me.flail.slashplayer.sp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

		if (command.getName().equalsIgnoreCase("slashplayer")) {
			if (!(sender instanceof Player)) {
				console("&cYou must use SlashPlayer commands in-game!");
				return true;
			}
			User operator = new User(((Player) sender).getUniqueId());

			if ((args.length > 0) && args[0].equalsIgnoreCase("test")) {
				if (operator.hasPermission("slashplayer.op")) {
					new Gui(plugin.loadedGuis.get("PlayerListGui.yml")).open(operator, null);
				}

			}

			if (!operator.hasPermission("slashplayer.command") && !operator.hasPermission("slashplayer.rank")) {
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

				default:
					plugin.userGui(operator, args);
				}

				break;
			case 2:
				switch (args[0].toLowerCase()) {
				case "rank":
					for (User user : plugin.players) {
						if (args[1].toLowerCase().startsWith(user.name())) {
							if (operator.rank() > user.rank()) {
								Message rankMsg = new Message("RankCheck");
								rankMsg = rankMsg.placeholders(user.commonPlaceholders());

								rankMsg.send(operator, null);
								break;
							}

							noPermission.send(operator, null);
							break;
						}

					}

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
						User offlineUser = plugin.offlinePlayer(args[1]);
						offlineUser.offlinePlayer().setWhitelisted(true);
						break;
					}

					noPermission.send(operator, null);
					break;
				case "unban":
					if (operator.hasPermission("slashplayer.ban")) {
						User offlineUser = plugin.offlinePlayer(args[1]);
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

				}


			default:

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
