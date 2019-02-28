package me.flail.SlashPlayer.Utilities;

import java.util.UUID;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.flail.SlashPlayer.Commands;
import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Executables.FlyControl;
import me.flail.SlashPlayer.Runnables.BanControl;

public class PlayerDataSetter extends Tools implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerJoin(PlayerJoinEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		pData.options().header(
				"All relevant Player data is stored in this file. \nThis is for storage purposes ONLY. \nPlease do not edit or change anything!\n");

		Player player = event.getPlayer();

		UUID pUuid = player.getUniqueId();

		BanControl banControl = new BanControl();

		boolean isBanned = banControl.checkBanned(event.getPlayer());

		if (isBanned) {
			banControl.banPlayer(event.getPlayer(), null, plugin.banTimer.get(event.getPlayer()).intValue());
			event.setJoinMessage("");
			return;
		}

		plugin.players.put(pUuid, player);

		new FlyControl().flyLogin(player);

		String pName = player.getName().toString();

		for (String s : pData.getKeys(false)) {

			String oldName = pData.get(s + ".Name").toString();
			if (oldName.equalsIgnoreCase(pName) && (s != pUuid.toString())) {
				pData.set(s, null);
			}

		}

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", true);
		pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

		plugin.savePlayerData(pData);

		PluginCommand pCommand = getCommand(pName, plugin);
		pCommand.setExecutor(new Commands());

	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerLeave(PlayerQuitEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		plugin.players.remove(player.getUniqueId());

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", false);

		if (pData.getBoolean(pUuid + ".IsBanned")) {
			event.setQuitMessage("");
		}

		plugin.savePlayerData(pData);

	}

}
