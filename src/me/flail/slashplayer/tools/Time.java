package me.flail.slashplayer.tools;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Time extends CommonUtilities {

	public static Date currentDate() {
		return Calendar.getInstance().getTime();
	}

	public static Instant currentInstant() {
		return currentDate().toInstant();
	}

	public static Instant finalBan(Instant initial, long duration) {
		return initial.plusSeconds(duration);
	}

	public static boolean isExpired(Date date, long duration) {
		return date.toInstant().plusSeconds(duration).isBefore(currentInstant());
	}

}
