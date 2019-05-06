package me.flail.slashplayer.sp;

import me.flail.slashplayer.tools.DataFile;

public class Messages extends FileManager {

	private static DataFile file = new DataFile("Messages.yml");

	public static String get(String key) {
		return file.getValue(key);
	}

}
