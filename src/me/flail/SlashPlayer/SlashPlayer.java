package me.flail.slashplayer;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class SlashPlayer extends JavaPlugin {

	public Server server;

	@Override
	public void onLoad() {
		server = getServer();
	}

}
