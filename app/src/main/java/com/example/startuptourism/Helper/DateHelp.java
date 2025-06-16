package com.example.startuptourism.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelp {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa");


    public static String convertToString(Date date, SimpleDateFormat sdf) {
        return sdf.format(date);
    }

    public static String convertToString(long date, SimpleDateFormat sdf) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return sdf.format(date);
    }

    public static Date convertToDate(String date, SimpleDateFormat sdf) throws ParseException {
        return sdf.parse(date);
    }


    public static int getCurrent(int field) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(field);
    }

    public static int getValueHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        if (calendar.get(Calendar.AM_PM) == Calendar.PM)
            hour += 12;
        return hour;
    }

    public static Date getCurrent() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.MONTH, getCurrent(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, getCurrent(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.YEAR, getCurrent(Calendar.YEAR));
        return calendar.getTime();
    }

    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date timePicker(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.HOUR, minute);
        return calendar.getTime();
    }

    public static int days(Date dateIn, Date dateOut) {

        Calendar calendarIn = Calendar.getInstance();
        calendarIn.setTime(dateIn);
        Calendar calendarOut = Calendar.getInstance();
        calendarOut.setTime(dateOut);
        return calendarOut.get(Calendar.DAY_OF_YEAR) - calendarIn.get(Calendar.DAY_OF_YEAR);
    }

    public static String convertToTime(int hour) {
        if (hour == 0)
            return "12:00 AM";
        else if (hour < 12)
            return hour + ":00 AM";
        else if (hour == 12)
            return hour + ":00 PM";
        else
            return (hour - 12) + ":00 PM";
    }
}
