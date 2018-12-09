package me.flail.SlashPlayer;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.flail.SlashPlayer.Executables.FlyControl;

public class PlayerDataSetter implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		Player player = event.getPlayer();

		UUID pUuid = player.getUniqueId();

		plugin.players.put(pUuid, player);

		new FlyControl().flyLogin(player);

		boolean isBanned = pData.getBoolean(pUuid + ".IsBanned");

		String banMessage = messages.getString("Banned");

		String pName = player.getName().toString();

		if (isBanned) {

			player.kickPlayer(chat.msg(banMessage, player, player, "Ban", "ban"));
			pData.set(pUuid + ".IsBanned", isBanned);
		}

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", true);
		pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

		plugin.savePlayerData();

	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		plugin.players.remove(player.getUniqueId());

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", false);

		plugin.savePlayerData();

	}

}
