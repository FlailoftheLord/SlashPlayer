package me.flail.SlashPlayer.ControlCenter;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.FileManager.FileManager;
import me.flail.SlashPlayer.Utilities.Time;
import me.flail.SlashPlayer.Utilities.Tools;
import me.flail.oldSlashPlayer.SlashPlayer;

public class BanControl {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);
	private Tools tools = new Tools();
	private Time time = new Time();
	private FileManager manager = new FileManager();


	public boolean banPlayer(OfflinePlayer player, String reason, int time) {

		FileConfiguration pData = manager.getFile("PlayerData");
		String pUuid = player.getUniqueId().toString();
		String timeOfBan = this.time.currentTime(true);

		pData.set(pUuid + ".IsBanned", Boolean.valueOf(true));
		pData.set(pUuid + ".BanDate", timeOfBan);
		pData.set(pUuid + ".BanDuration", Integer.valueOf(this.time.toSeconds("minute", time)));
		pData.set(pUuid + ".IsOnline", Boolean.valueOf(false));


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

		pData.set(pUuid + ".IsBanned", null);
		pData.set(pUuid + ".BanDuration", null);
		pData.set(pUuid + ".BanDate", null);
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

	public String getBanDuration(OfflinePlayer player) {
		FileConfiguration pData = manager.getFile("PlayerData");
		return time.fromSeconds(pData.getInt(player.getUniqueId().toString() + ".BanDuration", 0), true);
	}

	public void kickBanned(Player player, String reason) {
		if ((reason == null) || reason.isEmpty()) {
			reason = plugin.manager.getMessage("Banned").replace("%ban-duration%", this.getBanDuration(player) + "");

		}

		player.kickPlayer(tools.msg(reason, player, null, "Ban", "slashplayer"));
	}

}
