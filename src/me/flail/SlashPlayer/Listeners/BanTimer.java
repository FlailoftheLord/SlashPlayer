package me.flail.SlashPlayer.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities;

public class BanTimer extends BukkitRunnable {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	private Utilities chat = new Utilities();

	@Override
	public void run() {

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();

		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {

			String pUuid = player.getUniqueId().toString();

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
						plugin.getServer().broadcastMessage(
								chat.msg(unbanMsg, (Player) player, (Player) player, "AutoUnban", "AutoUnban"));
					} else {

						for (Player op : Bukkit.getOnlinePlayers()) {

							if (op.isOp() || op.hasPermission("slashplayer.notify")) {

								op.sendMessage(chat.m(unbanMsg).replace("%player%", player.getName()));

							}

						}

					}

				}

			}

		}

		console.sendMessage(chat.m("%prefix% Updating bans..."));

	}

}
