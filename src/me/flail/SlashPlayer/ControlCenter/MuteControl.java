package me.flail.SlashPlayer.ControlCenter;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.FileManager;

public class MuteControl extends BukkitRunnable {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);

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
		FileConfiguration pData = plugin.getPlayerData();

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
		FileConfiguration pData = plugin.getPlayerData();

		for (OfflinePlayer player : plugin.muteTimer.keySet()) {
			int time = plugin.muteTimer.get(player).intValue();
			String pUuid = player.getUniqueId().toString();

			if (time > 0) {
				pData.set(pUuid + ".IsMuted", true);
				pData.set(pUuid + ".MuteDuration", time);
			}

		}

		plugin.savePlayerData(pData);
	}

	public boolean mute(OfflinePlayer player, int time) {
		try {
			FileConfiguration pData = plugin.getPlayerData();

			String pUuid = player.getUniqueId().toString();
			plugin.muteTimer.put(player, Integer.valueOf(time));

			pData.set(pUuid + ".IsMuted", true);
			pData.set(pUuid + ".MuteDuration", time);

			plugin.savePlayerData(pData);

			this.saveList();
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean unMute(OfflinePlayer player) {

		FileConfiguration pData = plugin.getPlayerData();

		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsMuted", null);
		pData.set(pUuid + ".MuteDuration", null);
		plugin.savePlayerData(pData);

		plugin.muteTimer.remove(player);

		return true;
	}

	public boolean check(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		FileManager manager = plugin.manager;
		FileConfiguration pData = manager.getFile(plugin, "PlayerData.yml");
		if (pData.getBoolean(pUuid + ".IsMuted")) {
			return true;
		} else {
			return false;
		}

	}
}
