package me.flail.SlashPlayer.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.FileManager.FileManager;

public class InventoryManager {

	private SlashPlayer plugin = JavaPlugin.getPlugin(SlashPlayer.class);

	public boolean clearInventory(OfflinePlayer target) {
		if (target.isOnline()) {
			Player subject = target.getPlayer();
			FileManager manager = new FileManager(plugin);
			FileConfiguration invData = manager.getFile(plugin, "InventoryData.yml");

			String pUuid = subject.getUniqueId().toString();

			ItemStack[] invContent = subject.getInventory().getStorageContents();
			ItemStack[] armorContent = subject.getInventory().getArmorContents();
			ItemStack[] extraContent = subject.getInventory().getExtraContents();

			List<ItemStack> inventory = new ArrayList<>();
			List<ItemStack> armor = new ArrayList<>();
			List<ItemStack> extra = new ArrayList<>();

			for (ItemStack item : invContent) {
				if ((item != null) && (item.getType() != Material.AIR)) {
					inventory.add(item);
				}
			}
			for (ItemStack item : armorContent) {
				if ((item != null) && (item.getType() != Material.AIR)) {
					armor.add(item);
				}
			}
			for (ItemStack item : extraContent) {
				if ((item != null) && (item.getType() != Material.AIR)) {
					extra.add(item);
				}
			}

			if (!inventory.isEmpty()) {
				invData.set(pUuid + ".Inventory", inventory);
			} else {
				invData.set(pUuid + ".Inventory", null);
			}
			if (!armor.isEmpty()) {
				invData.set(pUuid + ".Armor", armor);
			} else {
				invData.set(pUuid + ".Armor", null);
			}
			if (!extra.isEmpty()) {
				invData.set(pUuid + ".Extras", extra);
			} else {
				invData.set(pUuid + ".Extras", null);
			}

			manager.saveFile(plugin, "InventoryData.yml", invData);

			subject.getInventory().clear();

			return true;
		} else {
			return false;
		}

	}

	public boolean restoreInventory(OfflinePlayer target) {
		if (target.isOnline()) {
			Player subject = target.getPlayer();
			FileManager manager = new FileManager(plugin);
			FileConfiguration invData = manager.getFile(plugin, "InventoryData.yml");

			String pUuid = subject.getUniqueId().toString();

			List<?> inventoryContent = invData.getList(pUuid + ".Inventory");
			List<?> armorContent = invData.getList(pUuid + ".Armor");
			List<?> extraContent = invData.getList(pUuid + ".Extras");
			List<ItemStack> inventory = new ArrayList<>();
			List<ItemStack> armor = new ArrayList<>();
			List<ItemStack> extra = new ArrayList<>();

			if ((inventoryContent != null) && !inventoryContent.isEmpty()) {
				for (Object obj : inventoryContent) {
					inventory.add((ItemStack) obj);
				}
			}
			if ((armorContent != null) && !armorContent.isEmpty()) {
				for (Object obj : armorContent) {
					armor.add((ItemStack) obj);
				}
			}
			if ((extraContent != null) && !extraContent.isEmpty()) {
				for (Object obj : extraContent) {
					extra.add((ItemStack) obj);
				}
			}

			this.clearInventory(subject);

			subject.getInventory().setContents(inventory.toArray(new ItemStack[] {}));
			subject.getInventory().setArmorContents(armor.toArray(new ItemStack[] {}));
			subject.getInventory().setExtraContents(extra.toArray(new ItemStack[] {}));

			return true;
		} else {
			return false;
		}

	}

	public void openPlayerInventory(Player operator, Player target) {
		operator.openInventory(target.getInventory());
	}

	public void openPlayerEnderchest(Player operator, Player target) {
		operator.openInventory(target.getEnderChest());
	}

}
