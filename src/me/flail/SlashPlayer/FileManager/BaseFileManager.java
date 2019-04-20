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
		fileName = yamlFile(fileName);

		File settingsFile = new File(plugin.getDataFolder() + "/" + fileName);

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
		fileName = yamlFile(fileName);

		File settingsFile = new File(plugin.getDataFolder() + "/" + fileName);

		if (!settingsFile.exists()) {
			try {
				settingsFile.createNewFile();
				plugin.saveResource(fileName, true);

			} catch (Throwable e) {
			}
		}

	}

	public void saveFile(FileConfiguration file) {
		try {
			String fileName = yamlFile(file.getName());

			File settingsFile = new File(plugin.getDataFolder() + "/" + fileName);

			file.save(settingsFile);

		} catch (Throwable e) {
		}

	}

	private String yamlFile(String fileName) {
		if (!fileName.endsWith(".yml") && !fileName.isEmpty()) {
			fileName = fileName.concat(".yml");
		}
		return fileName;
	}
}
