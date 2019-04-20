package me.flail.SlashPlayer.Utilities;

import java.util.UUID;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.ControlCenter.BanControl;
import me.flail.SlashPlayer.Executables.FlyControl;
import me.flail.SlashPlayer.FileManager.FileManager;

public class PlayerEventHandler extends Tools implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);
	private FileManager manager = new FileManager();
	BanControl bans = new BanControl();
	Time time = new Time();

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		boolean isBanned = bans.checkBanned(player);

		if (isBanned) {
			bans.kickBanned(player, null);
			event.setJoinMessage("");
			return;
		}

		plugin.players.put(player.getUniqueId(), player);

		this.setData(player);
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {

		FileConfiguration pData = manager.getFile("PlayerData.yml");

		Player player = event.getPlayer();

		plugin.players.remove(player.getUniqueId());

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", Boolean.valueOf(false));

		if (bans.checkBanned(player)) {
			bans.kickBanned(player, null);
			event.setQuitMessage("");
		}

		manager.saveFile(pData);

	}

	@EventHandler
	public void playerKicked(PlayerKickEvent event) {

		FileConfiguration pData = manager.getFile("PlayerData.yml");

		Player player = event.getPlayer();

		plugin.players.remove(player.getUniqueId());

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", Boolean.valueOf(false));

		if (pData.getBoolean(pUuid + ".IsBanned")) {
			event.setLeaveMessage("");
		}

		manager.saveFile(pData);

	}

	public void setData(Player player) {

		FileConfiguration pData = manager.getFile("PlayerData.yml");
		pData.options().header(
				"All relevant Player data is stored in this file. \nThis is for storage purposes ONLY. \nPlease do not edit or change anything!\n");

		UUID pUuid = player.getUniqueId();

		new FlyControl().flyLogin(player);

		String pName = player.getName().toString();

		for (String s : pData.getKeys(false)) {

			String oldName = pData.get(s + ".Name", "").toString();
			if (oldName.equalsIgnoreCase(pName) && (s != pUuid.toString())) {
				pData.set(s, null);
			}

		}

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", Boolean.valueOf(true));
		pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

		manager.saveFile(pData);

		PluginCommand pCommand = getCommand(pName, plugin);
		pCommand.setExecutor(plugin);

	}

}
