package me.flail.slashplayer.sp.gui;

import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.user.User;

public class GuiControl extends Gui {
	private DataFile file;

	public GuiControl(User user) {
		super(user);
	}

	public GuiControl playerList() {
		loadGui("PlayerListGui.yml");
		return this;
	}

	public GuiControl playerGui() {
		loadGui("PlayerGui.yml");
		return this;
	}

	public GuiControl reportGui() {
		loadGui("ReportGui.yml");
		return this;
	}

	public GuiControl gamemodeGui() {
		loadGui("GamemodeGui.yml");
		return this;
	}

	protected void loadGui(String path) {
		file = new DataFile("/GuiConfigurations/" + path);

	}

}
