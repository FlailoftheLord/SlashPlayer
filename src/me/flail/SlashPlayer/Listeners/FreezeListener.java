package me.flail.SlashPlayer.Listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class FreezeListener implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerMove(PlayerMoveEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		boolean isFrozen = pData.getBoolean(pUuid + ".IsFrozen");

		if (isFrozen) {

			Location from = event.getFrom();
			Location to = event.getTo();

			int fbX = from.getBlockX();
			int fbY = from.getBlockY();
			int fbZ = from.getBlockZ();

			int tbX = to.getBlockX();
			int tbY = to.getBlockY();
			int tbZ = to.getBlockZ();

			if ((player.getGameMode() != GameMode.ADVENTURE) && plugin.getConfig().getBoolean("Freeze.AdventureMode")) {
				player.setGameMode(GameMode.ADVENTURE);
			}

			if ((fbX != tbX) || (fbY != tbY) || (fbZ != tbZ)) {
				event.setCancelled(true);

				String cantMove = chat.msg(messages.getString("FreezeMove"), player, player, "Freeze", "slashplayer");

				if (plugin.messageCooldowns.get(player) != null) {
					int cooldown = plugin.messageCooldowns.get(player).intValue();
					if (cooldown > 1) {
						plugin.messageCooldowns.put(player, Integer.valueOf(cooldown - 1));
					} else if (cooldown < 1) {
						plugin.messageCooldowns.remove(player);
						player.sendMessage(cantMove);
						plugin.messageCooldowns.put(player, Integer.valueOf(6));
					}

				} else {
					player.sendMessage(cantMove);
					plugin.messageCooldowns.put(player, Integer.valueOf(6));
				}
			}

		}

	}

}
