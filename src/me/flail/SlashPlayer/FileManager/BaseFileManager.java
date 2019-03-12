package me.flail.SlashPlayer.FileManager;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flail.SlashPlayer.SlashPlayer;

public class BaseFileManager {
	SlashPlayer plugin = SlashPlayer.instance;

	protected BaseFileManager() {
	}

	public String getMessage(String path) {
		return this.getFile("Messages.yml").get(path, "Unknown").toString();

	}

	public FileConfiguration getFile(String fileName) {

		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File settingsFile = new File(plugin.getDataFolder(), fileName);

		if (!settingsFile.exists()) {
			this.loadFile(fileName);
		}

		FileConfiguration config = new YamlConfiguration();

		try {
			config.load(settingsFile);
			return config;
		} catch (Throwable e) {
			return null;
		}

	}

	public void loadFile(String fileName) {

		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File settingsFile = new File(plugin.getDataFolder(), fileName);

		if (!settingsFile.exists()) {
			try {
				settingsFile.createNewFile();
				plugin.saveResource(fileName, true);

			} catch (Throwable e) {
			}
		} else {
			settingsFile.mkdirs();
		}

		FileConfiguration settingsConfig = new YamlConfiguration();

		try {
			settingsConfig.load(settingsFile);
			settingsConfig.save(settingsFile);
		} catch (Throwable t) {
		}

	}

	public void saveFile(FileConfiguration file) {

		String fileName = file.getName();

		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File settingsFile = new File(plugin.getDataFolder(), fileName);

		if (fileName.equals("Messages.yml")) {

			file.options().header("Set all messages here\r\n" + "\n Color codes are fully supported... lol\r\n"
					+ "\n\r\n" + "\n If you want to use multiple lines simply set the message \r\n" + "\n from ->\r\n"
					+ "\n  Message: \"%prefix% &aExample messags...\"\r\n" + "\n to ->\r\n" + "\n  Message: |-\r\n"
					+ "\n    %prefix% &aExample messages...\r\n" + "\n     &cthese mssages have two lines.. hehe :>\r\n"
					+ "\n     &a and a thirDDddDd!!!\r\n" + "\n\r\n" + "\n = = = \n Placeholders \n = = = \n\r\n"
					+ "\n %player% the subject of the command\r\n" + "\n %operator% the player doing the action\r\n"
					+ "\n %reporter% the player typing the /report command \r\n"
					+ "\n %ban-duration% the ban time in minutes.\r\n"
					+ "\n %mute-duration% the mute time in minutes.\r\n"
					+ "\n %reason% the reason for the player report.\r\n"
					+ "\n %prefix% the plugin prefix, you can set this in the config.yml file.\r\n"
					+ "\n %command% the name of the command sent.\r\n"
					+ "\n %executable% the name of the executable clicked in the player GUI menu.\r\n"
					+ "\n %gamemode% the current gamemode of %player%\n");

		} else if (fileName.equals("SPconfig.yml")) {
			file.options()
					.header("#-----------------------------------------------------------------\n"
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
							+ "#-----------------------------------------------------------------\n" + " ");
		}

		if (!settingsFile.exists()) {
			this.loadFile(fileName);
		}

		try {
			file.save(settingsFile);
		} catch (Throwable e) {
		}

	}

}
