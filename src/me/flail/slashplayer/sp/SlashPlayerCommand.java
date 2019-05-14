package me.flail.slashplayer.sp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class SlashPlayerCommand extends Logger {
	private CommandSender operator;
	private Command command;
	private String label;
	private String[] args;


	public SlashPlayerCommand(CommandSender operator, Command command, String label, String[] args) {
		this.operator = operator;
		this.command = command;
		this.label = label;
		this.args = args;
	}

	public boolean run() {
		if (command.getName().equalsIgnoreCase("slashplayer")) {
			switch (label) {

			default:
				if ((args.length > 0) && args[0].equalsIgnoreCase("test")) {
					if (operator instanceof Player) {
						Player player = (Player) operator;
						User user = new User(player.getUniqueId());
						new Gui(plugin.loadedGuis.get("PlayerListGui.yml")).open(user, null);
					}

				}
			}

			return true;
		}
		if (command.getName().equals("ouch")) {
			if (operator instanceof Player) {
				Player player = (Player) operator;
				User user = new User(player.getUniqueId());
				user.ouch();
			}
		}

		return operator != null;
	}

}
