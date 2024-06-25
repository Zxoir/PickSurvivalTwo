package me.zxoir.picksurvivaltwo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static me.zxoir.picksurvivaltwo.util.CommonUtils.isLong;

/**
 * Provides utility methods for time conversion and formatting.
 */
public class TimeManager {
    private static final Pattern TIME_PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    private static final Map<String, Long> timeCache = new HashMap<>();
    private static final Map<Long, String> formattedTimeCache = new HashMap<>();
    private static final Map<Long, String> compactFormattedTimeCache = new HashMap<>();
    private static final StringBuilder formattedTime = new StringBuilder();

    /**
     * Converts a time duration string to milliseconds.
     *
     * @param input The time duration string (e.g., "5 minutes").
     * @return The equivalent duration in milliseconds, or -1 if the input is invalid.
     */
    @SuppressWarnings("unused")
    public static long toMillisecond(String input) {
        input = WHITESPACE_PATTERN.matcher(input).replaceAll("");

        if (timeCache.containsKey(input))
            return timeCache.get(input);

        String[] parts = TIME_PATTERN.split(input.toLowerCase());

        if (parts.length == 1)
            return isLong(input) ? Long.parseLong(input) * 1000 : -1;

        String unit;
        long value = 0;
        long milliseconds = 0;
        for (String part : parts) {
            if (isLong(part)) {
                value = Long.parseLong(part);
                continue;
            }

            unit = part;
            switch (unit) {
                case "second":
                case "seconds":
                case "s":
                    milliseconds += value * 1000;
                    break;
                case "minute":
                case "minutes":
                case "m":
                    milliseconds += value * 1000 * 60;
                    break;
                case "hour":
                case "hours":
                case "h":
                    milliseconds += value * 1000 * 60 * 60;
                    break;
                case "day":
                case "days":
                case "d":
                    milliseconds += value * 1000 * 60 * 60 * 24;
                    break;
                case "week":
                case "weeks":
                case "w":
                    milliseconds += value * 1000 * 60 * 60 * 24 * 7;
                    break;
                case "month":
                case "months":
                case "mo":
                    milliseconds += value * 1000 * 60 * 60 * 24 * 30;
                    break;
                default:
                    milliseconds = -1;
                    break;
            }

            if (milliseconds == -1)
                break;
        }


        timeCache.put(input, milliseconds);
        return milliseconds;
    }

    /**
     * Formats a time duration in milliseconds into a human-readable string.
     *
     * @param time     The time duration in milliseconds.
     * @param compact  Whether to format the time string without spaces.
     * @param withSecs Whether to include seconds in the formatted string.
     * @return The formatted time string.
     */
    public static String formatTime(long time, boolean compact, boolean withSecs) {
        Map<Long, String> cache = compact ? compactFormattedTimeCache : formattedTimeCache;

        if (cache.containsKey(time))
            return cache.get(time);

        formattedTime.setLength(0);

        long seconds = time / 1000;
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds % 3600);
        seconds %= 60;

        if (days > 0) {
            formattedTime.append(days).append(compact ? "d " : (days != 1 ? " days" : " day"));
            if (!compact && (hours > 0 || minutes > 0 || (withSecs && seconds > 0)) && !formattedTime.toString().endsWith(" ")) {
                formattedTime.append(" ");
            }
        }
        if (hours > 0) {
            formattedTime.append(hours).append(compact ? "h " : (hours != 1 ? " hours" : " hour"));
            if (!compact && (minutes > 0 || (withSecs && seconds > 0)) && !formattedTime.toString().endsWith(" ")) {
                formattedTime.append(" ");
            }
        }
        if (minutes > 0) {
            if ((days > 0 || hours > 0) && !formattedTime.isEmpty() && !compact) {
                formattedTime.append("and ");
            }
            formattedTime.append(minutes).append(compact ? "m " : (minutes != 1 ? " minutes" : " minute"));
            if (!compact && (withSecs && seconds > 0) && !formattedTime.toString().endsWith(" ")) {
                formattedTime.append(" ");
            }
        }
        if (withSecs && seconds > 0 && !(days > 0 && hours > 0) && (((days > 0 || hours > 0) && minutes <= 0) || (days <= 0 && hours <= 0))) {
            if (days > 0 || hours > 0 || minutes > 0) {
                formattedTime.append(compact ? "" : "and ");
            }
            formattedTime.append(seconds).append(compact ? "s" : (seconds != 1 ? " seconds" : " second"));
        }
        if (days <= 0 && hours <= 0 && minutes <= 0 && seconds <= 0)
            formattedTime.append(0).append(" seconds");

        String result = formattedTime.toString();
        cache.put(time, result);
        return result;
    }
}