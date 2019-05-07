package me.flail.slashplayer.sp;

import me.flail.slashplayer.tools.DataFile;

public class Messages extends FileManager {
	private DataFile file = new DataFile("Messages.yml");
	String prefix = chat(plugin.getConfig().getString("Prefix"));

	public String get(String key) {
		return file.getValue(key).replace("%prefix%", prefix);
	}

}
