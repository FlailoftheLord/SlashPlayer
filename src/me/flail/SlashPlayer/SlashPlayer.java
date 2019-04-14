package me.flail.SlashPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
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

import me.flail.SlashPlayer.Executables.Executables;
import me.flail.SlashPlayer.Executables.SetGamemode;
import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.Listeners.FreezeListener;
import me.flail.SlashPlayer.Listeners.InteractEvent;
import me.flail.SlashPlayer.Listeners.MuteListener;
import me.flail.SlashPlayer.Listeners.PlayerListGui;
import me.flail.SlashPlayer.Listeners.ReportGui;
import me.flail.SlashPlayer.Utilities.PlayerEventHandler;
import me.flail.SlashPlayer.Utilities.Tools;

public class SlashPlayer extends JavaPlugin implements Listener {
	public static SlashPlayer instance;
	public FileManager manager = new FileManager();

	public ConsoleCommandSender console = Bukkit.getConsoleSender();
	public PluginManager pm = getServer().getPluginManager();

	public Server server = this.getServer();
	public String version = getDescription().getVersion();

	public Map<UUID, Player> players = new HashMap<>();

	public Map<Player, Integer> messageCooldowns = new HashMap<>();

	private String serverVersion = getServer().getBukkitVersion();
	private String serverType = getServer().getVersion();

	public FileConfiguration config = new YamlConfiguration();

	@Override
	public void onEnable() {
		instance = this;
		manager = new FileManager();

		// Load up the Files
		saveDefaultConfig();
		config = this.getConfig();

		serverType = serverType.replace(serverType.substring(serverType.indexOf("(")), "v");

		Tools chat = new Tools();

		manager.loadFile("Messages");
		manager.loadFile("PlayerData");
		manager.loadFile("GuiConfig");

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
			PlayerEventHandler pHandler = new PlayerEventHandler();
			pHandler.setData(p);
		}

	}

	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);

	}

	public void startTasks() {
		this.stopTasks();
		server.getScheduler().scheduleSyncRepeatingTask(this, () -> {

		}, 64, 1200);

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

	public void logAction(String msg) {
		manager.log(msg);

	}

}