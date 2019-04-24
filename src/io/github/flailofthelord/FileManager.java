package io.github.flailofthelord;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager extends Utilities {

	public FileManager(Class<? extends JavaPlugin> mainPlugin) {
		super(mainPlugin);
	}

	public File getFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return file;
	}

	public FileConfiguration configuration(String path) {
		if (!path.endsWith(".yml")) {
			path = path.concat(".yml");
		}
		return YamlConfiguration.loadConfiguration(getFile(path));
	}

	/**
	 * @see FileManager#saveConfig(FileConfiguration config, String path)
	 */
	public boolean saveConfig(FileConfiguration config) {
		return saveConfig(config, JavaPlugin.getPlugin(plugin).getDataFolder().getPath());
	}

	/**
	 * Saves this config to the specified file path.
	 * 
	 * @param config
	 * @param path
	 * @return true if the Configuration was saved, false otherwise.
	 */
	public boolean saveConfig(FileConfiguration config, String path) {
		try {
			File file = new File(path + "/" + config.getName());
			if (file.isFile()) {
				config.save(file);
			}

			return file != null ? file.isFile() : false;
		} catch (Throwable t) {
			return false;
		}

	}

	public void log(String message, String filePath) {

	}

}
