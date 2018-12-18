package me.flail.SlashPlayer;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

	public static int playerRank(Player player) {

		int maxRank = 100;

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

	public static PluginCommand getCommand(String name, Plugin plugin) {
		PluginCommand command = null;

		try {
			Constructor<PluginCommand> cmd = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			cmd.setAccessible(true);
			command = cmd.newInstance(name, plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return command;
	}

	public String listToString(List<String> stringList) {

		if ((stringList != null) && !stringList.isEmpty()) {

			Iterator<String> listIterator = stringList.iterator();
			String s = "";

			while (listIterator.hasNext()) {

				String word = listIterator.next();
				if (word != null) {
					s = s + "\n" + word;
				} else {
					s = word;
				}

			}

			return s;
		} else {
			return ChatColor.translateAlternateColorCodes('&', "&4SlashPlayer Formatting ERROR!&r");
		}

	}

}
