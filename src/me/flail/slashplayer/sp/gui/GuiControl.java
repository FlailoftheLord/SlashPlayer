package me.flail.slashplayer.sp.gui;

import org.bukkit.inventory.ItemStack;

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

	public DataFile file() {
		return file;
	}

	public void processClick(User clicker, ItemStack clickedItem) {

	}

}
