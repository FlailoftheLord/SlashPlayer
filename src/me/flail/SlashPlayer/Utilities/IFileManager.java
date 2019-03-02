package me.flail.SlashPlayer.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.flail.SlashPlayer.SlashPlayer;

public class IFileManager extends FileManager {

	public IFileManager(SlashPlayer instance) {
		super(instance);
	}

	public void log(String msg) {
		BufferedWriter logs = null;

		Time time = new Time();
		Tools tools = new Tools();

		try {
			// create a temporary file
			String timeLog = time.monthName(Calendar.MONTH) + " "
					+ new SimpleDateFormat("dd_yyyy").format(Calendar.getInstance().getTime()).toString();

			boolean createFile = new File(plugin.getDataFolder() + "/logs").mkdirs();

			if (createFile || (createFile == false)) {

				File logFile = new File(plugin.getDataFolder() + "/logs/" + timeLog + ".txt");

				logs = new BufferedWriter(new FileWriter(logFile, true));
				logs.newLine();
				logs.write(time.currentDayTime() + " " + tools.m(msg));

				// console.sendMessage("Logging worked!");

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				logs.close();

				// console.sendMessage("logs closed");

			} catch (Exception e) {
			}

		}

	}

}
