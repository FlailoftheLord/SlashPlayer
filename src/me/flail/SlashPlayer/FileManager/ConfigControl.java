package me.flail.SlashPlayer.FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flail.SlashPlayer.SlashPlayer;

public class ConfigControl {

	SlashPlayer plugin = SlashPlayer.instance;

	private List<String> configLines = new ArrayList<>();

	public FileConfiguration get(boolean overwrite) {
		this.load(overwrite);

		return plugin.manager.getFile("config.yml");
	}

	public boolean load(boolean overwrite) {
		try {
			File file = new File(plugin.getDataFolder(), "config.yml");

			FileConfiguration config = new YamlConfiguration();
			config.load(file);

			if (!file.exists() || config.contains("ConfigVersion")) {
				this.forceUpdate();

			} else if (overwrite) {
				this.forceUpdate();
			}

			return true;
		} catch (Throwable t) {
			return false;
		}

	}

	private boolean forceUpdate() {
		try {
			File config = new File(plugin.getDataFolder() + "/config.yml");
			config.createNewFile();
			setterUpper();

			BufferedWriter writer = new BufferedWriter(new FileWriter(config));
			writer.write(header);
			for (String line : configLines) {
				writer.append(line + "\n");
			}

			writer.flush();
			writer.close();

			return true;
		} catch (Throwable t) {
			return false;
		}

	}

	private void setterUpper() {

		String[] lines = { " ", "# Prefix for messages and commands.", "Prefix: \"&8(&3&lSP&8)\"", " ",
				"# NOTE: Ranks go from 0 - 100 (in which 0 is the lowest rank) and can be applied to a user with the permission:",
				"#   slashplayer.rank.#   - replace # with any number from 0 - 100, ",
				"# If a player does not have a rank permisison, their rank will default to 0.", " ",
				"# Wether players of the same ranking can execute on each other.", "EqualsCanExecute: true", " ",
				"# Kick the player if they've been un-whitelisted?", "KickOnUnWhitelist: true", " ",
				"# Fill empty space in GUI's?", "# You can set the filler item type in the 'GuiConfig.yml' file.",
				"FillEmptySpace: true", " ", "# Title for the player-list GUI",
				"PlayerListTitle: \"&2&lOnline Players\"", " ", "# Title for the player moderation menu.",
				"# Use the %player% placeholder for their name.", "PlayerMenuTitle: \"&2&l%player%\"", " ",
				"# Used for the %website% placeholder.", "Website: \"www.myserver.com/ban-apppeals\"",
				"# The time to ban the player for in Minutes.", "# 1 hour = 60 minutes",
				"# 1 day = 24 hours = 1440 minutes", "# 1 week = 7 days = 10080 minutes", "BanTime: 60", " ",
				"# Mute time in minutes.", "MuteTime: 5", " ", "# Settings for frozen players.",
				"# Use 'deny' or 'allow' to specify.",
				"# If you set adveture mode to true, it will put the player into adventure gamemode while they are frozen",
				"Frozen:", "  AdventureMode: true", "  Interact: deny", "  Chat: deny", " ",
				"# Broadcast options, enable or disable for each one.", "Broadcast: ", "  Kick: false", "  Mute: false",
				"  Ban: true", " ", "# More console output?",
				"# Note: All actions are logged in the logs files located under /logs/[Date]", "ConsoleVerbose: true",
				" ", "# Force update your config file? ",
				"# Setting this to true will allow the plugin to update your config file to the latest settings.",
				"# Doing this, though, will reset all settings to default.",
				"# A copy of your old config will be saved to the location: 'old-config.yml'",
				"ForceConfigUpdates: false", " ", " ", " ", " ",
				"# No touchy pls, unless you want the plugin to go boom-boom.  :>",
				"ConfigVersion: " + plugin.version.replace(".", "")

		};

		for (String line : lines) {
			configLines.add(line);
		}

	}

	public static String header = "#-----------------------------------------------------------------\n"
			+ "#==================================================================#\n"
			+ "#                                                                  #\n"
			+ "#                 Plugin by FlailoftheLord.                        #\n"
			+ "#        -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-                   #\n"
			+ "#       For questions please join my discord server:               #\n"
			+ "#                https://discord.gg/wuxW5PS                        #\n"
			+ "#   ______               __        _____                           #\n"
			+ "#   |       |           /  \\         |        |                    #\n"
			+ "#   |__     |          /____\\        |        |                    #\n"
			+ "#   |       |         /      \\       |        |                    #\n"
			+ "#   |       |_____   /        \\    __|__      |______              #\n"
			+ "#                                                                  #\n"
			+ "#==================================================================#\n"
			+ "#-----------------------------------------------------------------\n" + " ";

}
