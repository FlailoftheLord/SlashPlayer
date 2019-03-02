package me.flail.SlashPlayer.Listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.ControlCenter.MuteControl;
import me.flail.SlashPlayer.Utilities.Tools;

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

		MuteControl mutes = new MuteControl();

		boolean isMuted = mutes.check(player);

		if (isMuted) {
			if (!(player.hasPermission("slashplayer.exempt.mute")) || !player.isOp()) {

				event.setCancelled(true);

				String timeLeft = pData.get(pUuid + ".MuteDuration").toString();

				String cantTalk = chat.msg(messages.getString("Muted").replace("%mute-duration%", timeLeft), player,
						player, "Mute", "slashplayer");

				player.sendMessage(cantTalk);

			}

		}

		boolean isFrozen = pData.getBoolean(pUuid + ".IsFrozen");
		String blockChat = config.get("Freeze.Chat").toString();

		if (isFrozen) {

			if (blockChat.equalsIgnoreCase("deny")) {

				event.setCancelled(true);
				String cantChatWhileFrozen = chat.msg(messages.getString("FreezeOther"), player, player, "Freeze",
						"slashplayer");
				player.sendMessage(cantChatWhileFrozen);

			}

		}

		plugin.savePlayerData(pData);
	}

}
