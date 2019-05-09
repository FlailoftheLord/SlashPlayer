package me.flail.slashplayer.sp.gui;

import me.flail.slashplayer.gui_old.Gui;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.user.User;

/**
 * Use this to create new instances of a GUI
 * 
 * @author FlailoftheLord
 */
public class GuiControl extends Gui {
	private DataFile file;

	public GuiControl(User user) {
		super(user);
	}

	public GuiControl(User user, int size, String title) {
		super(user, size, title);
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

	protected GuiControl loadGui(String path) {
		file = new DataFile("/GuiConfigurations/" + path);
		return this;
	}

	/**
	 * @return the raw tier Gui for modification purposes.
	 */
	public Gui gui() {
		return this;
	}

	/**
	 * Get the Data file for this Gui
	 * 
	 * @return this Gui's {@link DataFile}
	 */
	public DataFile file() {
		return file;
	}

	public void openModerationGui(User operator, String type) {

		if (type.contains("gamemode")) {
			this.open("GamemodeGui.yml");
			return;
		}
		this.open("PlayerGui.yml");
	}

}
