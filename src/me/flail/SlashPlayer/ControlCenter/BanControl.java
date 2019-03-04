package me.flail.SlashPlayer.ControlCenter;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class BanControl {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private Tools tools = new Tools();

	public void runTimer() {
		FileConfiguration pData = plugin.getPlayerData();

		this.loadBanList();

		for (String uuid : plugin.banList) {
			if (pData.getBoolean(uuid + ".IsBanned", false)) {
				int time = pData.getInt(uuid + ".BanDuration", 60);
				pData.set(uuid + ".BanDuration", time - 1);

			}

		}

	}

	public void loadBanList() {
		FileConfiguration pData = plugin.getPlayerData();

		for (String pUuid : pData.getKeys(false)) {
			boolean isBanned = pData.getBoolean(pUuid + ".IsBanned", false);
			if (isBanned) {
				plugin.banList.add(pUuid);
			}

		}

	}

	public boolean banPlayer(OfflinePlayer player, String reason, int time) {

		FileConfiguration pData = plugin.getPlayerData();
		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsBanned", true);
		pData.set(pUuid + ".BanDuration", time);
		pData.set(pUuid + ".IsOnline", false);
		plugin.manager.saveFile(plugin, "PlayerData.yml", pData);

		plugin.banList.add(pUuid);

		// Finally, we kick teh player off if he's still online!
		if (player.isOnline()) {
			Player onlinePlayer = player.getPlayer();
			this.kickBanned(onlinePlayer, reason);
		}

		return true;

	}

	public boolean unbanPlayer(OfflinePlayer player) {

		FileConfiguration pData = plugin.getPlayerData();
		String pUuid = player.getUniqueId().toString();

		plugin.banList.remove(pUuid);

		pData.set(pUuid + ".IsBanned", null);
		pData.set(pUuid + ".BanDuration", null);
		plugin.savePlayerData(pData);

		return true;
	}

	public boolean checkBanned(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		FileConfiguration pData = plugin.getPlayerData();
		if (pData.getBoolean(pUuid + ".IsBanned", false)) {
			return true;
		}

		return false;
	}

	public int getBanDuration(OfflinePlayer player) {
		FileConfiguration pData = plugin.getPlayerData();
		return pData.getInt(player.getUniqueId().toString() + ".BanDuration");
	}

	public void kickBanned(Player player, String reason) {
		if ((reason == null) || reason.isEmpty()) {
			reason = plugin.manager.getMessage("Banned").replace("%ban-duration%", this.getBanDuration(player) + "");

		}

		player.kickPlayer(tools.msg(reason, player, null, "Ban", "slashplayer"));
	}

}
