package me.flail.SlashPlayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.Executables.Executables;
import me.flail.SlashPlayer.Executables.SetGamemode;
import me.flail.SlashPlayer.Listeners.FreezeListener;
import me.flail.SlashPlayer.Listeners.InteractEvent;
import me.flail.SlashPlayer.Listeners.MuteListener;
import me.flail.SlashPlayer.Listeners.PlayerListGui;
import me.flail.SlashPlayer.Listeners.ReportGui;
import me.flail.SlashPlayer.Utilities.BanTimer;
import me.flail.SlashPlayer.Utilities.MuteTimer;
import me.flail.SlashPlayer.Utilities.PlayerDataSetter;
import me.flail.SlashPlayer.Utilities.Time;
import me.flail.SlashPlayer.Utilities.Tools;

public class SlashPlayer extends JavaPlugin implements Listener {

	private File guiConfigFile;
	private FileConfiguration guiConfig;

	private File pDataFile;
	private FileConfiguration pDataConfig;

	private File messagesFile;
	private FileConfiguration messagesConfig;

	private File reportedPlayers;
	private FileConfiguration reportedPlayersConfig;

	public ConsoleCommandSender console = Bukkit.getConsoleSender();

	private PluginManager pm = getServer().getPluginManager();

	public Server server = this.getServer();

	private String version = getDescription().getVersion();

	private String serverVersion = getServer().getBukkitVersion();
	private String serverType = getServer().getVersion();

	public Map<UUID, Player> players = new HashMap<>();

	private BufferedWriter logs = null;

	public SlashPlayer() {
	}

	@Override
	public void onEnable() {

		Tools chat = new Tools();

		// Load up the Files
		saveDefaultConfig();
		loadGuiConfig();
		loadPlayerData();
		loadMessages();

		// Register Commands and Events
		registerCommands();
		pm.registerEvents(this, this);

		registerEvents();

		// Friendly console spam :>
		console.sendMessage("SlashPlayer running under " + serverType + serverVersion);
		console.sendMessage(chat.m("&9============================="));
		console.sendMessage(chat.m(" &eSlashPlayer &6v" + version));
		console.sendMessage(chat.m("   &2by FlailoftheLord"));
		console.sendMessage(chat.m(" &eAn easy, modern way,"));
		console.sendMessage(chat.m(" &eto manage your players!"));
		console.sendMessage(chat.m("&9============================="));

		// Load up the players and store them in a map for later access ;>
		for (Player p : Bukkit.getOnlinePlayers()) {
			UUID pUuid = p.getUniqueId();
			players.put(pUuid, p);
		}

		// And finally initiate the Ban and Mute Timers 5 seconds after startup.
		server.getScheduler().scheduleSyncDelayedTask(this, () -> {

			new BanTimer().runTaskTimerAsynchronously(this, 100, 1200);
			console.sendMessage(chat.m("%prefix% &8Updating bans..."));
			new MuteTimer().runTaskTimerAsynchronously(this, 100, 1200);

		}, 120);

	}

	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);
		saveReportedPlayers();
		savePlayerData();

	}

	@EventHandler
	private void handleAliases(PlayerCommandPreprocessEvent event) {

		String commandLabel = event.getMessage().toLowerCase(Locale.ENGLISH);

		if (commandLabel.equalsIgnoreCase("/sp") || commandLabel.startsWith("/sp ")) {

			event.setMessage(commandLabel.replaceAll("/sp", "/slashplayer"));

		}

		for (Player p : players.values()) {
			String pName = p.getName().toLowerCase();

			if (commandLabel.startsWith("/" + pName)) {
				event.setMessage(commandLabel.replaceAll("(?i)" + Pattern.quote("/" + pName), "/slashplayer " + pName));
				break;
			}

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
		pm.registerEvents(new InteractEvent(), this);
		pm.registerEvents(new ReportGui(), this);
	}

	public FileConfiguration getMessages() {
		if (messagesConfig == null) {
			loadMessages();
		}
		return messagesConfig;
	}

	public void loadMessages() {

		messagesFile = new File(getDataFolder(), "Messages.yml");
		if (!messagesFile.exists()) {
			messagesFile.getParentFile().mkdirs();
			saveResource("Messages.yml", false);
		}

		messagesConfig = new YamlConfiguration();
		try {
			messagesConfig.load(messagesFile);
		} catch (InvalidConfigurationException | IOException e) {
			getLogger().log(Level.SEVERE, "Could not load " + messagesFile, e);
		}

	}

	public void saveMessages() {
		if ((messagesConfig == null) || (messagesFile == null)) {
			return;
		}

		try {
			getMessages().save(messagesFile);
		} catch (IOException | IllegalArgumentException e) {
			getLogger().log(Level.SEVERE, "Could not save " + messagesFile, e);
		}
	}

	public FileConfiguration getReportedPlayers() {
		if (reportedPlayersConfig == null) {
			loadReportedPlayers();
		}
		return reportedPlayersConfig;
	}

	public void loadReportedPlayers() {
		reportedPlayers = new File(getDataFolder(), "ReportedPlayers.yml");
		if (!reportedPlayers.exists()) {
			reportedPlayers.getParentFile().mkdirs();
			saveResource("ReportedPlayers.yml", false);
		}

		reportedPlayersConfig = new YamlConfiguration();
		try {
			reportedPlayersConfig.load(reportedPlayers);
		} catch (InvalidConfigurationException | IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't load " + reportedPlayers, e);
		}

	}

	public void saveReportedPlayers() {
		if ((reportedPlayersConfig == null) || (reportedPlayers == null)) {
			return;
		}

		try {
			getReportedPlayers().save(reportedPlayers);
		} catch (IOException | IllegalArgumentException e) {
			getLogger().log(Level.SEVERE, "Couldn't save file " + reportedPlayers, e);
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

	public void logAction(String msg) {

		Time time = new Time();
		Tools tools = new Tools();

		try {
			// create a temporary file
			String timeLog = time.monthName(Calendar.MONTH) + " "
					+ new SimpleDateFormat("dd_yyyy").format(Calendar.getInstance().getTime()).toString();

			boolean createFile = new File(this.getDataFolder() + "/logs").mkdirs();

			if (createFile || (createFile == false)) {

				File logFile = new File(this.getDataFolder() + "/logs/" + timeLog + ".txt");

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