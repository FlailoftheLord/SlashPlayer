package me.flail.slashplayer.sp;

import java.util.Map;

import me.flail.slashplayer.tools.DataFile;

public class Message extends FileManager {
	private DataFile file;
	private String prefix;
	private String message;

	public Message(String key) {
		file = new DataFile("Messages.yml");
		prefix = chat(plugin.getConfig().getString("Prefix"));
		message = file.getValue(key);
	}

	public DataFile getFile() {
		return file;
	}

	public String get() {
		return message.replace("%prefix%", msgPrefix());
	}

	public String msgPrefix() {
		return prefix;
	}

	public String placeholders(Map<String, String> placeholders) {
		message = get();
		for (String p : placeholders.keySet()) {
			message = message.replace(p, placeholders.get(p));
		}
		return placeholders != null ? message : get();
	}
}

