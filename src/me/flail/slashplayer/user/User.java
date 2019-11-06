package me.flail.slashplayer.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
import org.bukkit.potion.PotionEffect;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Time;

/**
 * Definitely <b>NOT</b> a user Object.
 * 
 * @author FlailoftheLord
 */
public class User extends UserData {

	public enum KickReason {
		BANNED, MUTED, WARNING, CUSTOM
	}

	public User(UUID uuid) {
		super(uuid);
	}

	public User(Player player) {
		super(player.getUniqueId());
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
			for (int r = 100; r > -1; --r) {
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
		return plugin.server.getOfflinePlayer(uuid());
	}

	public DataFile dataFile() {
		return this.getDataFile();
	}

	/**
	 * Loads the user's data file. Always trigger this when they join the server.
	 */
	public void setup(boolean verbose) {
		plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			setOnline(true);

			dataFile().load();
			loadDefaultValues(this);
			if (verbose) {
				console("Loaded UserData for &7" + name() + "&8[" + ip() + "]" + "  &8(" + uuid() + ")");
			}


			setGamemode(GameMode.valueOf(gamemode().toUpperCase()));


			new GuiControl().loadGui("PlayerListGui.yml", false, true);
		}, 20L);
	}

	protected void setupOffline() {
		if (!dataFile().hasList("Name")) {
			List<String> names = new ArrayList<>();
			names.add(name());

			dataFile().setValue("Name", names);
		}
	}

	public void logout() {
		setOnline(false);
		plugin.openGuis.remove(this.uuid());

		new GuiControl().loadGui("PlayerListGui.yml", false, true);
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

	public String playerGamemode() {
		return isOnline() ? player().getGameMode().toString().toLowerCase() : "user not online";
	}

	public String gamemode() {
		return dataFile().hasValue("Gamemode") ? dataFile().getValue("Gamemode") : playerGamemode();
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

	public void sendMessage(String message) {
		player().sendMessage(chat(message));
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

	public void setOnline(boolean status) {
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
		new GuiControl().loadGui("ReportGui.yml", false, true);

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

	public boolean mute(long duration) {
		Instant instant = Time.currentInstant();

		dataFile().setValue("MuteDuration", duration + "");
		dataFile().setValue("UnmuteTime", Time.finalTime(instant, duration).toString());
		dataFile().setValue("Muted", "true");

		return this.isMuted();
	}

	public void unmute() {
		dataFile().setValue("MuteDuration", null);
		dataFile().setValue("UnmuteTime", null);
		dataFile().setValue("Muted", "false");
	}

	public boolean ban(long duration) {
		Instant instant = Time.currentInstant();

		dataFile().setValue("BanDuration", duration + "");
		dataFile().setValue("UnbanTime", Time.finalTime(instant, duration).toString());
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

	public void teleport(User target) {
		if (isOnline() && target.isOnline()) {
			player().teleport(target.player());
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
			for (PotionEffect effect : player().getActivePotionEffects()) {
				player().removePotionEffect(effect.getType());
			}
		}
	}

	public void feed(int level) {
		player().setFoodLevel(level);
	}

	public void setGamemode(GameMode mode) {
		if (isOnline()) {
			player().setGameMode(mode);
		}

		dataFile().setValue("Gamemode", mode.toString().toLowerCase());
	}

	public void burn(boolean toggle, int time) {
		if (toggle) {
			player().setFireTicks(time);
			return;
		}
		player().setFireTicks(0);
	}

	public void backupInventory() {
		List<ItemStack> storedList = new ArrayList<>();

		ItemStack[] invContents = player().getInventory().getContents();
		for (ItemStack item : invContents) {
			if (item != null) {
				storedList.add(item);
			}
		}

		manualInventoryBackup(storedList);
	}

	public void manualInventoryBackup(List<ItemStack> items) {
		if (!items.isEmpty()) {
			dataFile().setValue("InventoryBackup." + Time.currentDate().toString().replace(":", "_"),
					items.toArray(new ItemStack[] {}));
		}
	}

	public void clearInventory(boolean backup) {
		if (backup) {
			this.backupInventory();
		}

		player().getInventory().clear();
	}

	public void restoreInv(String date) {
		String key = "InventoryBackup." + date;
		if (dataFile().hasValue(key)) {
			ItemStack[] itemList = (ItemStack[]) dataFile().getObj(key);

			ItemStack[] currentInv = player().getInventory().getContents();
			for (ItemStack item : currentInv) {
				if (item != null) {
					player().getWorld().dropItemNaturally(player().getLocation(), item);
				}
			}
			player().getInventory().clear();

			for (ItemStack item : itemList) {
				if (item != null) {
					HashMap<Integer, ItemStack> leftovers = player().getInventory().addItem(item);

					if (!leftovers.isEmpty()) {
						for (ItemStack leftover : leftovers.values()) {
							if (leftover != null) {
								player().getWorld().dropItem(player().getLocation(), leftover);
							}
						}
					}
				}

			}

			return;
		}

		console("doesn't have value");
	}

	public void removeInvBackup(String date) {
		String key = "InventoryBackup." + date;
		if (dataFile().hasValue(key)) {

			dataFile().setValue(key, null);
		}

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
		placeholders.put("%gamemode%", gamemode());
		placeholders.put("%status-mute%", isMuted() + "");
		placeholders.put("%status-frozen%", isFrozen() + "");
		placeholders.put("%status-ban%", isBanned() + "");
		if (!isOnline()) {
			placeholders.put("%food%", "user not online");
			placeholders.put("%health%", "user not online");
			placeholders.put("%rank%", "0");
		}

		if (this.isOnline()) {
			placeholders.put("%health%", player().getHealth() + "");
			placeholders.put("%food%", player().getFoodLevel() + "");

			placeholders.put("%rank%", rank() + "");
			if (isBanned()) {
				placeholders.put("%ban-duration%", this.banDuration() + " seconds");
				placeholders.put("%unban-time%", this.banExpiry());
			}
			if (isMuted()) {
				placeholders.put("%mute-duration%", dataFile().getValue("MuteDuration"));
				placeholders.put("%unmute-time%", Time.formatInstant(Instant.parse(dataFile().getValue("UnmuteTime"))));
			}
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
				liveFrend.setCustomName(chat("&c" + name() + "'s Friend."));
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
				AttributeInstance spawnZomboid = liveFrend.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);
				if (spawnZomboid != null) {
					liveFrend.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);
				}
				AttributeInstance followRange = liveFrend.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
				if (followRange != null) {
					liveFrend.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(0);
				}

				liveFrend.setMetadata("SlashPlayerFrend-" + name(),
						new FixedMetadataValue(plugin, liveFrend.getType().toString().toLowerCase()));

			}

		}

		return false;
	}

}
