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

	public GuiControl playerList() {
		return loadGui("PlayerListGui.yml");
	}

	public GuiControl playerGui() {
		return loadGui("PlayerGui.yml");
	}

	public GuiControl reportGui() {
		return loadGui("ReportGui.yml");
	}

	public GuiControl gamemodeGui() {
		return loadGui("GamemodeGui.yml");
	}

	public GuiControl loadGui(String path) {
		file = new DataFile("/GuiConfigurations/" + path);
		new GuiGenerator(file).run();
		console("loaded Gui file: " + path);
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

	public DataFile file() {
		return file;
	}

	public boolean processClick(User operator, Gui gui, ItemStack clickedItem) {
		ItemStack header = gui.getHeader();

		if (header != null) {
			User subject = new User(UUID.fromString(this.getTag(header, "uuid")));

			boolean equalsCanExecute = plugin.config.getBoolean("EqualsCanExecute");

			if (this.hasTag(clickedItem, "execute")) {
				if ((equalsCanExecute && (subject.rank() <= operator.rank())) || (subject.rank() < operator.rank())) {

					return new Executioner(subject, Exe.get(this.getTag(clickedItem, "execute"))).execute(operator);
				}

				new Message("RankTooLow").send(subject, operator);
			}

		}

		return false;
	}

}
