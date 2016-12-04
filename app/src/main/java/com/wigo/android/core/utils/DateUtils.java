package com.wigo.android.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by AlexUA89 on 11/2/2016.
 */

public class DateUtils {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static String calendarToString(Calendar calendar) {
        return dateToString(calendar.getTime());
    }

    public static Calendar dateFromString(String dateString) throws ParseException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DATE_FORMAT.setTimeZone(tz);
        Calendar result = Calendar.getInstance();
        result.setTime(DATE_FORMAT.parse(dateString));
        return result;
    }

    public static String dateToString(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DATE_FORMAT.setTimeZone(tz);
        return DATE_FORMAT.format(date);
    }
}
