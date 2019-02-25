package me.flail.SlashPlayer.Listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.Utilities.Tools;

public class InteractEvent extends Tools implements Listener {

	SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		boolean isFrozen = pData.getBoolean(pUuid + ".IsFrozen");

		if (isFrozen) {

			FileConfiguration config = plugin.getConfig();

			String blockInteract = config.get("Freeze.Interact").toString();

			if (blockInteract.equalsIgnoreCase("deny")) {

				FileConfiguration messages = plugin.getMessages();

				String cantInteractWhileFrozen = msg(messages.getString("FreezeInteract"), player, player, "Freeze",
						"freeze");

				player.sendMessage(cantInteractWhileFrozen);
				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void playerClick(PlayerInteractEntityEvent event) {

		Entity subject = event.getRightClicked();

		if (subject instanceof Player) {
			Player player = (Player) subject;
			Player operator = event.getPlayer();

			if (operator.hasPermission("slashplayer.command")) {

				String operatorHandItem = operator.getInventory().getItemInMainHand().getType().toString();
				String operatorOffhandItem = operator.getInventory().getItemInOffHand().getType().toString();

				if (operatorHandItem.equalsIgnoreCase("air") || (operatorHandItem == null)) {

					if (operatorOffhandItem.equalsIgnoreCase("golden_axe")
							|| operatorOffhandItem.equalsIgnoreCase("air") || (operatorOffhandItem == null)) {

						plugin.logAction(operator.getName() + " Opened the SlashPlayer gui for " + player.getName());

						Inventory pInv = new PlayerInfoInventory().playerInfo(player);
						operator.openInventory(pInv);
						event.setCancelled(true);

					}

				}

			}

		}

	}

}
