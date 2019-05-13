package me.flail.slashplayer.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.sp.gui.GuiControl;
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

	public String id() {
		return uuid().toString();
	}

	/**
	 * @return This user's {@link Player} object if online. null otherwise.
	 */
	public Player player() {
		return plugin.server.getOfflinePlayer(uuid()).isOnline() ? plugin.server.getPlayer(uuid()) : null;
	}

	public OfflinePlayer offlinePlayer() {
		return plugin.server.getOfflinePlayer(uuid());
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
		plugin.openGuis.remove(this);
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

	/**
	 * Opens the SlashPlayer GUI of the specified <code>subject</code> for this user.
	 * 
	 * @param subject
	 * @param type
	 *                    whether to open the main menu or the Gamemode changer
	 */
	public void moderatePlayer(User subject, String type) {
		new GuiControl(subject).openModerationGui(this, type);
	}

	public void ouch() {
		player().damage(0.1);
		player().sendMessage(chat("&4&l<3"));
	}

	public ItemStack getSkull() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(offlinePlayer());
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack headerItem() {
		ItemStack skull = getSkull();
		DataFile guiConfig = new DataFile("GuiConfig.yml");
		List<String> lore = new ArrayList<>();
		lore.add(chat("&8" + uuid()));

		List<String> loreFormat = guiConfig.getList("Header.info");

		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("%uuid%", uuid().toString());
		placeholders.put("%player%", name());

		if (this.isOnline()) {
			placeholders.put("%health%", player().getHealth() + "");
			placeholders.put("%food%", player().getFoodLevel() + "");
			placeholders.put("%gamemode%", player().getGameMode().toString().toLowerCase());
			placeholders.put("%status-mute%", isMuted() + "");
			placeholders.put("%status-frozen%", isFrozen() + "");
			placeholders.put("%status-ban%", isBanned() + "");
		}
		for (String line : loreFormat) {
			lore.add(this.placeholders(line, placeholders));
		}

		ItemMeta meta = skull.getItemMeta();
		meta.setDisplayName(chat(guiConfig.getValue("Header.NameColor") + name()));
		meta.setLore(lore);
		skull.setItemMeta(meta);

		return skull;
	}

}
