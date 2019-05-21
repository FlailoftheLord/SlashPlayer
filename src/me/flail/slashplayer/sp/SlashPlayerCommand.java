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

			switch (args.length) {
			case 0:
				if (operator.hasPermission("slashplayer.command")) {
					new Gui(plugin.loadedGuis.get("PlayerListGui.yml")).open(operator, null);
					break;
				}
				new Message("NoPermission").send(operator, null);

			case 1:
				switch (args[0].toLowerCase()) {
				case "rank":
					new Message("RankCheck").send(operator, null);
					break;
				case "help":
					plugin.sendHelp(operator);
					break;
				case "reload":
					if (operator.hasPermission("slashplayer.op")) {
						plugin.reload();
						new Message("Reloaded").send(operator, null);
						break;
					}
					new Message("NoPermission").send(operator, null);
					break;
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

							new Message("NoPermission").send(operator, null);
							break;
						}

					}

					break;
				case "help":
					plugin.sendHelp(operator);
					break;
				case "whitlist":
					if (!plugin.server.hasWhitelist()) {
						new Message("WhitelistNotOn").send(operator, null);
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
