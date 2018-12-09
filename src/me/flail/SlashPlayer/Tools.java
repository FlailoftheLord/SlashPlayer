package me.flail.SlashPlayer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Tools {

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

		if (s != null) {

			if ((player != null) && (operator != null)) {

				String pName = player.getName();

				String sender = operator.getName();

				String pUuid = player.getUniqueId().toString();

				String gamemode = player.getGameMode().toString();

				reply = ChatColor.translateAlternateColorCodes('&',
						s.replaceAll("%prefix%", prefix).replaceAll("%player%", pName).replaceAll("%operator%", sender)
								.replaceAll("%reporter%", sender).replaceAll("%command%", command)
								.replaceAll("%executable%", exe).replaceAll("%website%", website)
								.replaceAll("%ban-duration%", banTime).replaceAll("%mute-duration%", muteTime)
								.replaceAll("%gamemode%", gamemode).replaceAll("%uuid%", pUuid));

			} else {
				reply = s;
			}

		} else {
			reply = ChatColor.translateAlternateColorCodes('&',
					"%prefix% &cAn error occured with SlashPlayer, please contact me on my support server for help!"
							.replace("%prefix%", prefix));
		}

		return reply;

	}

	public int playerRank(Player player) {

		FileConfiguration config = plugin.getConfig();

		int maxRank = config.getInt("HighestRank");

		int reply = 0;

		if (player != null) {

			if (maxRank > 0) {

				int number = maxRank;

				while (number <= maxRank) {

					String rank = "slashplayer.rank." + number;

					if (player.hasPermission(rank)) {

						reply = number;
						break;
					} else {
						number -= 1;
						continue;
					}

				}

			}

		}

		return reply;

	}

}
