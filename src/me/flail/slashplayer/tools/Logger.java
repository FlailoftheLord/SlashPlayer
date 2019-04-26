package me.flail.slashplayer.tools;

public class Logger extends StringUtils {

	public void console(String string) {
		plugin.server.getConsoleSender().sendMessage(chat("[" + plugin.getDescription().getPrefix() + "] " + string));
	}

	public Logger nl() {
		plugin.server.getConsoleSender().sendMessage("");
		return this;
	}

}
