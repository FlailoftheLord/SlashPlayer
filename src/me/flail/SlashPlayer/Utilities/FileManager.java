package me.flail.SlashPlayer.Utilities;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flail.SlashPlayer.SlashPlayer;

public class FileManager {
	SlashPlayer plugin;

	public FileConfiguration config;

	public FileManager(SlashPlayer inst) {
		plugin = inst;

		config = plugin.getConfig();
	}

	public String getMessage(String path) {
		return this.getFile(plugin, "Messages.yml").get(path).toString();

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
				plugin.saveResource(fileName, false);

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
