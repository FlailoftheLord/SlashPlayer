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
		FileConfiguration config = plugin.getConfig();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

		if (isMuted) {

			if (!(player.hasPermission("slashplayer.exempt")) || !player.isOp()) {

				event.setCancelled(true);

				String timeLeft = pData.get(pUuid + ".MuteDuration").toString();

				String cantTalk = chat.msg(messages.getString("Muted").replace("%time%", timeLeft), player, player,
						"Mute", "mute");

				player.sendMessage(cantTalk);

			}

		} else {
			pData.set(pUuid + ".IsMuted", false);
		}

		boolean isFrozen = pData.getBoolean(pUuid + ".IsFrozen");
		String blockChat = config.get("Freeze.Chat").toString();

		if (isFrozen) {

			if (blockChat.equalsIgnoreCase("deny")) {

				event.setCancelled(true);
				String cantChatWhileFrozen = chat.msg(messages.getString("FreezeOther"), player, player, "Freeze",
						"freeze");
				player.sendMessage(cantChatWhileFrozen);

			}

		}

		plugin.savePlayerData();
	}

}
