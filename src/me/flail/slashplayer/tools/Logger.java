package me.flail.slashplayer.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.ChatColor;

/**
 * Basically, make all your classes extend this one.
 * Contains all the basic utilities all bundled neatly into one. (including an instance of the
 * plugin)
 * 
 * @author FlailoftheLord
 */
public class Logger extends CommonUtilities {

	public void console(String string) {
		plugin.server.getConsoleSender().sendMessage(chat("[" + plugin.getDescription().getPrefix() + "] " + string));
	}

	public Logger nl() {
		plugin.server.getConsoleSender().sendMessage("");
		return this;
	}

	public void log(String msg) throws IOException {
		BufferedWriter logWriter = null;

		Time time = new Time();


		// create a temporary file
		String timeLog = new SimpleDateFormat("MMM dd_yyyy").format(Calendar.getInstance().getTime()).toString();

		File logFile = new File(plugin.getDataFolder() + "/logs/" + timeLog + ".txt");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}

		try {
			logWriter = new BufferedWriter(new FileWriter(logFile, true));

			logWriter.newLine();
			logWriter.write(time.currentDayTime() + " " + ChatColor.stripColor(msg));
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {
				logWriter.close();

			} catch (Exception e) {
			}

		}

	}

}
