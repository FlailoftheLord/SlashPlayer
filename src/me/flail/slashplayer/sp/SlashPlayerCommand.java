package me.flail.slashplayer.sp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.flail.slashplayer.tools.Logger;

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

			}

		}
		return operator != null;
	}

}
