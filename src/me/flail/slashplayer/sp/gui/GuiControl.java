package me.flail.slashplayer.sp.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.executables.Executioner;
import me.flail.slashplayer.gui.GeneratedGui;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

/**
 * Use this to load Gui files
 * 
 * @author FlailoftheLord
 */
public class GuiControl extends Logger {
	private DataFile file;

	public GuiControl() {
	}

	public GuiControl loadGui(String path, boolean verbose, boolean generate) {
		file = new DataFile("GuiConfigurations/" + path);
		if (generate) {
			new GuiGenerator(file).run();
		}
		if (verbose) {
			console("loaded Gui file: " + path);
		}
		return this;
	}

	public void openModerationGui(User operator, User subject) {
		if (operator.player().hasPermission("slashplayer.command")) {
			GeneratedGui guiData = plugin.loadedGuis.get("PlayerGui.yml");
			Gui gui = new Gui(guiData);
			gui = gui.setTitle(guiData.title().replace("%player%", subject.name()));

			gui.open(operator, subject);
		}
	}

	public void openGamemodeGui(User operator, User subject) {
		if (operator.player().hasPermission("slashplayer.gamemode")) {
			GeneratedGui guiData = plugin.loadedGuis.get("GamemodeGui.yml");
			Gui gui = new Gui(guiData);
			gui = gui.setTitle(guiData.title().replace("%player%", subject.name()));

			gui.open(operator, subject);
		}
	}

	public void playerListGui(User operator) {
		if (operator.hasPermission("slashplayer.command")) {
			GeneratedGui guiData = plugin.loadedGuis.get("PlayerListGui.yml");
			Gui gui = new Gui(guiData);
			gui.open(operator, null);
		}
	}

	public void reportListGui(User operator) {
		if (operator.hasPermission("slashplayer.command")) {
			GeneratedGui guiData = plugin.loadedGuis.get("ReportGui.yml");
			Gui gui = new Gui(guiData);
			gui.open(operator, null);
		}
	}

	public DataFile file() {
		return file;
	}

	public boolean processClick(User operator, Gui gui, ItemStack clickedItem, int slot, boolean shiftClick) {
		ItemStack header = gui.getHeader();

		if (shiftClick && hasTag(clickedItem, "shift-click-remove")) {
			if (gui.data().dataFile().name().equals("ReportGui.yml")) {
				String id = getTag(clickedItem, "uuid");
				DataFile reportedPlayers = new DataFile("ReportedPlayers.yml");

				reportedPlayers.setValue(id, null);


			}

			return true;
		}

		if (header != null) {
			User subject = new User(UUID.fromString(this.getTag(header, "user")));

			boolean equalsCanExecute = plugin.config.getBoolean("EqualsCanExecute");

			if (this.hasTag(clickedItem, "execute")) {
				Exe exe = Exe.get(this.getTag(clickedItem, "execute"));

				if ((equalsCanExecute && (subject.rank() <= operator.rank())) || (subject.rank() < operator.rank())) {
					new Executioner(subject, exe).execute(operator);
				} else {
					new Message("RankTooLow").placeholders(subject.commonPlaceholders()).replace("%executable%", exe.toString())
					.send(operator, operator);
				}

			}

			if (this.hasTag(clickedItem, "close-after-click")) {
				boolean closeAfterClick = Boolean.valueOf(getTag(clickedItem, "close-after-click")).booleanValue();

				if (closeAfterClick) {
					operator.closeGui();
				}
			}

			return true;
		}



		if (hasTag(clickedItem, "inv-backup")) {
			User subject = new User(UUID.fromString(getTag(clickedItem, "uuid")));

			subject.restoreInv(getTag(clickedItem, "inv-backup"));

			new Message("InventoryRestored").send(subject, operator);
			new Message("PlayerInventoryRestored").placeholders(subject.commonPlaceholders()).send(operator, operator);
			operator.closeGui();
			return true;
		}

		if (hasTag(clickedItem, "user")) {
			UUID uuid = UUID.fromString(this.getTag(clickedItem, "user"));
			User subject = new User(uuid);
			if (operator.hasPermission("slashplayer.command")) {
				openModerationGui(operator, subject);
			}
		}

		return true;
	}

	public void invRestoreGui(User subject, User operator) {
		DataFile invGui = new DataFile("GuiConfigurations/RestoreInvGui.yml");
		DataFile invData = subject.dataFile();

		if ((invData != null) && !invData.keySet().isEmpty()) {
			List<String> backupNames = new ArrayList<>();

			backupNames.addAll(invData.keySet("InventoryBackups"));
			int index = 0;

			Map<Integer, ItemStack> items = new HashMap<>();

			for (String name : backupNames) {
				Material material = Material.matchMaterial(invGui.getValue("Format.Item"));
				if ((material == null) || material.equals(null)) {
					material = Material.BARRIER;
				}
				ItemStack item = new ItemStack(material);
				ItemMeta meta = item.getItemMeta();

				List<String> lore = invGui.getList("Format.Lore");
				List<String> nLore = new ArrayList<>();
				for (String line : lore) {
					nLore.add(chat(line.replace("%player%", subject.name()).replace("%inventory-backup-date%", name)));
				}

				meta.setDisplayName(chat(invGui.getValue("Format.Name").replace("%player%", subject.name())
						.replace("%inventory-backup-date%", name)));
				meta.setLore(nLore);
				item.setItemMeta(meta);
				item = addTag(item, "inv-backup", name);
				item = addTag(item, "uuid", subject.id());

				items.put(Integer.valueOf(index), item);

				index++;
			}

			while (index < 54) {
				ItemStack fillerItem = new ItemStack(Material.matchMaterial(invGui.getValue("Format.FillerItem")));
				ItemMeta meta = fillerItem.getItemMeta();

				meta.setDisplayName(" ");
				fillerItem.setItemMeta(meta);

				items.put(Integer.valueOf(index), fillerItem);

				index++;
			}

			GeneratedGui invBackupGui = new GeneratedGui(invGui, items);
			plugin.loadedGuis.put("RestoreInvGui.yml", invBackupGui);

			Gui gui = new Gui(invBackupGui);

			gui.open(operator, subject);
		}

	}

}
