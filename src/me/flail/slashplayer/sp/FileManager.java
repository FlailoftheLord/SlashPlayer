package me.flail.slashplayer.sp;

import java.io.File;

import me.flail.slashplayer.tools.Logger;

public class FileManager extends Logger {

	public void setupGuiFiles(String[] fileNames) {
		for (String fileName : fileNames) {
			if (!fileName.endsWith(".yml")) {
				fileName = fileName.concat(".yml");
			}
			if (!fileName.startsWith("GuiConfigurations/")) {
				fileName = "GuiConfigurations/" + fileName;
			}

			File file = new File(plugin.getDataFolder() + "/" + fileName);

			if (plugin.getResource(fileName) != null) {
				if (!file.exists()) {
					plugin.saveResource(fileName, false);
				}
				continue;
			}

			console("&cFile couldn't be found: " + fileName);
		}

	}


}
