package me.flail.slashplayer.sp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.listeners.PlayerListener;
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
		String[] guiFiles = { "GamemodeGui.yml", "PlayerListGui.yml", "PlayerGui.yml", "ReportGui.yml", "SampleListGui.yml",
				"SamplePlainGui.yml" };
		long startTime = System.currentTimeMillis();

		try {
			console("&aLoading Files...");
			plugin.saveDefaultConfig();
			plugin.verbose = plugin.getConfig().getBoolean("ConsoleVerbose");
			new FileManager().setupGuiFiles(guiFiles);

			nl();
			loadEvents();
			console("Registered Listeners.");
			nl();
			loadCommands();
			int players = this.loadOnlinePlayers();
			switch (players) {
			case 1:
				console("&aLoaded one player...");
				break;
			default:
				console("&aLoaded " + players + " players...");
			}

		} catch (Exception e) {
			return false;
		}

		console("&aStartup complete! &8(&7" + (System.currentTimeMillis() - startTime) + "ms&8)");
		return true;
	}

	protected void loadEvents() {
		pm.registerEvents(new PlayerListener(), plugin);

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
			user.setup(false);
			onlinePlayers++;
		}
		return onlinePlayers;
	}

}
