package me.flail.SlashPlayer.Executables;

import java.util.Locale;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.flail.SlashPlayer.FileManager.FileManager;

public class FlyControl {

	private FileManager manager = new FileManager();

	public boolean fly(Player player) {

		FileConfiguration pData = manager.getFile("PlayerData");

		String pUuid = player.getUniqueId().toString();

		boolean isFlying = pData.getBoolean(pUuid + ".IsFlying", false);

		if (!isFlying) {
			player.setAllowFlight(true);
			player.setFlying(true);
			pData.set(pUuid + ".IsFlying", true);

			manager.saveFile(pData);
			return true;
		} else {
			pData.set(pUuid + ".IsFlying", false);
			player.setFlying(false);
			player.setAllowFlight(false);

		}

		manager.saveFile(pData);
		return false;

	}

	public void flyLogin(Player player) {

		FileConfiguration pData = manager.getFile("PlayerData");

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

			manager.saveFile(pData);
		}

	}

}
