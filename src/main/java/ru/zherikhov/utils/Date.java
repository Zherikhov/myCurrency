package ru.zherikhov.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Date {
    public static String getSourceDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(new java.util.Date());
    }

    public static int compareTime(String time) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        java.util.Date sourceTime = formatter.parse(formatter.format(new java.util.Date()));

        return formatter.parse(time).compareTo(sourceTime);
    }
}
