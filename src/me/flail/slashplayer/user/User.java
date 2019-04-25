package me.flail.slashplayer.user;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.tools.DataFile;

public class User extends UserData {

	public User(SlashPlayer plugin, UUID uuid) {
		super(plugin, uuid);
	}

	public UUID uuid() {
		return playerUuid;
	}

	/**
	 * @return This user's {@link Player} object if online. null otherwise.
	 */
	public Player player() {
		return plugin.server.getOfflinePlayer(playerUuid).isOnline() ? plugin.server.getPlayer(playerUuid) : null;

	}

	public DataFile dataFile() {
		return this.getDataFile();
	}

	public String name() {
		return player().getName();
	}

	public boolean isBanned() {
		return false;
	}

	public boolean isMuted() {
		return false;
	}

	public boolean isFrozen() {
		return false;
	}

	public boolean isDead() {
		return player().isDead();
	}

	public boolean command(String command) {
		return player().performCommand(command);
	}

	public boolean operatorCommand(String command) {
		return plugin.server.dispatchCommand(player(), command);
	}

}
