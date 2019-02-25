package me.flail.SlashPlayer.Listeners;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.Tools;

public class FreezeListener implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void playerMove(PlayerMoveEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		ConfigurationSection cs = pData.getConfigurationSection(pUuid);

		if (cs != null) {

			boolean isFrozen = cs.getBoolean("IsFrozen");

			if (isFrozen) {

				Location from = event.getFrom();
				Location to = event.getTo();

				int fbX = from.getBlockX();
				int fbY = from.getBlockY();
				int fbZ = from.getBlockZ();

				int tbX = to.getBlockX();
				int tbY = to.getBlockY();
				int tbZ = to.getBlockZ();

				if ((fbX != tbX) || (fbY != tbY) || (fbZ != tbZ)) {
					event.setCancelled(true);

					String cantMove = chat.msg(messages.getString("FreezeMove"), player, player, "Freeze", "freeze");

					player.sendMessage(cantMove);
				}

			}

		}

	}

}
