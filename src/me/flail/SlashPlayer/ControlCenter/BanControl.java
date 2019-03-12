package me.flail.SlashPlayer.ControlCenter;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.Utilities.Tools;

public class BanControl {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private Tools tools = new Tools();
	private FileManager manager = new FileManager();

	public void runTimer() {
		FileConfiguration pData = manager.getFile("PlayerData");

		this.loadList();

		for (String uuid : plugin.banList.keySet()) {
			if (pData.getBoolean(uuid + ".IsBanned", false)) {
				int time = plugin.banList.get(uuid).intValue();
				pData.set(uuid + ".BanDuration", time - 1);

			}

		}

		manager.saveFile(pData);
	}

	public void loadList() {
		FileConfiguration pData = manager.getFile("PlayerData");

		for (String pUuid : pData.getKeys(false)) {
			boolean isBanned = pData.getBoolean(pUuid + ".IsBanned", false);
			if (isBanned) {
				plugin.banList.put(pUuid,
						Integer.valueOf(pData.getInt(pUuid + ".BanDuration", plugin.banList.get(pUuid).intValue())));
			}

		}

	}

	public void saveList() {
		FileConfiguration pData = manager.getFile("PlayerData");

		for (String pUuid : plugin.banList.keySet()) {
			pData.set(pUuid + ".IsBanned", Boolean.valueOf(true));
			pData.set(pUuid + ".BanDuration", plugin.banList.get(pUuid));
		}

		manager.saveFile(pData);
	}

	public boolean banPlayer(OfflinePlayer player, String reason, int time) {

		FileConfiguration pData = manager.getFile("PlayerData");
		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsBanned", Boolean.valueOf(true));
		pData.set(pUuid + ".BanDuration", Integer.valueOf(time));
		pData.set(pUuid + ".IsOnline", Boolean.valueOf(false));

		plugin.banList.put(pUuid, Integer.valueOf(time));

		// Finally, we kick teh player off if he's still online!
		if (player.isOnline()) {
			Player onlinePlayer = player.getPlayer();
			this.kickBanned(onlinePlayer, reason);
		}

		plugin.manager.saveFile(pData);
		return true;

	}

	public boolean unbanPlayer(OfflinePlayer player) {

		FileConfiguration pData = manager.getFile("PlayerData");
		String pUuid = player.getUniqueId().toString();

		plugin.banList.remove(pUuid);

		pData.set(pUuid + ".IsBanned", null);
		pData.set(pUuid + ".BanDuration", null);
		manager.saveFile(pData);

		return true;
	}

	public boolean checkBanned(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		FileConfiguration pData = manager.getFile("PlayerData");
		if (pData.getBoolean(pUuid + ".IsBanned", false)) {
			return true;
		}

		return false;
	}

	public int getBanDuration(OfflinePlayer player) {
		FileConfiguration pData = manager.getFile("PlayerData");
		return pData.getInt(player.getUniqueId().toString() + ".BanDuration");
	}

	public void kickBanned(Player player, String reason) {
		if ((reason == null) || reason.isEmpty()) {
			reason = plugin.manager.getMessage("Banned").replace("%ban-duration%", this.getBanDuration(player) + "");

		}

		player.kickPlayer(tools.msg(reason, player, null, "Ban", "slashplayer"));
	}

}
