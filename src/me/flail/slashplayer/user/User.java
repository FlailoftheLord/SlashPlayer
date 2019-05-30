package me.flail.slashplayer.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import me.flail.slashplayer.SlashPlayer;
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

	public static User fromName(String username) {
		return SlashPlayer.offlinePlayer(username);
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
		if (isOnline()) {
			for (int r = 100; r > -1; r--) {
				if (hasPermission("slashplayer.rank." + r)) {
					return r;
				}
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

		new GuiControl().loadGui("PlayerListGui.yml", false);
	}

	public void logout() {
		setOnline(false);
		plugin.openGuis.remove(this.uuid());

		new GuiControl().loadGui("PlayerListGui.yml", false);
	}

	/**
	 * Closes this user's active GUI if they have one open.
	 */
	public void closeGui() {
		if (isOnline()) {
			player().closeInventory();
		}
	}

	public String name() {
		return offlinePlayer().getName();
	}

	public String ip() {
		return isOnline() ? player().getAddress().toString().replace("/", "") : "user.not.online";
	}

	public String gamemode() {
		return isOnline() ? player().getGameMode().toString().toLowerCase() : "user not online";
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

	public boolean isReported() {
		return dataFile().getBoolean("Reported");
	}

	public boolean isStaff() {
		return hasPermission("slashplayer.staff");
	}

	public boolean hasPermission(String permission) {
		if (isOnline()) {
			return player().hasPermission(permission);
		}

		return false;
	}

	public boolean command(String command) {
		return isOnline() ? player().performCommand(command) : false;
	}

	public boolean operatorCommand(String command) {
		return isOnline() ? plugin.server.dispatchCommand(player(), command) : false;
	}

	public boolean isOnline() {
		return offlinePlayer().isOnline();
	}

	public String onlineStatus() {
		return isOnline() ? "online" : "offline";
	}

	protected void setOnline(boolean status) {
		dataFile().setValue("Online", Boolean.valueOf(status));
		if (!status) {
			plugin.players.remove(uuid());
			return;
		}

		plugin.players.put(uuid(), this);
	}

	public DataFile reportedPlayerList() {
		return new DataFile("ReportedPlayers.yml");
	}

	public boolean report(User reporter, String reason) {
		DataFile reports = new DataFile("ReportedPlayers.yml");
		List<String> players = new ArrayList<>();

		if (reports.hasValue("ReportedPlayers")) {
			players.addAll(reports.getList("ReportedPlayers"));
		}
		players.add(this.id());
		for (String id : players) {
			reports.setValue(id + ".Reason", reason);
			reports.setValue(id + ".Reporter", reporter.id());
		}

		dataFile().setValue("Reported", "true");

		return !reason.isEmpty();
	}

	public void kick(KickReason reason) {
		switch (reason) {
		case BANNED:
			if (this.getBanMessage() != null) {
				player().kickPlayer(this.getBanMessage().toSingleString());
				break;
			}
			console("&c" + name() + " is not actually banned. And thusly, cannot be kicked for being banned.");
			break;
		case MUTED:
			player().kickPlayer(new Message("Muted").stringValue());
			break;
		case WARNING:
			;
		default:
			player().kickPlayer(new Message("KickMessage").stringValue().replace("%player%", name()));
			break;
		}

		this.logout();
	}

	public boolean ban(long duration) {
		Instant instant = Time.currentInstant();

		dataFile().setValue("BanDuration", duration + "");
		dataFile().setValue("UnbanTime", Time.finalBan(instant, duration).toString());
		dataFile().setValue("Banned", "true");
		if (isOnline()) {
			kick(KickReason.BANNED);
		}

		return this.isBanned();
	}

	public void unban() {
		dataFile().setValue("BanDuration", null);
		dataFile().setValue("UnbanTime", null);
		dataFile().setValue("Banned", "false");
	}

	public void toggleFly(User operator) {
		if (isOnline()) {
			if (player().getAllowFlight()) {
				player().setFlying(false);
				player().setAllowFlight(false);

				new Message("FlyOff").send(this, operator);
				new Message("PlayerFlyOff").placeholders(this.commonPlaceholders()).send(operator, operator);

				return;
			}

			player().setAllowFlight(true);
			player().setFlying(true);

			new Message("FlyOn").send(this, operator);
			new Message("PlayerFlyOn").placeholders(this.commonPlaceholders()).send(operator, operator);
			return;
		}

	}

	/**
	 * @return true if the user was frozen, false otherwise.
	 */
	public boolean freeze() {
		String blockInteract = plugin.config.get("Frozen.BlockInteract").toString().toLowerCase();
		this.closeGui();


		if (!isFrozen()) {
			dataFile().setValue("Frozen", "true");
			if (blockInteract.equals("deny")) {
				setGamemode(GameMode.ADVENTURE);
			}


			return true;
		}

		dataFile().setValue("Frozen", "false");
		if (blockInteract.equals("deny")) {
			setGamemode(GameMode.SURVIVAL);
		}
		return false;
	}

	public void ouch() {
		player().damage(0.1);
		player().sendMessage(chat("&4&l<3"));
	}

	public void kill(Message message) {
		if (isOnline()) {
			player().setHealth(0);
			message.send(this, null);
		}
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

	public void setGamemode(GameMode mode) {
		if (isOnline()) {
			player().setGameMode(mode);
		}

		dataFile().setValue("Gamemode", this.gamemode());
	}

	public void burn(boolean toggle, int time) {
		if (toggle) {
			player().setFireTicks(time);
			return;
		}
		player().setFireTicks(0);
	}

	public void backupInventory() {
		DataFile invData = new DataFile("InventoryData.yml");
		List<ItemStack> storedList = new ArrayList<>();

		ItemStack[] invContents = player().getInventory().getContents();
		for (ItemStack item : invContents) {
			if (item != null) {
				storedList.add(item);
			}
		}

		if (!storedList.isEmpty()) {
			invData.setValue(me().id() + ".InventoryBackup." + Time.currentDate().toString(), storedList);
		}
	}

	public void clearInventory(boolean backup) {
		if (backup) {
			this.backupInventory();
		}

		player().getInventory().clear();
	}

	public void restoreInv() {

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

		if (this.isReported()) {
			String reason = this.reportedPlayerList().getValue(id() + ".Reason");
			placeholders.put("%reason%", reason);
		}

		return placeholders;
	}

	public boolean spawnNewFriend() {
		if (isOnline()) {
			List<EntityType> entities = this.validMobs("both");
			int rInt = new Random().nextInt(entities.size());

			Location location = player().getLocation();
			Entity frend = location.getWorld().spawnEntity(location, entities.get(rInt));
			if (frend instanceof LivingEntity) {
				LivingEntity liveFrend = (LivingEntity) frend;

				liveFrend.setAI(true);
				liveFrend.setCustomName(chat("&cYour Friend."));
				liveFrend.setCustomNameVisible(true);
				liveFrend.setRemoveWhenFarAway(false);
				liveFrend.setCanPickupItems(false);
				liveFrend.setCollidable(false);

				liveFrend.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
				liveFrend.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
				liveFrend.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);


				AttributeInstance atkDmg = liveFrend.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
				if (atkDmg != null) {
					liveFrend.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0);
				}
				AttributeInstance flySpd = liveFrend.getAttribute(Attribute.GENERIC_FLYING_SPEED);
				if (flySpd != null) {
					liveFrend.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(0);
				}

				liveFrend.setMetadata("SlashPlayerFrend",
						new FixedMetadataValue(plugin, liveFrend.getType().toString().toLowerCase()));

			}

		}

		return false;
	}

}
