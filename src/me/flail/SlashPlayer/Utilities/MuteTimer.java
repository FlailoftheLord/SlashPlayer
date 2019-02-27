package me.flail.SlashPlayer.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.SlashPlayer.SlashPlayer;

public class MuteTimer extends BukkitRunnable {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);
	// private ConsoleCommandSender console = Bukkit.getConsoleSender();
	private Tools chat = new Tools();

	@Override
	public void run() {

		FileConfiguration messages = plugin.getMessages();

		FileConfiguration pData = plugin.getPlayerData();

		for (Player p : plugin.players.values()) {

			String pUuid = p.getUniqueId().toString();

			boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

			if (isMuted) {

				int timeLeft = pData.getInt(pUuid + ".MuteDuration");

				if (timeLeft > 0) {

					int newTime = timeLeft - 1;

					pData.set(pUuid + ".MuteDuration", newTime);

				} else if (timeLeft <= 0) {

					pData.set(pUuid + ".IsMuted", false);
					pData.set(pUuid + ".MuteDuration", null);

					p.sendMessage(chat.m("%prefix% &ayou have been unmuted, you may now talk again!"));

					String unMuteMsg = messages.getString("AutoUnmute");

					for (Player op : Bukkit.getOnlinePlayers()) {

						if (op.isOp() || op.hasPermission("slashplayer.notify")) {

							op.sendMessage(chat.m(unMuteMsg).replace("%player%", p.getName()));

						}

					}

				}

			}

		}

		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {

			String pUuid = p.getUniqueId().toString();

			boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

			if (isMuted) {

				int timeLeft = pData.getInt(pUuid + ".MuteDuration");

				if (timeLeft > 0) {

					int newTime = timeLeft - 1;

					pData.set(pUuid + ".MuteDuration", newTime);

				} else if (timeLeft <= 0) {

					pData.set(pUuid + ".IsMuted", false);
					pData.set(pUuid + ".MuteDuration", null);

					String unMuteMsg = messages.getString("AutoUnmute");

					for (Player op : Bukkit.getOnlinePlayers()) {

						if (op.isOp() || op.hasPermission("slashplayer.notify")) {

							op.sendMessage(chat.m(unMuteMsg).replace("%player%", p.getName()));

						}

					}

				}

			}

		}

		plugin.savePlayerData(pData);

	}

}
