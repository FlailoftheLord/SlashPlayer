package me.flail.slashplayer.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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

	/**
	 * TBH i don't even know why i put this here... lol
	 * 
	 * @return myself.
	 */
	public User me() {
		return this;
	}

	public UUID uuid() {
		return playerUuid;
	}

	public int rank() {
		for (int r = 0; r < 101; r++) {
			if (hasPermission("slashplayer.rank." + r)) {
				return r;
			}
		}
		return 0;
	}

	public String id() {
		return uuid().toString();
	}

	/**
	 * @return This user's {@link Player} object if online. null otherwise.
	 */
	public Player player() {
		return offlinePlayer().isOnline() ? plugin.server.getPlayer(uuid()) : null;
	}

	public OfflinePlayer offlinePlayer() {
		return Bukkit.getOfflinePlayer(uuid());
	}

	public DataFile dataFile() {
		return this.getDataFile();
	}

	/**
	 * Loads the user's data file. Always trigger this when they join the server.
	 */
	public void setup(boolean verbose) {
		dataFile().load();
		loadDefaultValues(this);
		if (verbose) {
			console("Loaded UserData for &7" + name() + "&8[" + ip() + "]" + "  &8(" + uuid() + ")");
		}

	}

	public void logout() {
		setOnline(false);
		plugin.players.remove(this);
		plugin.openGuis.remove(this.uuid());
	}

	public String name() {
		return offlinePlayer().getName();
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

	public boolean hasPermission(String permission) {
		return player().hasPermission(permission);
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

	public String onlineStatus() {
		return isOnline() ? "online" : "offline";
	}

	protected void setOnline(boolean status) {
		dataFile().setValue("Online", Boolean.valueOf(status));
	}

	public void kick(KickReason reason) {
		setOnline(false);
		switch (reason) {
		case BANNED:
			player().kickPlayer(this.getBanMessage().toSingleString());
			break;
		case MUTED:
			player().kickPlayer(new Message("Muted").stringValue());
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

	public void ouch() {
		player().damage(0.1);
		player().sendMessage(chat("&4&l<3"));
	}

	public void heal(boolean removePotionEffects) {
		player().setHealth(20);
		feed(22);

		if (removePotionEffects) {
			player().getActivePotionEffects().clear();
		}
	}

	public void feed(int level) {
		player().setFoodLevel(level);
	}

	public ItemStack getSkull() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(offlinePlayer());
		item.setItemMeta(meta);

		item = this.addTag(item, "user", id());

		return item;
	}

	public ItemStack headerItem() {
		ItemStack skull = getSkull();
		DataFile guiConfig = new DataFile("GuiConfig.yml");
		List<String> lore = new ArrayList<>();
		lore.add(chat("&8" + uuid()));

		List<String> loreFormat = guiConfig.getList("Header.info");

		for (String line : loreFormat) {
			lore.add(this.placeholders(line, commonPlaceholders()));
		}

		ItemMeta meta = skull.getItemMeta();
		meta.setDisplayName(chat(guiConfig.getValue("Header.NameColor") + name()));
		meta.setLore(lore);
		skull.setItemMeta(meta);

		this.addTag(skull, "uuid", id());

		return skull;
	}

	public Map<String, String> commonPlaceholders() {
		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("%uuid%", uuid().toString());
		placeholders.put("%player%", name());
		placeholders.put("%status-online%", onlineStatus());

		if (this.isOnline()) {
			placeholders.put("%health%", player().getHealth() + "");
			placeholders.put("%food%", player().getFoodLevel() + "");
			placeholders.put("%gamemode%", player().getGameMode().toString().toLowerCase());
			placeholders.put("%status-mute%", isMuted() + "");
			placeholders.put("%status-frozen%", isFrozen() + "");
			placeholders.put("%status-ban%", isBanned() + "");
			placeholders.put("%rank%", rank() + "");
			placeholders.put("%ban-duration%", this.banDuration() + " seconds");
			placeholders.put("%unban-time%", this.banExpiry());
		}

		return placeholders;
	}

}
