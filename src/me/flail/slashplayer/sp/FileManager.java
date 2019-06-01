package me.flail.slashplayer.sp;

import me.flail.slashplayer.sp.gui.GuiControl;
import me.flail.slashplayer.tools.Logger;

public class FileManager extends Logger {

	public void setupGuiFiles(String[] fileNames) {
		for (String fileName : fileNames) {
			if (!fileName.endsWith(".yml")) {
				fileName = fileName.concat(".yml");
			}
			new GuiControl().loadGui(fileName, true, true);
		}

		new GuiControl().loadGui("RestoreInvGui.yml", false, false);
	}


}
