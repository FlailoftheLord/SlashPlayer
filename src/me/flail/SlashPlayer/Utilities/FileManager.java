package me.flail.SlashPlayer.Utilities;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flail.SlashPlayer.SlashPlayer;

public class FileManager {
	SlashPlayer plugin;

	public FileConfiguration config;

	public FileManager(SlashPlayer instance) {
		plugin = instance;

		config = plugin.config;
	}

	public String getMessage(String path) {
		return this.getFile(plugin, "Messages.yml").get(path, "Unknown").toString();

	}

	public FileConfiguration getFile(SlashPlayer plugin, String fileName) {

		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File settingsFile = new File(plugin.getDataFolder(), fileName);

		if (!settingsFile.exists()) {
			this.loadFile(plugin, fileName);
		}

		FileConfiguration config = new YamlConfiguration();

		try {
			config.load(settingsFile);
			return config;
		} catch (Throwable e) {
			return null;
		}

	}

	public void loadFile(SlashPlayer plugin, String fileName) {

		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File settingsFile = new File(plugin.getDataFolder() + "/" + fileName);

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

	public void saveFile(SlashPlayer plugin, String fileName, FileConfiguration file) {

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

		}

		if (!settingsFile.exists()) {
			this.loadFile(plugin, fileName);
		}
		if (file != null) {
			try {
				file.save(settingsFile);
			} catch (Throwable e) {
			}

		} else {
		}

	}

}
