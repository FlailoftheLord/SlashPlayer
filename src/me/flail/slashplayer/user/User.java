package me.flail.slashplayer.user;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Time;

public class User extends UserData {

	public enum KickReason {
		BANNED, MUTED, WARNING, CUSTOM
	}

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

	public void logout() {
		setOnline(false);
	}

	public String name() {
		return player().getName();
	}

	public String ip() {
		return player() != null ? player().getAddress().toString().replace("/", "") : "user.not.online";
	}

	public String gamemode() {
		return player() != null ? player().getGameMode().toString().toLowerCase() : "user not online";
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
		return player().isDead() ? true : false;
	}

	public boolean command(String command) {
		return isOnline() ? player().performCommand(command) : false;
	}

	public boolean operatorCommand(String command) {
		return isOnline() ? plugin.server.dispatchCommand(player(), command) : false;
	}

	public boolean isOnline() {
		return player().isOnline();
	}

	protected void setOnline(boolean status) {
		dataFile().setValue("Online", Boolean.valueOf(status));
	}

	public void kick(KickReason reason) {
		setOnline(false);
		switch (reason) {
		case BANNED:
			player().kickPlayer(this.getBanMessage());
			break;
		case MUTED:
			player().kickPlayer(new Message("Muted").get());
			break;
		case WARNING:

			break;
		case CUSTOM:

			break;
		}
	}

	public boolean ban(long duration) {
		Instant instant = Time.currentInstant();
		dataFile().setValue("Banned", Boolean.valueOf(true));
		dataFile().setValue("BanDuration", duration + "");
		dataFile().setValue("UnbanTime", Time.finalBan(instant, duration));
		if (isOnline()) {
			kick(KickReason.BANNED);
		}

		return this.isBanned();
	}

}
