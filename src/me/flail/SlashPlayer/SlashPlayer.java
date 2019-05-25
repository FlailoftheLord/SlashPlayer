package me.flail.slashplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.slashplayer.gui.GeneratedGui;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.Boot;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.sp.SlashPlayerCommand;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.TabCompleter;
import me.flail.slashplayer.user.User;

public class SlashPlayer extends JavaPlugin {
	public static SlashPlayer instance;

	public FileConfiguration config;
	public DataFile messages;

	public Map<String, GeneratedGui> loadedGuis = new HashMap<>(4);
	public Map<UUID, Gui> openGuis = new HashMap<>();
	public List<User> players = new ArrayList<>(8);

	public String[] guiFiles = { "GamemodeGui.yml", "PlayerListGui.yml", "PlayerGui.yml", "ReportGui.yml" };

	public Server server;
	public boolean verbose = false;

	@Override
	public void onLoad() {
		saveDefaultConfig();
		config = this.getConfig();

		server = getServer();
	}

	@Override
	public void onEnable() {
		instance = this;
		Boot boot = new Boot(this);
		boot.startup();
	}

	@Override
	public void onDisable() {
		server.getScheduler().getPendingTasks().clear();
		server.getScheduler().cancelTasks(this);

		openGuis.clear();
		loadedGuis.clear();
		if (!players.isEmpty()) {
			for (User user : players) {
				if (user.isOnline()) {
					user.player().closeInventory();
				}
			}
		}

	}

	public long reload() {
		return new Boot(this).reload();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return new SlashPlayerCommand(sender, command, args).run();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new TabCompleter(command).construct(label, args);
	}


	public void sendHelp(User operator, String cmd) {
		if (operator.hasPermission("slashplayer.command")) {
			Map<String, String> cmdPl = new HashMap<>();
			cmdPl.put("%command%", cmd);
			cmdPl.put("%cmd%", cmd);

			new Message("HelpMessage").placeholders(cmdPl).send(operator, null);
			return;
		}
		new Message("NoPermission").send(operator, null);
	}

	@SuppressWarnings("deprecation")
	public User offlinePlayer(String name) {
		OfflinePlayer deprecatedMethodThatIDontWantToUseButIHaveToUseIt = Bukkit.getOfflinePlayer(name);
		return new User(deprecatedMethodThatIDontWantToUseButIHaveToUseIt.getUniqueId());
	}


	public void userGui(User operator, String... username) {
		boolean isOnline = false;
		for (User user : players) {
			if (user.name().toLowerCase().startsWith(username[0].toLowerCase())) {
				new GuiControl().openModerationGui(operator, user);

				isOnline = true;
				break;
			}
		}


		if (!isOnline) {
			new GuiControl().openModerationGui(operator, this.offlinePlayer(username[0]));
		}


	}

}
