package com.enderpay.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;

public class DateTimeHelper {

    public static final String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static String timeUntil(String iso8601) {

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(iso8601);

        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(zonedDateTime);

    }

}
