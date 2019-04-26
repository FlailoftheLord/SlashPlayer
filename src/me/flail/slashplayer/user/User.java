package me.flail.slashplayer.user;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.flail.slashplayer.tools.DataFile;

public class User extends UserData {

	public User(UUID uuid) {
		super(uuid);
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

	/**
	 * Loads the user's data file. Always trigger this when they join the server.
	 */
	public void setup(boolean verbose) {
		plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			dataFile().load();
			loadDefaultValues(this);
			if (verbose) {
				console("Loaded UserData for &7" + name() + "&8[" + ip() + "]" + "  &8(" + uuid() + ")");
			}
		}, 12L);
	}

	public String name() {
		return player().getName();
	}

	public String ip() {
		return player().getAddress().toString().replace("/", "");
	}

	public String gamemode() {
		return player().getGameMode().toString().toLowerCase();
	}

	public boolean isBanned() {
		return dataFile().getBoolean("Banned");
	}

	public boolean isMuted() {
		return dataFile().getBoolean("Muted");
	}

	public boolean isFrozen() {
		return dataFile().getBoolean("Frozen");
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
