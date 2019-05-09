package me.flail.slashplayer.sp.gui;

import me.flail.slashplayer.gui.Gui;
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

	public Gui get() {
		return this;
	}

	public DataFile getGuiFile() {
		return file;
	}

	public void openModerationGui(User subject) {
		this.setSubject(user);
		this.open();
	}

}
