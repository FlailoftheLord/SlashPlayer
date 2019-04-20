package me.flail.SlashPlayer.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.flail.SlashPlayer.SlashPlayer;

public class TabCompleter extends ArrayList<String> {
	private SlashPlayer plugin = SlashPlayer.instance;
	/**
	 * UID
	 */
	private static final long serialVersionUID = 43097891222L;
	private Command command;

	public TabCompleter(Command command) {
		this.command = command;
	}

	public TabCompleter construct(String[] args) {
		if (command.getName().equalsIgnoreCase("slashplayer")) {
			List<String> baseArgs = new ArrayList<>();
			for (Player p : plugin.players.values()) {
				baseArgs.add(p.getName());
			}

			switch (args.length) {
			case 1:
				baseArgs.add("reload");
				baseArgs.add("report");
				baseArgs.add("rank");
				for (String s : baseArgs) {
					if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
						this.add(s);
					}
				}
				break;
			case 2:
				switch (args[0].toLowerCase()) {
				default:
					for (String s : baseArgs) {
						if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
							this.add(s);
						}
					}

				}
				break;
			case 3:
				if (args[0].equalsIgnoreCase("report")) {
					this.add("[<Report-Reason>]");
				}
				break;
			}

		}

		return this;
	}

}
