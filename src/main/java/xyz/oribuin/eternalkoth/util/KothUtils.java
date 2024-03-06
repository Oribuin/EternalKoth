package xyz.oribuin.eternalkoth.util;

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

        return sb.toString();
    }

    /**
     * Parse a time string into milliseconds
     *
     * @param time The time string
     * @return The time in milliseconds
     */
    public static long parseTime(String time) {
        String[] parts = time.split(" ");
        long totalSeconds = 0;

        for (String part : parts) {

            // get the last character
            char lastChar = part.charAt(part.length() - 1);
            String num = part.substring(0, part.length() - 1);
            if (num.isEmpty())
                continue;

            int amount;
            try {
                amount = Integer.parseInt(num);
            } catch (NumberFormatException e) {
                continue;
            }

            switch (lastChar) {
                case 'w' -> totalSeconds += amount * 604800L;
                case 'd' -> totalSeconds += amount * 86400L;
                case 'h' -> totalSeconds += amount * 3600L;
                case 'm' -> totalSeconds += amount * 60L;
                case 's' -> totalSeconds += amount;
            }
        }

        return totalSeconds * 1000;
    }

}
