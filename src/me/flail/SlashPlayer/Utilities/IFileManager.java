package me.flail.SlashPlayer.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flail.SlashPlayer.SlashPlayer;

public class IFileManager {

	SlashPlayer plugin;

	public IFileManager(SlashPlayer plugin) {
		this.plugin = plugin;
	}

	public FileConfiguration getConfig(String fileName) {
		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		File file = new File(plugin.getDataFolder() + "/" + fileName);
		FileConfiguration config = new YamlConfiguration();

		this.loadConfig(fileName);

		try {
			config.load(file);

		} catch (Throwable t) {
		}

		return config;
	}

	public boolean loadConfig(String fileName) {
		if (!fileName.endsWith(".yml")) {
			fileName = fileName.concat(".yml");
		}

		try {

			File file = new File(plugin.getDataFolder() + "/" + fileName);
			FileConfiguration config = new YamlConfiguration();
			if (!file.exists()) {
				plugin.saveResource(fileName, false);
			}

			file = new File(plugin.getDataFolder() + "/" + fileName);

			config.load(file);
			config.save(file);

			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean saveConfig(String fileName, FileConfiguration config) {
		try {

			if (!fileName.endsWith(".yml")) {
				fileName = fileName.concat(".yml");
			}

			File settingsFile = new File(plugin.getDataFolder(), fileName);

			if (!settingsFile.exists()) {
				this.loadConfig(fileName);
			}

			if (config != null) {

				FileConfiguration settingsConfig = config;

				try {
					settingsConfig.save(settingsFile);
				} catch (Throwable e) {
				}

			} else {
			}

			return true;
		} catch (Throwable t) {
			return false;
		}

	}

	public void log(String msg) {
		BufferedWriter logs = null;

		Time time = new Time();
		Tools tools = new Tools();

		try {
			// create a temporary file
			String timeLog = time.monthName(Calendar.MONTH) + " "
					+ new SimpleDateFormat("dd_yyyy").format(Calendar.getInstance().getTime()).toString();

			boolean createFile = new File(plugin.getDataFolder() + "/logs").mkdirs();

			if (createFile || (createFile == false)) {

				File logFile = new File(plugin.getDataFolder() + "/logs/" + timeLog + ".txt");

				logs = new BufferedWriter(new FileWriter(logFile, true));
				logs.newLine();
				logs.write(time.currentDayTime() + " " + tools.m(msg));

				// console.sendMessage("Logging worked!");

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				logs.close();

				// console.sendMessage("logs closed");

			} catch (Exception e) {
			}

		}

	}

}
