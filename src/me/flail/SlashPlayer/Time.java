package me.flail.SlashPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.configuration.file.FileConfiguration;

public class Time {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	public String monthName(int month) {
		switch (month - 1) {

		case 1:
			return "January";
		case 2:
			return "Febuary";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		default:
			return month + "";

		}
	}

	public String currentDayTime() {

		String time = new SimpleDateFormat("(HH:mm:ss)").format(Calendar.getInstance().getTime());

		return time;

	}

	public void time() {

		FileConfiguration pData = plugin.getPlayerData();

		SimpleDateFormat timeM = new SimpleDateFormat("mm");
		SimpleDateFormat timeH = new SimpleDateFormat("HH");
		SimpleDateFormat timeD = new SimpleDateFormat("dd");
		SimpleDateFormat month = new SimpleDateFormat("MM");
		SimpleDateFormat year = new SimpleDateFormat("yyyy");

		pData.set("RealTime.Year", year);
		pData.set("RealTime.Month", month);
		pData.set("RealTime.Day", timeD);
		pData.set("RealTime.Hour", timeH);
		pData.set("RealTime.Minute", timeM);

	}

	public String serverTime() {

		long second = System.currentTimeMillis() / 1000;

		long minute = second / 60;

		long hour = minute / 60;

		long day = hour / 24;

		String time = "Day: " + day + " Hour: " + hour + " Minute: " + minute + " Second: " + second;

		return time;

	}

}
