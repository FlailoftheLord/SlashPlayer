package me.flail.slashplayer.sp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.listeners.CommandListener;
import me.flail.slashplayer.listeners.GuiListener;
import me.flail.slashplayer.listeners.PlayerListener;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Boot extends Logger {
	private SlashPlayer plugin;
	private PluginManager pm;

	public Boot(SlashPlayer plugin) {
		this.plugin = plugin;
		pm = plugin.server.getPluginManager();
	}

	public boolean startup() {

		long startTime = System.currentTimeMillis();

		try {
			console("&aLoading Files...");
			plugin.saveDefaultConfig();
			plugin.verbose = plugin.getConfig().getBoolean("ConsoleVerbose");
			new FileManager().setupGuiFiles(plugin.guiFiles);
			plugin.messages = new DataFile("Messages.yml");
			new DataFile("GuiConfig.yml");
			nl();
			loadEvents();
			console("Registered Listeners.");
			nl();
			loadCommands();
			switch (this.loadOnlinePlayers()) {
			case 1:
				console("&aLoaded &7one&a player...");
				break;
			default:
				console("&aLoaded &7" + plugin.players.size() + " &aplayers...");
			}

		} catch (Exception e) {
			return false;
		}

		console("&aStartup complete! &8(&7" + (System.currentTimeMillis() - startTime) + "ms&8)");
		return true;
	}

	public long reload() {
		long startTime = System.currentTimeMillis();
		try {
			console("&aReloading Slashplayer...");
			plugin.server.getScheduler().getPendingTasks().clear();
			plugin.server.getScheduler().cancelTasks(plugin);

			plugin.players.clear();

			for (User user : plugin.players) {
				user.player().closeInventory();
			}

			plugin.openGuis.clear();
			plugin.loadedGuis.clear();
			plugin.saveDefaultConfig();
			plugin.verbose = plugin.getConfig().getBoolean("ConsoleVerbose");
			plugin.messages = new DataFile("Messages.yml");

			new FileManager().setupGuiFiles(plugin.guiFiles);
			new DataFile("GuiConfig.yml");
			nl();

			switch (this.loadOnlinePlayers()) {
			case 1:
				console("&aLoaded &7one&a player...");
				break;
			default:
				console("&aLoaded &7" + plugin.players.size() + " &aplayers...");
			}

		} catch (Exception e) {
			return -1;
		}

		nl();
		console("&aReload complete! &8(&7" + (System.currentTimeMillis() - startTime) + "ms&8)");
		return System.currentTimeMillis() - startTime;
	}

	protected void loadEvents() {
		pm.registerEvents(new PlayerListener(), plugin);
		pm.registerEvents(new GuiListener(), plugin);
		pm.registerEvents(new CommandListener(), plugin);
	}

	protected boolean loadCommands() {
		for (String cmd : plugin.getDescription().getCommands().keySet()) {
			plugin.getCommand(cmd).setExecutor(plugin);
			if (!cmd.equals("ouch")) {
				console("&eRegistered Command&8: &7/" + cmd);
			}
		}

		return plugin != null;
	}

	private int loadOnlinePlayers() {
		int onlinePlayers = 0;
		for (Player p : plugin.server.getOnlinePlayers()) {
			User user = new User(p.getUniqueId());
			plugin.players.add(user);

			user.setup(false);
			onlinePlayers++;
		}
		return onlinePlayers;
	}

}
