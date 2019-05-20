package me.flail.slashplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.slashplayer.gui.GeneratedGui;
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

	public Map<String, GeneratedGui> loadedGuis = new HashMap<>(4);
	public Map<UUID, Gui> openGuis = new HashMap<>();
	public List<User> players = new ArrayList<>(8);

	public String[] guiFiles = { "GamemodeGui.yml", "PlayerListGui.yml", "PlayerGui.yml", "ReportGui.yml" };

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



}
