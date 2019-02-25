package me.flail.SlashPlayer.Utilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;

public class BanTimer extends BukkitRunnable {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	private Tools chat = new Tools();

	@Override
	public void run() {

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();

		for (String player : pData.getKeys(false)) {

			String pUuid = player;

			Player offlinePlayer = plugin.server.getOfflinePlayer(UUID.fromString(player)).getPlayer();

			boolean isBanned = pData.getBoolean(pUuid + ".IsBanned");

			if (isBanned) {

				int banDuration = pData.getInt(pUuid + ".BanDuration");

				if (banDuration > 0) {

					int newBanTime = banDuration - 1;

					pData.set(pUuid + ".BanDuration", newBanTime);

				} else if (banDuration <= 0) {

					pData.set(pUuid + ".BanDuration", null);

					pData.set(pUuid + ".IsBanned", false);

					boolean broadcastUnban = config.getBoolean("Broadcast.UnBan");

					String unbanMsg = messages.getString("AutoUnban");

					if (broadcastUnban) {
						plugin.getServer().broadcast(
								chat.msg(unbanMsg, offlinePlayer, offlinePlayer, "AutoUnban", "slashplayer"),
								"slashplayer.notify");
					} else {

						for (Player op : plugin.players.values()) {

							if (op.hasPermission("slashplayer.notify")) {

								op.sendMessage(chat.msg(unbanMsg, offlinePlayer, op, "AutoUnban", "ban"));
								continue;

							}

						}

					}

					console.sendMessage(
							chat.m(unbanMsg.replaceAll("%player%", pData.get(player + ".Name").toString())));

				}

			}

		}

	}

}
