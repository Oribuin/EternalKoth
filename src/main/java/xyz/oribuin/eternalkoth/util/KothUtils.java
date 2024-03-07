package xyz.oribuin.eternalkoth.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class KothUtils {

    public KothUtils() {
        throw new IllegalStateException("This is a utility class");
    }

    /**
     * Format Milliseconds into xh xm xs time format
     *
     * @param milliseconds Milliseconds
     * @return String in format xh xm xs format
     */
    public static String convertMillis(long milliseconds) {
        if ((milliseconds / 1000) < 1)
            return "0s";

        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1);

        StringBuilder sb = new StringBuilder();
        if (hours > 0)
            sb.append(hours).append("h ");

        if (minutes > 0)
            sb.append(minutes).append("m ");

        if (seconds > 0)
            sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Convert a duration to a time string
     *
     * @param text The duration string
     * @return The duration
     */
    public static Duration parseDuration(String text) {
        if (text.length() < 2)
            return Duration.ZERO;

        char lastChar = text.charAt(text.length() - 1);
        String num = text.substring(0, text.length() - 1);

        long amount;
        try {
            amount = Long.parseLong(num);
        } catch (NumberFormatException e) {
            return Duration.ZERO;
        }

        return switch (lastChar) {
            case 'd' -> Duration.ofDays(amount);
            case 'h' -> Duration.ofHours(amount);
            case 'm' -> Duration.ofMinutes(amount);
            case 's' -> Duration.ofSeconds(amount);
            default -> Duration.ZERO;
        };
    }

}
