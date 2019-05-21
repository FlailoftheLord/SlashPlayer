package me.flail.slashplayer.tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.user.User;

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

	public TabCompleter construct(String label, String[] args) {
		if (command.getName().equalsIgnoreCase("slashplayer")) {
			List<String> baseArgs = new ArrayList<>();

			switch (args.length) {
			case 1:
				baseArgs.addAll(usernames());
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
				case "report":
					baseArgs.addAll(usernames());
					break;
				case "rank":
					baseArgs.addAll(usernames());
					break;
				case "opengui":
					for (String guiName : plugin.guiFiles) {
						this.add(guiName);
					}
				default:
					baseArgs.clear();
					break;

				}

				for (String s : baseArgs) {
					if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
						this.add(s);
					}
				}


				break;
			case 3:
				switch (args[0].toLowerCase()) {
				case "report":
					this.add("[<Report-Reason>]");
					break;
				case "opengui":
					this.addAll(usernames());
					break;
				}
			}

		}

		return this;
	}

	private List<String> usernames() {
		List<String> names = new ArrayList<>();
		for (User user : plugin.players) {
			names.add(user.name());
		}
		return names;
	}

}
