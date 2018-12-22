package me.flail.SlashPlayer;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.flail.SlashPlayer.Executables.FlyControl;

public class PlayerDataSetter extends Tools implements Listener {

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

		for (String s : pData.getKeys(false)) {

			String oldName = pData.get(s + ".Name").toString();
			if (oldName.equalsIgnoreCase(pName)) {
				pData.set(s, null);
				break;
			}

		}

		if (isBanned) {

			try {

				String timeLeft = pData.get(pUuid + ".BanDuration").toString();

				player.kickPlayer(chat.msg(banMessage.replaceAll("%time%", timeLeft), player, player, "Ban", "ban"));
				pData.set(pUuid + ".IsBanned", isBanned);

			} catch (NullPointerException e) {
				player.kickPlayer(chat.msg(banMessage.replace("%time%", 60 + ""), player, player, "Ban", "ban"));
				pData.set(pUuid + ".IsBanned", isBanned);
			}
		}

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", true);
		pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

		plugin.savePlayerData();

		getCommand(pName, plugin).setExecutor(new Commands());

	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

			plugin.players.clear();

			for (Player p : plugin.server.getOnlinePlayers()) {
				UUID pUuid = p.getUniqueId();
				plugin.players.put(pUuid, p);
			}

		}, 10);

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".IsOnline", false);

		plugin.savePlayerData();

	}

}
