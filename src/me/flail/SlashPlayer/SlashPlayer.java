package me.flail.slashplayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.Boot;
import me.flail.slashplayer.tools.TabCompleter;
import me.flail.slashplayer.user.User;

public class SlashPlayer extends JavaPlugin {
	public static SlashPlayer instance;

	public List<Gui> openGuis = new LinkedList<>();
	public List<User> players = new ArrayList<>();

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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new TabCompleter(command).construct(label, args);
	}


}
