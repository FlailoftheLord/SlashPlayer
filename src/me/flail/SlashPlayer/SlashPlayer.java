package me.flail.SlashPlayer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.Executables.Executables;
import me.flail.SlashPlayer.Listeners.BanTimer;
import me.flail.SlashPlayer.Listeners.FreezeListener;
import me.flail.SlashPlayer.Listeners.MuteListener;
import me.flail.SlashPlayer.Listeners.MuteTimer;
import me.flail.SlashPlayer.Listeners.PlayerListGui;
import me.flail.SlashPlayer.Listeners.SetGamemode;

public class SlashPlayer extends JavaPlugin {

	private File guiConfigFile;
	private FileConfiguration guiConfig;

	private File pDataFile;
	private FileConfiguration pDataConfig;

	private File messagesFile;
	private FileConfiguration messagesConfig;

	public ConsoleCommandSender console = Bukkit.getConsoleSender();

	private PluginManager pm = getServer().getPluginManager();

	public Server server = this.getServer();

	private String version = getDescription().getVersion();

	private String serverVersion = getServer().getBukkitVersion();

	@Override
	public void onEnable() {

		// Load up the Files
		saveDefaultConfig();
		loadGuiConfig();
		loadPlayerData();
		loadMessages();

		// Register Commands and Events
		handleAliases();
		registerCommands();

		registerEvents();

		// Begin the Ban and Mute Timers
		new BanTimer().runTaskTimer(this, 100, 1200);
		new MuteTimer().runTaskTimer(this, 100, 1200);

		// Friendly console spam :>
		console.sendMessage("SlashPlayer running under " + serverVersion);
		console.sendMessage(ChatColor.BLUE + "=============================");
		console.sendMessage(ChatColor.YELLOW + " SlashPlayer " + ChatColor.GOLD + "v" + version);
		console.sendMessage(ChatColor.DARK_GREEN + "   by FlailoftheLord");
		console.sendMessage(ChatColor.YELLOW + " An easy, modern way,");
		console.sendMessage(ChatColor.YELLOW + " to manage your players!");
		console.sendMessage(ChatColor.BLUE + "=============================");

	}

	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);
		savePlayerData();

	}

	private void handleAliases() {

		boolean spcmdExists = server.getCommandAliases().containsKey("sp");
		boolean playercmdExists = server.getCommandAliases().containsKey("player");
		if (spcmdExists) {
			server.getCommandAliases().remove("sp");
		}

		if (playercmdExists) {
			server.getCommandAliases().remove("player");
		}

	}

	public void registerCommands() {

		getCommand("slashplayer").setExecutor(new Commands());

	}

	public void registerEvents() {
		pm.registerEvents(new PlayerListGui(), this);
		pm.registerEvents(new Executables(), this);
		pm.registerEvents(new SetGamemode(), this);
		pm.registerEvents(new FreezeListener(), this);
		pm.registerEvents(new PlayerDataSetter(), this);
		pm.registerEvents(new MuteListener(), this);
	}

	public FileConfiguration getMessages() {
		if (messagesConfig == null) {
			loadMessages();
		}
		return messagesConfig;
	}

	public void loadMessages() {

		messagesFile = new File(getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			messagesFile.getParentFile().mkdirs();
			saveResource("Messages.yml", false);
		}

		messagesConfig = new YamlConfiguration();
		try {
			messagesConfig.load(messagesFile);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Could not load " + messagesFile, e);
		}

	}

	public void saveMessages() {
		if ((messagesConfig == null) || (messagesFile == null)) {
			return;
		}

		try {
			getMessages().save(messagesFile);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Could not save " + messagesFile, e);
		}
	}

	public FileConfiguration getGuiConfig() {
		if (guiConfig == null) {
			loadGuiConfig();
		}
		return guiConfig;
	}

	public void loadGuiConfig() {
		guiConfigFile = new File(getDataFolder(), "GuiConfig.yml");
		if (!guiConfigFile.exists()) {
			guiConfigFile.getParentFile().mkdirs();
			saveResource("GuiConfig.yml", false);
		}

		guiConfig = new YamlConfiguration();
		try {
			guiConfig.load(guiConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not load " + guiConfigFile, e);
		}
	}

	public void saveGuiConfig() {
		if ((guiConfig == null) || (guiConfigFile == null)) {
			return;
		}

		try {
			getGuiConfig().save(guiConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not save " + guiConfigFile, e);
		}

	}

	public FileConfiguration getPlayerData() {
		if (pDataConfig == null) {
			loadPlayerData();
		}
		return pDataConfig;
	}

	public void loadPlayerData() {
		pDataFile = new File(getDataFolder(), "PlayerData.yml");
		if (!pDataFile.exists()) {
			pDataFile.getParentFile().mkdirs();
			saveResource("PlayerData.yml", false);
		}

		pDataConfig = new YamlConfiguration();
		try {
			pDataConfig.load(pDataFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not load " + pDataFile, e);
		}
	}

	public void savePlayerData() {
		if ((pDataConfig == null) || (pDataFile == null)) {
			return;
		}

		try {
			getPlayerData().save(pDataFile);
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not save " + pDataConfig, e);
		}

	}

}