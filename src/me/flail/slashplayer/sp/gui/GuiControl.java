package me.flail.slashplayer.sp.gui;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

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

	public GuiControl loadGui(String path, boolean verbose) {
		file = new DataFile("GuiConfigurations/" + path);
		new GuiGenerator(file).run();
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

	public boolean processClick(User operator, Gui gui, ItemStack clickedItem) {
		ItemStack header = gui.getHeader();

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

		if (hasTag(clickedItem, "user")) {
			UUID uuid = UUID.fromString(this.getTag(clickedItem, "user"));
			User subject = new User(uuid);
			if (operator.hasPermission("slashplayer.command")) {
				openModerationGui(operator, subject);
			}
		}

		return true;
	}

}
