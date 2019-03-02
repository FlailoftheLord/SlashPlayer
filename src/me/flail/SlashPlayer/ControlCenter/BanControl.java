package me.flail.SlashPlayer.ControlCenter;

import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.FileManager;
import me.flail.SlashPlayer.Utilities.Tools;

public class BanControl extends BukkitRunnable {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);

	@Override
	public void run() {

		Set<OfflinePlayer> banList = plugin.banTimer.keySet();

		for (OfflinePlayer p : banList) {
			int time = plugin.banTimer.get(p).intValue();

			if (time <= 0) {
				this.unbanPlayer(p);
			} else {
				plugin.banTimer.put(p, Integer.valueOf(time - 1));
			}

		}

	}

	public void loadBanList() {
		FileConfiguration pData = plugin.getPlayerData();

		for (String pUuid : pData.getKeys(false)) {
			boolean isBanned = pData.getBoolean(pUuid + ".IsBanned", false);
			if (isBanned) {
				UUID uuid = UUID.fromString(pUuid);
				int time = pData.getInt(pUuid + ".BanDuration", 60);

				plugin.banTimer.put(plugin.server.getOfflinePlayer(uuid), time);
			}

		}

	}

	public void saveBanList() {
		FileConfiguration pData = plugin.getPlayerData();

		for (OfflinePlayer player : plugin.banTimer.keySet()) {
			int time = plugin.banTimer.get(player).intValue();
			String pUuid = player.getUniqueId().toString();

			if (time > 0) {
				pData.set(pUuid + ".IsBanned", true);
				pData.set(pUuid + ".BanDuration", time);
			}

		}

		plugin.savePlayerData(pData);
	}

	public boolean banPlayer(OfflinePlayer player, String reason, int time) {
		try {
			FileConfiguration pData = plugin.getPlayerData();

			String pUuid = player.getUniqueId().toString();
			plugin.banTimer.put(player, Integer.valueOf(time));

			pData.set(pUuid + ".IsBanned", true);
			pData.set(pUuid + ".BanDuration", time);
			pData.set(pUuid + ".IsOnline", false);

			// Finally, we kick teh player off if he's still online!
			if (player.isOnline()) {
				Player onlinePlayer = player.getPlayer();
				Tools tools = new Tools();

				if ((reason == null) || reason.isEmpty()) {
					reason = plugin.manager.getMessage("Banned").replace("%ban-duration%",
							plugin.banTimer.get(player).toString());

				}

				onlinePlayer.kickPlayer(tools.msg(reason, onlinePlayer, null, "Ban", "slashplayer"));
			}

			plugin.savePlayerData(pData);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean unbanPlayer(OfflinePlayer player) {

		FileConfiguration pData = plugin.getPlayerData();

		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsBanned", false);
		plugin.savePlayerData(pData);

		plugin.banTimer.remove(player);

		return true;
	}

	public boolean checkBanned(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		FileManager manager = plugin.manager;
		FileConfiguration pData = manager.getFile(plugin, "PlayerData.yml");
		if (pData.getBoolean(pUuid + ".IsBanned")) {
			return true;
		} else {
			return false;
		}

	}

}
