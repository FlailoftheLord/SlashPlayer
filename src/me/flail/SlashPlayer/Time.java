package me.flail.SlashPlayer;

import java.text.SimpleDateFormat;

import org.bukkit.configuration.file.FileConfiguration;

public class Time {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

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
