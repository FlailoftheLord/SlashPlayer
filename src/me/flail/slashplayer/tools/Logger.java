package me.flail.slashplayer.tools;

public class Logger extends StringUtils {

	public void console(String string) {
		plugin.server.getConsoleSender().sendMessage(plugin.getDescription().getPrefix() + " " + chat(string));
	}

}
