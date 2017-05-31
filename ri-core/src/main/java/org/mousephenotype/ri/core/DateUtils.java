package org.mousephenotype.ri.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrelac on 22/05/2017.
 */
public class DateUtils {

    /**
     * Given two dates (in any order), returns a <code>String</code> in the
     * format "xxx days, yyy hours, zzz minutes, nnn seconds" that equals
     * the absolute value of the time difference between the two days.
     * @param date1 the first operand
     * @param date2 the second operand
     * @return a <code>String</code> in the format "dd:hh:mm:ss" that equals the
     * absolute value of the time difference between the two date.
     */
    public String formatDateDifference(Date date1, Date date2) {
        long lower = Math.min(date1.getTime(), date2.getTime());
        long upper = Math.max(date1.getTime(), date2.getTime());
        long diff = upper - lower;

        long days = diff / (24 * 60 * 60 * 1000);
        long hours = diff / (60 * 60 * 1000) % 24;
        long minutes = diff / (60 * 1000) % 60;
        long seconds = diff / 1000 % 60;

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * Return the string representation of the specified <code>milliseconds</code>
     * in hh:mm:ss format. NOTE: year, month, and day do not participate in the
     * computation. If milliseconds is longer than 24 hours, incorrect results
     * will be returned.
     *
     * @param milliseconds
     * @return
     */
    public String msToHms(Long milliseconds) {
        String result = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        return result;
    }
}