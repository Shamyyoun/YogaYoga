package com.socyle.yogayoga.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {
	public static String convertTime(String pattern, long milliSeconds) {
		if (pattern.equalsIgnoreCase("HH:mm:ss")) {
			String seconds = "" + (int) (milliSeconds / 1000) % 60;
			String minutes = "" + (int) ((milliSeconds / (1000 * 60)) % 60);
			String hours = "" + (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
			
			// foramt time
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
			
			return hours + ":" + minutes + ":" + seconds;
		} else {
			DateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			return formatter.format(calendar.getTime());
		}
	}
}
