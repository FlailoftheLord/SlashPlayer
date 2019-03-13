package me.flail.SlashPlayer.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Time {

	public String monthName(int month) {
		switch (month) {

		case 01:
			return "January";
		case 02:
			return "Febuary";
		case 03:
			return "March";
		case 04:
			return "April";
		case 05:
			return "May";
		case 06:
			return "June";
		case 07:
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

		String time = new SimpleDateFormat("MMM dd, yyyy  (HH:mm.ss)").format(Calendar.getInstance().getTime());

		return time;

	}

	public String currentTime(boolean formatted) {
		Date date = Calendar.getInstance().getTime();

		int timeS = Integer.parseInt(new SimpleDateFormat("ss").format(date));
		int timeM = Integer.parseInt(new SimpleDateFormat("mm").format(date));
		int timeH = Integer.parseInt(new SimpleDateFormat("HH").format(date));
		int timeD = Integer.parseInt(new SimpleDateFormat("dd").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));

		return this.format(timeS, timeM, timeH, timeD, month, year, formatted);
	}

	public String fromSeconds(int value, boolean format) {
		if (value >= 60) {  // minutes
			if (value >= 3600) {  // hours
				if (value >= 86400) {  // days
					if (value >= 2592000) {  // months
						if (value >= 31104000) {  // years
							return (value / 31104000) + "Y, " + (value % 2592000) + "M ," + (value % 86400) + "d " + (value % 3600)
									+ "h " + (value % 60) + "m "
							+ (value % 31104000) + "s";
						}
						return (value / 2592000) + "M ," + (value % 86400) + "d " + (value % 3600) + "h " + (value % 60) + "m "
						+ (value % 2592000) + "s";
					}
					return (value / 86400) + "d " + (value % 3600) + "h " + (value % 60) + "m " + (value % 86400) + "s";
				}
				return (value / 3600) + "h " + (value % 60) + "m " + (value % 3600) + "s";
			}
			return (value / 60) + "m " + (value % 60) + "s";
		}
		return value + "s";
	}

	public int subtractDates(String initial, String subtractor, boolean isFormatted) {
		if (isFormatted) {
			int unit = 0;
			Map<String, Integer> initDate = this.rawDate(initial);
			Map<String, Integer> subDate = this.rawDate(subtractor);
			for (String value : initDate.keySet()) {
				unit += this.toSeconds(value, initDate.get(value).intValue() - subDate.get(value).intValue());
			}

			return unit;
		} else {
			int initDate = this.trimDate(initial);
			int subDate = this.trimDate(subtractor);

			return initDate - subDate;
		}

	}

	public Map<String, Integer> rawDate(String date) {
		Map<String, Integer> timestamp = new HashMap<>();
		String[] time = date.split("-");
		for (String s : time) {
			String unit = s.split(":")[0].toLowerCase();
			switch (unit) {
			case "second":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
				break;
			case "minute":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
				break;
			case "hour":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
				break;
			case "day":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
				break;
			case "month":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
				break;
			case "year":
				timestamp.put(unit, Integer.valueOf(Integer.parseInt(s.split(":")[1])));
			}

		}

		return timestamp;
	}

	public int toSeconds(String type, int value) {
		switch (type.toLowerCase()) {
		case "year":
			return value * 365 * 24 * 60 * 60;
		case "month":
			return value * 30 * 24 * 60 * 60;
		case "day":
			return value * 24 * 60 * 60;
		case "hour":
			return value * 60 * 60;
		case "minute":
			return value * 60;
		default:
			return value;
		}
	}

	private int trimDate(String date) {
		return Integer
				.parseInt(date.replace("/", "").replace(" (", "").replace(")", "").replace("_", ""));

	}

	public String serverUptime() {

		long second = System.currentTimeMillis() / 1000;

		long minute = second / 60;

		long hour = minute / 60;

		long day = hour / 24;

		String time = "Days: " + day + " Hours: " + hour + " Minutes: " + minute + " Seconds: " + second;

		return time;

	}

	public String format(int s, int m, int h, int day, int month, int year, boolean format) {
		String time = month + "/" + day + "/" + year + " (" + h + "_" + m + "_" + s + ")";

		if (format) {
			time = "Year:" + year + "-Month:" + month + "-Day:" + day + "-Hour:" + h + "-Minute:" + m + "-Second:" + s;
		}

		return time;

	}

}
