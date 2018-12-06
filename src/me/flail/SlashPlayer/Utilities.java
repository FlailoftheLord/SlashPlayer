package me.flail.SlashPlayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Utilities {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	public String m(String s) {

		FileConfiguration config = plugin.getConfig();

		String prefix = config.getString("Prefix");

		return ChatColor.translateAlternateColorCodes('&', s.replaceAll("%prefix%", prefix));

	}

	public String msg(String s, Player player, Player operator, String exe, String command) {
		String reply = "";

		FileConfiguration config = plugin.getConfig();

		String banTime = config.getInt("BanTime") + "";
		String muteTime = config.getInt("MuteTime") + "";
		String website = config.getString("Website");

		String prefix = config.getString("Prefix");

		if ((player != null) && (operator != null)) {

			String pName = player.getName();

			String sender = operator.getName();

			String gamemode = player.getGameMode().toString();

			reply = ChatColor.translateAlternateColorCodes('&',
					s.replaceAll("%prefix%", prefix).replaceAll("%player%", pName).replaceAll("%operator%", sender)
							.replaceAll("%reporter%", sender).replaceAll("%command%", command)
							.replaceAll("%executable%", exe).replaceAll("%website%", website)
							.replaceAll("%ban-duration%", banTime).replaceAll("%mute-duration%", muteTime)
							.replaceAll("%gamemode%", gamemode));

		}

		return reply;

	}

}
