package me.flail.SlashPlayer.Executables;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Tools;
import me.flail.SlashPlayer.GUI.GamemodeInventory;

public class Executables implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	@EventHandler
	public void executables(InventoryClickEvent event) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration guiConfig = plugin.getGuiConfig();

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration messages = plugin.getMessages();

		Inventory inv = event.getInventory();

		InventoryHolder holder = event.getInventory().getHolder();

		if ((holder != null) && (holder instanceof Player)) {

			Player subject = (Player) holder;

			int headerSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			ItemStack pInfo = inv.getItem(headerSlot - 1);

			String loreUid = "";

			Player pInfoPlayer = null;

			if ((pInfo != null) && pInfo.hasItemMeta() && pInfo.getItemMeta().hasLore()) {

				List<String> lore = pInfo.getItemMeta().getLore();

				loreUid = ChatColor.stripColor(lore.get(0));

				pInfoPlayer = plugin.server.getPlayer(UUID.fromString(loreUid));

			} else {

			}

			if ((pInfoPlayer != null) && pInfoPlayer.equals(subject)) {

				String invTitle = chat.m(config.getString("PlayerMenuTitle").replace("%player%", subject.getName()));

				if (inv.getTitle().equalsIgnoreCase(invTitle)) {

					Player operator = (Player) event.getWhoClicked();

					Player player = subject;

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						operator.closeInventory();
					}

					String pUuid = player.getUniqueId().toString();

					String cantUseExe = messages.getString("AccessDenied");

					ItemStack item = event.getCurrentItem();

					ItemMeta iM;

					int slot;

					if ((item != null) && item.hasItemMeta()) {
						iM = item.getItemMeta();
						slot = iM.getEnchantLevel(Enchantment.MENDING);
						ConfigurationSection cs = guiConfig.getConfigurationSection("PlayerInfo." + slot);

						if (cs != null) {
							String exe = cs.getString("Execute");

							if ((exe != null) && (exe != "")) {

								boolean closeInv = cs.getBoolean("CloseInventory");

								switch (exe.toLowerCase()) {

								case "teleporttoplayer":

									if (operator.hasPermission("slashplayer.teleport")) {

										operator.teleport(player);

										operator.sendMessage(chat.msg(messages.getString("TeleportPlayer"), player,
												operator, exe, "teleport"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "teleport"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "teleportplayer":

									if (operator.hasPermission("slashplayer.summon")) {

										player.teleport(operator);

										player.sendMessage(chat.msg(messages.getString("Summoned"), player, operator,
												exe, "summon"));

										operator.sendMessage(chat.msg(messages.getString("SummonPlayer"), player,
												operator, exe, "summon"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "summon"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "setgamemode":

									if (operator.hasPermission("slashplayer.gamemode")) {

										Inventory gmInv = new GamemodeInventory().gamemodeInv(player);
										operator.openInventory(gmInv);

									} else {
										if (closeInv != false) {
											operator.closeInventory();
										}
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "gamemode"));
									}

									break;

								case "healplayer":

									if (operator.hasPermission("slashplayer.heal")) {

										player.setHealth(20);

										player.sendMessage(
												chat.msg(messages.getString("Healed"), player, operator, exe, "heal"));

										operator.sendMessage(chat.msg(messages.getString("HealPlayer"), player,
												operator, exe, "heal"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "heal"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "feedplayer":

									if (operator.hasPermission("slashplayer.feed")) {

										player.setFoodLevel(20);

										player.sendMessage(
												chat.msg(messages.getString("Fed"), player, operator, exe, "feed"));
										operator.sendMessage(chat.msg(messages.getString("FeedPlayer"), player,
												operator, exe, "feed"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "feed"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "freezeplayer":

									if (operator.hasPermission("slashplayer.freeze")) {

										pData.set(pUuid + ".IsFrozen", true);

										if (config.getBoolean("Freeze.AdventureMode")) {

											pData.set(pUuid + ".Gamemode", player.getGameMode().toString());

											player.setGameMode(GameMode.ADVENTURE);

										}

										player.sendMessage(chat.msg(messages.getString("Frozen"), player, operator, exe,
												"freeze"));

										operator.sendMessage(chat.msg(messages.getString("FreezePlayer"), player,
												operator, exe, "freeze"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "freeze"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "unfreezeplayer":

									if (operator.hasPermission("slashplayer.freeze")) {

										pData.set(pUuid + ".IsFrozen", false);

										if (config.getBoolean("Freeze.AdventureMode")) {

											String pGm = pData.getString(pUuid + ".Gamemode");

											GameMode gm = GameMode.valueOf(pGm.toUpperCase());

											player.setGameMode(gm);

										}

										player.sendMessage(chat.msg(messages.getString("Unfrozen"), player, operator,
												exe, "unfreeze"));

										operator.sendMessage(chat.msg(messages.getString("UnfreezePlayer"), player,
												operator, exe, "unfreeze"));

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "unfreeze"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "kickplayer":

									if (operator.hasPermission("slashplayer.kick")) {

										player.kickPlayer(chat.msg(messages.getString("KickMessage"), player, operator,
												exe, "kick"));

										operator.sendMessage(chat.msg(messages.getString("PlayerKicked"), player,
												operator, exe, "kick"));

										pData.set(pUuid + ".IsOnline", false);

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "kick"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "banplayer":

									if (operator.hasPermission("slashplayer.ban")) {

										if (player.hasPermission("slashplayer.exempt")) {

											operator.sendMessage(chat.msg(messages.getString("CantBanPlayer"), player,
													operator, exe, "ban"));

										} else {

											int banDuration = config.getInt("BanTime");

											player.kickPlayer(chat.msg(messages.getString("Banned"), player, operator,
													exe, "ban"));

											pData.set(pUuid + ".IsBanned", true);

											pData.set(pUuid + ".BanDuration", banDuration);

											operator.sendMessage(chat.msg(messages.getString("BanPlayer"), player,
													operator, exe, "ban"));
											operator.sendMessage(chat.msg(messages.getString("UnbanPrompt"), player,
													operator, exe, "player"));

											pData.set(pUuid + ".IsOnline", false);

										}

									} else {

										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "ban"));
									}
									if (closeInv != false) {
										operator.closeInventory();
									}

									break;

								case "unbanplayer":

									if (operator.hasPermission("slashplayer.ban")) {

										pData.set(pUuid + ".IsBanned", false);

										operator.sendMessage(chat.msg(messages.getString("UnbanPlayer"), player,
												operator, exe, "unban"));

									} else {
										operator.closeInventory();
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "unban"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "togglefly":

									if (operator.hasPermission("slashplayer.fly")) {

										boolean isFlying = pData.getBoolean(pUuid + ".IsFlying");

										if (isFlying) {

											pData.set(pUuid + ".IsFlying", false);

											player.setAllowFlight(false);

											player.sendMessage(chat.msg(messages.getString("FlyOff"), player, operator,
													"TogglFly", "fly"));

											operator.sendMessage(chat.msg(messages.getString("PlayerFlyOff"), player,
													operator, "ToggleFly", "fly"));

										} else {

											pData.set(pUuid + ".IsFlying", true);

											player.setAllowFlight(true);

											player.sendMessage(chat.msg(messages.getString("FlyOn"), player, operator,
													"ToggleFly", "fly"));

											operator.sendMessage(chat.msg(messages.getString("PlayerFlyOn"), player,
													operator, "ToggleFly", "fly"));
										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "fly"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "muteplayer":

									if (operator.hasPermission("slashplayer.mute")) {

										if (player.hasPermission("slashplayer.exempt")) {

											operator.sendMessage(chat.msg(messages.getString("MuteExempt"), player,
													operator, "MutePlayer", "mute"));

										} else {

											pData.set(pUuid + ".IsMuted", true);

											int muteTime = config.getInt("MuteTime");

											pData.set(pUuid + ".MuteDuration", muteTime);

											player.sendMessage(chat.msg(messages.getString("Muted"), player, operator,
													"MutePlayer", "mute"));

											operator.sendMessage(chat.msg(messages.getString("MutePlayer"), player,
													operator, "MutePlayer", "mute"));

										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "mute"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "unmuteplayer":

									if (operator.hasPermission("slashplayer.mute")) {

										boolean isMuted = pData.getBoolean(pUuid + ".IsMuted");

										if (isMuted) {

											pData.set(pUuid + ".IsMuted", false);

											pData.set(pUuid + ".MuteDuration", null);

											player.sendMessage(chat.msg(messages.getString("Unmuted"), player, operator,
													"UnmutePlayer", "mute"));

											operator.sendMessage(chat.msg(messages.getString("UnmutePlayer"), player,
													operator, "UnmutePlayer", "mute"));

										} else {

											String notMuted = messages.getString("NotMuted");

											operator.sendMessage(
													chat.msg(notMuted, player, operator, "UnmutePlayer", "mute"));

										}

									} else {
										operator.sendMessage(chat.msg(cantUseExe, player, operator, exe, "mute"));
									}

									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								case "killplayer":

									if (operator.hasPermission("slahsplayer.kill")) {

										if (!player.hasPermission("slashplayer.exempt")) {
											player.damage(696969, operator);
											player.sendMessage(chat.msg(messages.getString("Killed"), player, operator,
													"KillPlayer", "kill"));
											operator.sendMessage(chat.msg(messages.getString("KillPlayer"), player,
													operator, "KillPlayer", "kill"));

										} else {
											operator.sendMessage(chat.msg(messages.getString("PlayerExempt"), player,
													operator, "KillPlayer", "kill"));
										}

									} else {
										operator.sendMessage(cantUseExe);
									}
									if (closeInv != false) {
										operator.closeInventory();
									}
									break;

								}

							}

							plugin.savePlayerData();
						}

					}

				}
				event.setCancelled(true);
			}

		}

	}

}
