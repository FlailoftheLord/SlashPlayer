package me.flail.SlashPlayer.Utilities;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.flail.SlashPlayer.SlashPlayer;

public class Tools {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	public String m(String s) {

		FileConfiguration config = plugin.getConfig();

		String prefix = config.getString("Prefix");

		return ChatColor.translateAlternateColorCodes('&', s.replaceAll("%prefix%", prefix).replaceAll(" ~!~", " \n"));

	}

	public String msg(String s, Player player, Player operator, String exe, String command) {
		String reply = s;

		FileConfiguration config = plugin.getConfig();

		String banTime = config.get("BanTime").toString();
		String muteTime = config.get("MuteTime").toString();
		String website = config.getString("Website");

		String prefix = config.getString("Prefix");

		if ((s != null)) {

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
				reply = ChatColor.translateAlternateColorCodes('&', s.replaceAll("%prefix%", prefix));
			}

		} else if (s == null) {
			reply = s;
			plugin.console.sendMessage(this.m(
					"%prefix% &cAn error occured while translating placeholders, please contact me on my support server with this message: "));
			plugin.console.sendMessage("Exe: " + exe + " Operator: " + operator.isOnline() + " Target: "
					+ player.isOnline() + " Command: " + command);
		}

		return reply;

	}

	public static int playerRank(Player player) {

		int maxRank = 100;

		int reply = 0;

		if (player != null) {

			if (player.hasPermission("slashplayer.rank")) {

				try {

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

				} catch (Exception e) {
					reply = 0;
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

	public String encodeLore(String lore) {
		if (!hasCode(lore)) {
			return this.m("&a&f&4&r" + lore);
		} else {
			return lore;
		}
	}

	public String extractCode(String line) {
		String newLine = line.replace("§", "");
		if (newLine.startsWith("af4")) {
			return this.m("&8" + ChatColor.stripColor(line));
		}

		return line;
	}

	public static boolean hasCode(String line) {
		String newLine = line.replace("§", "");
		if (newLine.startsWith("af4")) {
			return true;
		}

		return false;
	}

}
