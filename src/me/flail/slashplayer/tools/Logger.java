package me.flail.slashplayer.tools;

/**
 * Basically, make all your classes extend this one.
 * Contains all the basic utilities all bundled neatly into one. (including an instance of the
 * plugin)
 * 
 * @author FlailoftheLord
 */
public class Logger extends StringUtils {

	public void console(String string) {
		plugin.server.getConsoleSender().sendMessage(chat("[" + plugin.getDescription().getPrefix() + "] " + string));
	}

	public Logger nl() {
		plugin.server.getConsoleSender().sendMessage("");
		return this;
	}

}
