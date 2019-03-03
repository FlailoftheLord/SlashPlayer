package me.flail.SlashPlayer.ControlCenter;

import java.util.UUID;

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

		for (OfflinePlayer p : plugin.banTimer.keySet()) {
			int time = plugin.banTimer.get(p).intValue();

			if (time < 1) {
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

				plugin.banTimer.put(plugin.server.getOfflinePlayer(uuid), Integer.valueOf(time));
			}

		}

	}

	public void saveBanList() {

		for (OfflinePlayer player : plugin.banTimer.keySet()) {

			int time = plugin.banTimer.get(player).intValue();
			String pUuid = player.getUniqueId().toString();

			if (time > 0) {
				FileConfiguration pData = plugin.manager.getFile(plugin, "PlayerData.yml");
				pData.set(pUuid + ".IsOnline", false);
				pData.set(pUuid + ".IsBanned", true);
				pData.set(pUuid + ".BanDuration", time);
				plugin.manager.saveFile(plugin, "PlayerData.yml", pData);
			}

		}

	}

	public boolean banPlayer(OfflinePlayer player, String reason, int time) {

		plugin.banTimer.put(player, Integer.valueOf(time));

		FileConfiguration pData = plugin.manager.getFile(plugin, "PlayerData.yml");
		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsBanned", true);
		pData.set(pUuid + ".BanDuration", time);
		pData.set(pUuid + ".IsOnline", false);
		plugin.manager.saveFile(plugin, "PlayerData.yml", pData);

		// Finally, we kick teh player off if he's still online!
		if (player.isOnline()) {
			Player onlinePlayer = player.getPlayer();
			this.kickBanned(onlinePlayer, reason);
		}

		this.saveBanList();
		return true;

	}

	public boolean unbanPlayer(OfflinePlayer player) {

		plugin.banTimer.remove(player);

		FileConfiguration pData = plugin.manager.getFile(plugin, "PlayerData.yml");
		String pUuid = player.getUniqueId().toString();

		pData.set(pUuid + ".IsBanned", null);
		pData.set(pUuid + ".BanDuration", null);
		plugin.manager.saveFile(plugin, "PlayerData.yml", pData);

		this.saveBanList();
		return true;
	}

	public boolean checkBanned(OfflinePlayer player) {

		String pUuid = player.getUniqueId().toString();

		FileConfiguration pData = plugin.manager.getFile(plugin, "PlayerData.yml");
		if (pData.getBoolean(pUuid + ".IsBanned", false)) {
			return true;
		}

		this.saveBanList();
		return false;
	}

	public void kickBanned(Player player, String reason) {
		if ((reason == null) || reason.isEmpty()) {
			reason = plugin.manager.getMessage("Banned").replace("%ban-duration%",
					plugin.banTimer.get(player).toString());

		}

		player.kickPlayer(tools.msg(reason, player, null, "Ban", "slashplayer"));

		this.saveBanList();
	}

}
