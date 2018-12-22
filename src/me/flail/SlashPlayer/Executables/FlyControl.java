package me.flail.SlashPlayer.Executables;

import java.util.Locale;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.flail.SlashPlayer.SlashPlayer;

public class FlyControl {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	public void fly(Player player) {

		FileConfiguration pData = plugin.getPlayerData();

		String pUuid = player.getUniqueId().toString();

		boolean isFlying = pData.getBoolean(pUuid + ".IsFlying");

		if (isFlying) {
			player.setAllowFlight(true);
			player.setFlying(true);
		} else {
			pData.set(pUuid + ".IsFlying", false);
			player.setFlying(false);
			player.setAllowFlight(false);
		}

		plugin.savePlayerData();

	}

	public void flyLogin(Player player) {

		FileConfiguration pData = plugin.getPlayerData();

		if (player != null) {

			if (player.getGameMode().equals(GameMode.SURVIVAL)) {

				String pUuid = player.getUniqueId().toString();

				boolean isFlying = pData.getBoolean(pUuid + ".IsFlying");

				String playerBlock = player.getLocation().add(0, -1, 0).getBlock().getType().toString()
						.toLowerCase(Locale.ENGLISH);

				if (playerBlock.equals("air")) {

					if (isFlying) {

						player.setAllowFlight(true);
						player.setFlying(true);

					} else {
						player.setAllowFlight(false);
						player.setFlying(false);
						pData.set(pUuid + ".IsFlying", false);
					}

				}

			} else if (player.getGameMode().equals(GameMode.CREATIVE)) {

				String pUuid = player.getUniqueId().toString();

				pData.set(pUuid + ".IsFlying", true);
				pData.set(pUuid + ".Gamemode", "Creative");
				player.setAllowFlight(true);
				player.setFlying(true);
			}

			plugin.savePlayerData();
		}

	}

}
