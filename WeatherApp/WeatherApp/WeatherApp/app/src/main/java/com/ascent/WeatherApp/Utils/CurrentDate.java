package com.ascent.WeatherApp.Utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CurrentDate {

    public String currentDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formattime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd");
        String dtStart=format.format(Calendar.getInstance().getTime());
        Date date = null;

        try {
            date = format.parse(dtStart);
        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2013
        return dayOfTheWeek+", "+monthString+" "+day+", "+year;
    }

}
