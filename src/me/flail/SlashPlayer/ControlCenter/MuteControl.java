package me.flail.SlashPlayer.ControlCenter;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.BaseFileManager;
import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.Utilities.Time;

public class MuteControl {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();
	private Time time = new Time();


	public boolean mute(OfflinePlayer player, int time) {
		try {
			FileConfiguration pData = manager.getFile("PlayerData");

			String pUuid = player.getUniqueId().toString();

			pData.set(pUuid + ".IsMuted", Boolean.valueOf(true));
			pData.set(pUuid + ".MuteDuration", Integer.valueOf(this.time.toSeconds("minute", time)));

			manager.saveFile(pData);

			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean unMute(OfflinePlayer player) {

		FileConfiguration pData = manager.getFile("PlayerData");

		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsMuted", null);
		pData.set(pUuid + ".MuteDuration", null);
		manager.saveFile(pData);

		return true;
	}

	public boolean check(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		BaseFileManager manager = plugin.manager;
		FileConfiguration pData = manager.getFile("PlayerData.yml");
		if (pData.getBoolean(pUuid + ".IsMuted", false)) {
			return true;
		} else {
			return false;
		}

	}
}
