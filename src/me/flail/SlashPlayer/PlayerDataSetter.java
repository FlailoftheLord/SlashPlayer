package me.flail.SlashPlayer;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataSetter implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Utilities chat = new Utilities();

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration config = plugin.getConfig();

		Player player = event.getPlayer();

		UUID pUuid = player.getUniqueId();

		boolean isBanned = pData.getBoolean(pUuid + ".IsBanned");

		String helpLink = config.getString("HelpLink");

		String banMessage = config.getString("DefaultBanMessage");

		String pName = player.getName().toString();

		if (isBanned) {

			player.kickPlayer(chat.m("%prefix% " + banMessage.replace("%help_link%", helpLink)));
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

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", false);

		plugin.savePlayerData();

	}

}
