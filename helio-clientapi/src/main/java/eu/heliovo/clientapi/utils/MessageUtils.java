package eu.heliovo.clientapi.utils;

public class MessageUtils {

	/**
	 * Format milliseconds as string, converting large values to seconds.
	 * @param time time in Milliseconds 
	 * @return user friendly string representation of the time.
	 */
	public static String formatSeconds(long time) {
		if (time > 999) {
			return String.format("%1$1.3fs", (double)time/1000.0);
		} 
		return String.format("%1$1dms", time);
	}

}
