package me.flail.slashplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.Boot;
import me.flail.slashplayer.sp.SlashPlayerCommand;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.TabCompleter;
import me.flail.slashplayer.user.User;

public class SlashPlayer extends JavaPlugin {
	public static SlashPlayer instance;

	public FileConfiguration config;
	public DataFile messages;

	public List<Gui> loadedGuis = new ArrayList<>(8);
	public Map<User, Gui> openGuis = new HashMap<>();
	public List<User> players = new ArrayList<>(8);

	public String[] guiFiles = { "GamemodeGui.yml", "PlayerListGui.yml", "PlayerGui.yml", "ReportGui.yml", "SampleListGui.yml",
	"SamplePlainGui.yml" };

	public Server server;
	public boolean verbose = false;

	@Override
	public void onLoad() {
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
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return new SlashPlayerCommand(sender, command, label, args).run();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new TabCompleter(command).construct(label, args);
	}

	@EventHandler
	public void cmdProcess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		if (message.startsWith("/sp ")) {
			message = message.replace("/sp ", "/slashplayer ");
		}
		for (User user : players) {
			String name = user.name();
			if (message.toLowerCase().startsWith("/"+ name.toLowerCase()+" ")) {
				message = message.replaceAll("(?i)" + "/" + name + " ", "/slashplayer " + name + " ");
				break;
			}
		}

		event.setMessage(message);
	}

}
