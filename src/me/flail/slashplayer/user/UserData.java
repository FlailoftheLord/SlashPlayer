package me.flail.slashplayer.user;

import java.util.UUID;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;

public class UserData extends Logger {
	protected SlashPlayer plugin;
	protected UUID playerUuid;

	protected UserData(SlashPlayer plugin, UUID playerUuid) {
		this.plugin = plugin;
		this.playerUuid = playerUuid;
	}

	protected DataFile getDataFile() {
		return new DataFile(plugin.getDataFolder() + "/PlayerData/" + playerUuid + ".yml");
	}

}
