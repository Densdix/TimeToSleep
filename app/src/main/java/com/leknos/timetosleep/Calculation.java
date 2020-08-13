package com.leknos.timetosleep;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Calculation {
    public static final String TAG = "MyCalculation";
    private Calendar dateAndTime = Calendar.getInstance();

    public long calcTimeToSleep(int sleepHour, int sleepMinute){
        dateAndTime.setTime(new Date());
        int year = dateAndTime.get(Calendar.YEAR);
        int month = dateAndTime.get(Calendar.MONTH);
        int date = dateAndTime.get(Calendar.DATE);
        int hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateAndTime.get(Calendar.MINUTE);

        int sleepDate = date;

        //calc current time in millis
        long currentMilliseconds = dateAndTime.getTimeInMillis();
        Log.d(TAG, "currentMilliseconds: "+currentMilliseconds);

        if(sleepHour <= hour){
            if(sleepMinute <= minute){
                sleepDate++;
            }
        }

        Log.d(TAG, "date: "+date);
        Log.d(TAG, "sleepDate: "+sleepDate);

        dateAndTime.set(year, month, sleepDate, sleepHour, sleepMinute, 0);

        long sleepMilliseconds = dateAndTime.getTimeInMillis();
        Log.d(TAG, "sleepMilliseconds: "+sleepMilliseconds);

        long timeToSleep = sleepMilliseconds - currentMilliseconds;
        Log.d(TAG, "timeToSleep: "+timeToSleep);

        return timeToSleep;
    }
}
