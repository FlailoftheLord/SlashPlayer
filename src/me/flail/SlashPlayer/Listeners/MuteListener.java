package me.flail.SlashPlayer.Listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;

public class MuteListener implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration messages = plugin.getMessages();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		String msg = event.getMessage();

		boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

		if (isMuted) {

			if (!(msg.startsWith("/"))) {

				if (!(player.hasPermission("slashplayer.exempt")) || !player.isOp()) {

					event.setCancelled(true);

					String timeLeft = pData.getInt(pUuid + ".MuteDuration") + "";

					String cantTalk = chat.msg(messages.getString("Muted").replace("%time%", timeLeft), player, player,
							"Mute", "mute");

					player.sendMessage(cantTalk);

				}

			}

		} else if (isMuted == false) {
			return;
		} else {
			pData.set(pUuid + ".IsMuted", false);
		}

		plugin.savePlayerData();
	}

}
