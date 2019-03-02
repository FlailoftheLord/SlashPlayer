package me.flail.SlashPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.ControlCenter.BanControl;
import me.flail.SlashPlayer.ControlCenter.MuteControl;
import me.flail.SlashPlayer.Executables.Executables;
import me.flail.SlashPlayer.Executables.SetGamemode;
import me.flail.SlashPlayer.FileManager.ConfigControl;
import me.flail.SlashPlayer.Listeners.FreezeListener;
import me.flail.SlashPlayer.Listeners.InteractEvent;
import me.flail.SlashPlayer.Listeners.MuteListener;
import me.flail.SlashPlayer.Listeners.PlayerListGui;
import me.flail.SlashPlayer.Listeners.ReportGui;
import me.flail.SlashPlayer.Utilities.FileManager;
import me.flail.SlashPlayer.Utilities.IFileManager;
import me.flail.SlashPlayer.Utilities.PlayerEventHandler;
import me.flail.SlashPlayer.Utilities.Tools;

public class SlashPlayer extends JavaPlugin implements Listener {

	public ConsoleCommandSender console = Bukkit.getConsoleSender();

	public FileManager manager = new FileManager(this);

	public FileConfiguration config = new YamlConfiguration();

	public PluginManager pm = getServer().getPluginManager();

	public Server server = this.getServer();
	public String version = getDescription().getVersion();

	public Map<OfflinePlayer, Integer> banTimer = new HashMap<>();
	public Map<OfflinePlayer, Integer> muteTimer = new HashMap<>();
	public Map<Player, Integer> messageCooldowns = new HashMap<>();

	private String serverVersion = getServer().getBukkitVersion();
	private String serverType = getServer().getVersion();

	public Map<UUID, Player> players = new HashMap<>();

	public static SlashPlayer instance;

	@Override
	public void onEnable() {
		instance = this;

		serverType = serverType.replace(serverType.substring(serverType.indexOf("(")), "");

		Tools chat = new Tools();

		ConfigControl config = new ConfigControl();

		// Load up the Files
		config.load(false);
		this.config = config.get(false);

		loadGuiConfig();
		loadPlayerData();
		loadMessages();

		// Register Commands and Events
		registerCommands();
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
			new MuteControl().loadList();
			new BanControl().loadBanList();
			startTasks();
			console.sendMessage(chat.m("%prefix% &8Updating bans..."));
		}, 120);

	}

	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);
		saveReportedPlayers(this.getReportedPlayers());
		savePlayerData(this.getPlayerData());

		new MuteControl().saveList();
		new BanControl().saveBanList();

	}

	public void startTasks() {
		this.stopTasks();
		server.getScheduler().runTaskLater(this, () -> {
			new BanControl().runTaskTimerAsynchronously(this, 64, 1280);
			new MuteControl().runTaskTimerAsynchronously(this, 64, 1280);

		}, 10);

	}

	public void stopTasks() {
		server.getScheduler().cancelTasks(this);
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

		for (String command : this.getDescription().getCommands().keySet()) {
			this.getCommand(command).setExecutor(new Commands());
		}

	}

	public void registerEvents() {
		pm.registerEvents(this, this);
		pm.registerEvents(new PlayerListGui(), this);
		pm.registerEvents(new Executables(), this);
		pm.registerEvents(new SetGamemode(), this);
		pm.registerEvents(new FreezeListener(), this);
		pm.registerEvents(new PlayerEventHandler(), this);
		pm.registerEvents(new MuteListener(), this);
		pm.registerEvents(new InteractEvent(), this);
		pm.registerEvents(new ReportGui(), this);

	}

	@Override
	public FileConfiguration getConfig() {
		return new ConfigControl().get(false);
	}

	public FileConfiguration getMessages() {
		return manager.getFile(this, "Messages.yml");
	}

	public void loadMessages() {
		manager.loadFile(this, "Messages.yml");

	}

	public void saveMessages(FileConfiguration config) {
		manager.saveFile(this, "Messages.yml", config);
	}

	public FileConfiguration getReportedPlayers() {
		return manager.getFile(this, "ReportedPlayers.yml");
	}

	public void loadReportedPlayers() {
		manager.loadFile(this, "ReportedPlayers.yml");

	}

	public void saveReportedPlayers(FileConfiguration config) {
		manager.saveFile(this, "ReportedPlayers.yml", config);
	}

	public FileConfiguration getGuiConfig() {
		return manager.getFile(this, "GuiConfig.yml");
	}

	public void loadGuiConfig() {
		manager.loadFile(this, "GuiConfig.yml");
	}

	public void saveGuiConfig(FileConfiguration config) {
		manager.saveFile(this, "GuiConfig.yml", config);

	}

	public FileConfiguration getPlayerData() {
		return manager.getFile(this, "PlayerData.yml");
	}

	public void loadPlayerData() {
		manager.loadFile(this, "PlayerData.yml");
	}

	public void savePlayerData(FileConfiguration config) {
		manager.saveFile(this, "PlayerData.yml", config);

	}

	public void logAction(String msg) {

		IFileManager manager = new IFileManager(this);
		manager.log(msg);

	}

}