package me.flail.SlashPlayer.ControlCenter;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.BaseFileManager;
import me.flail.SlashPlayer.FileManager.FileManager;

public class MuteControl extends BukkitRunnable {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();

	@Override
	public void run() {

		for (OfflinePlayer p : plugin.muteTimer.keySet()) {
			int time = plugin.muteTimer.get(p).intValue();

			if (time <= 0) {
				this.unMute(p);
			} else {
				plugin.muteTimer.put(p, Integer.valueOf(time - 1));
			}

		}

	}

	public void loadList() {
		FileConfiguration pData = manager.getFile("PlayerData");

		for (String pUuid : pData.getKeys(false)) {
			boolean isMuted = pData.getBoolean(pUuid + ".IsMuted", false);
			if (isMuted) {
				UUID uuid = UUID.fromString(pUuid);
				int time = pData.getInt(pUuid + ".MuteDuration", 5);

				plugin.muteTimer.put(plugin.server.getOfflinePlayer(uuid), time);
			}

		}

	}

	public void saveList() {
		FileConfiguration pData = manager.getFile("PlayerData");

		for (OfflinePlayer player : plugin.muteTimer.keySet()) {
			int time = plugin.muteTimer.get(player).intValue();
			String pUuid = player.getUniqueId().toString();

			if (time > 0) {
				pData.set(pUuid + ".IsMuted", true);
				pData.set(pUuid + ".MuteDuration", time);
			}

		}

		manager.saveFile(pData);
	}

	public boolean mute(OfflinePlayer player, int time) {
		try {
			FileConfiguration pData = manager.getFile("PlayerData");

			String pUuid = player.getUniqueId().toString();
			plugin.muteTimer.put(player, Integer.valueOf(time));

			pData.set(pUuid + ".IsMuted", true);
			pData.set(pUuid + ".MuteDuration", time);

			manager.saveFile(pData);

			this.saveList();
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

		plugin.muteTimer.remove(player);

		return true;
	}

	public boolean check(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		BaseFileManager manager = plugin.manager;
		FileConfiguration pData = manager.getFile("PlayerData.yml");
		if (pData.getBoolean(pUuid + ".IsMuted")) {
			return true;
		} else {
			return false;
		}

	}
}
