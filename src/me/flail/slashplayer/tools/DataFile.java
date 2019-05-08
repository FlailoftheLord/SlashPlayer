package me.flail.slashplayer.tools;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.slashplayer.SlashPlayer;

public class DataFile extends Logger {
	protected static SlashPlayer plugin;
	private File file;
	private FileConfiguration config = new YamlConfiguration();

	public DataFile(String path) {
		try {
			plugin = JavaPlugin.getPlugin(SlashPlayer.class);
			file = new File(plugin.getDataFolder() + "/" + path);
			if (!file.exists()) {

				file.createNewFile();

			}

			config.load(file);
		} catch (Exception e) {
		}
	}

	public void load() {
		try {
			config.save(file);
		} catch (Exception e) {
		}
	}

	public void loadFromPlugin(String fileName) {
		plugin.saveResource(fileName, false);
	}

	public DataFile save(FileConfiguration config) {
		try {
			config.save(file);
		} catch (Throwable t) {
			this.console("&cError while saving configuration: " + config.getName() + " to: " + file.getPath());
		}
		return this;
	}

	/**
	 * Sets the specified <code>value</code> under the defined <code>key</code> in the data file.
	 * 
	 * @param key
	 * @param value
	 * @return the {@link DataFile} to which this value was set.
	 */
	public DataFile setValue(String key, Object value) {
		config.set(key, value);
		return save(config);
	}

	public Object getObj(String key) {
		return config.get(key);
	}

	public String getValue(String key) {
		return getObj(key).toString();
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(getValue(key)).booleanValue();
	}

	public List<String> getList(String key) {
		return config.getStringList(key);
	}

	public String[] getArray(String key) {
		return getValue(key).replace("[", "").replace("]", "").split(", ");
	}

	public DataFile setHeader(String string) {
		config.options().header(string);
		return save(config);
	}

	/**
	 * Checks wether this DataFile contains the specified <code>key</code> value.
	 * 
	 * @param key
	 * @return true if found, false otherwise.
	 */
	public boolean hasValue(String key) {
		return config.contains(key);
	}

}
