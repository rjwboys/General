
package net.craftstars.general.util;

import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.craftstars.general.General;

public class Time {
	public enum TimeFormat {
		TWENTY_FOUR_HOUR, TWELVE_HOUR
	};
	
	public static TimeFormat currentFormat;
	
	public static void setup() {
		currentFormat = TimeFormat.TWELVE_HOUR;
		try {
			boolean in24hr = Option.TIME_FORMAT.get();
			if(in24hr) currentFormat = TimeFormat.TWENTY_FOUR_HOUR;
		} catch(Exception x) {
			General.logger.warn(LanguageText.LOG_CONFIG_BAD_TIME.value());
			x.printStackTrace();
		}
	}
	
	public static String formatTime(long time, TimeFormat fmt) {
		String suffix = "", formatString = "";
		long minutes = time % 1000, ticks = time;
		minutes = Math.round(minutes * 0.06);
		time /= 1000;
		time += 6;
		while(time > 24)
			time -= 24;
		switch(fmt) {
		case TWELVE_HOUR:
			if(time > 12) {
				time -= 12;
				suffix = (time == 12) ? "am" : "pm";
			} else if(time < 12) {
				suffix = "am";
			} else {
				suffix = "pm";
			}
			formatString = "%d";
		break;
		case TWENTY_FOUR_HOUR:
			if(time == 24) time = 0;
			suffix = "h";
			formatString = "%02d";
		break;
		}
		formatString += ":%02d%s";
		Formatter fmtr = new Formatter();
		if(Option.SHOW_TICKS.get())
			return fmtr.format(formatString + " (%d)", time, minutes, suffix, ticks).toString();
		else return fmtr.format(formatString, time, minutes, suffix).toString();
	}
	
	private static long twelveHourToTicks(long hour, boolean isPM) {
		if(isPM) hour += 12;
		if(hour == 12)
			hour = 0;
		else if(hour == 24) hour = 12;
		return twentyFourHourToTicks(hour);
	}
	
	private static long twentyFourHourToTicks(long hour) {
		hour += 18;
		while(hour >= 24)
			hour -= 24;
		return hour;
	}
	
	private static Pattern pat12hr = Pattern.compile("(0?[1-9]|1[0-2]):([0-5]?[0-9])([aApP][mM])");
	private static Pattern pat24hr = Pattern.compile("([01]?[0-9]|2[0-3]?):([0-5]?[0-9])[hH]?");
	private static Pattern patShort = Pattern.compile("([01]?[0-9]|2[0-3]?)([hH]|[aApP][mM])");
	
	public static long extractTime(String time) {
		Matcher m;
		boolean matched = false;
		long hour = 0, minutes = 0;
		// First try 24-hour
		m = pat24hr.matcher(time);
		if(m.matches()) {
			hour = twentyFourHourToTicks(Long.valueOf(m.group(1)));
			minutes = Long.valueOf(m.group(2));
			matched = true;
		}
		m = pat12hr.matcher(time);
		if(m.matches()) {
			String suffix = m.group(3);
			hour = twelveHourToTicks(Long.valueOf(m.group(1)), suffix.equalsIgnoreCase("pm"));
			minutes = Long.valueOf(m.group(2));
			matched = true;
		}
		if(!matched) {
			m = patShort.matcher(time);
			if(m.matches()) {
				String suffix = m.group(2);
				if(suffix.equalsIgnoreCase("h"))
					hour = twentyFourHourToTicks(Long.valueOf(m.group(1)));
				else hour = twelveHourToTicks(Long.valueOf(m.group(1)), suffix.equalsIgnoreCase("pm"));
			} else return Long.valueOf(time);
		}
		hour *= 1000;
		minutes = Math.round(minutes / 0.06);
		return hour + minutes;
	}
	
	public static String formatDuration(long time) {
		// TODO: Put this format in LanguageText
		long minutes = time % 1000, hours = time / 1000;
		minutes = Math.round(minutes * 0.06);
		Formatter fmtr = new Formatter();
		if(minutes + hours == 0) return fmtr.format("%d ticks", time).toString();
		if(minutes == 0) return fmtr.format("%d hours", hours).toString();
		if(hours == 0) return fmtr.format("%d minutes", minutes).toString();
		return fmtr.format("%d hours and %d minutes", hours, minutes).toString();
	}
	
	// private static Pattern patDuration = Pattern.compile("(\\d*[hH])(\\d*[mM])");
	private static Pattern patHour = Pattern.compile("(\\d+)[hH].*");
	private static Pattern patMin = Pattern.compile(".*?(\\d+)[mM]");
	
	public static long extractDuration(String time) {
		if(time.isEmpty()) return 0;
		Matcher m;
		boolean matched = false;
		long hours = 0, minutes = 0;
		m = patHour.matcher(time);
		if(m.matches()) {
			hours = Long.valueOf(m.group(1));
			matched = true;
		}
		m = patMin.matcher(time);
		if(m.matches()) {
			minutes = Long.valueOf(m.group(1));
			matched = true;
		}
		if(matched) return (hours * 1000) + Math.round(minutes / 0.06);
		return Long.valueOf(time);
	}
}
