package com.leknos.timetosleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private TextView timeToSleep;
    private TextView timeSleep;
    private Button button;
    private Calendar dateAndTime = Calendar.getInstance();
    private SharedPreferences sharedPreferences;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    public static final String SLEEP_HOUR_TIME = "hour_time";
    public static final String SLEEP_MINUTE_TIME = "minute_time";

    private int sleepHour;
    private int sleepMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeToSleep = findViewById(R.id.time_to_sleep);
        timeSleep = findViewById(R.id.time_sleep);
        button = findViewById(R.id.button);
        sharedPreferences = getSharedPreferences("my_setting", MODE_PRIVATE);
        if(sharedPreferences.contains(SLEEP_HOUR_TIME) && sharedPreferences.contains(SLEEP_MINUTE_TIME)){
            sleepHour = sharedPreferences.getInt(SLEEP_HOUR_TIME, 23);
            sleepMinute = sharedPreferences.getInt(SLEEP_MINUTE_TIME, 15);
        }else{
            sleepHour = 23;
            sleepMinute = 15;
        }
        //default by first run
        timeSleep.setText(sleepHour+":"+sleepMinute);

        timeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sleepHour = hourOfDay;
                        sleepMinute = minute;
                        timeSleep.setText(hourOfDay+":"+minute);
                        Toast.makeText(MainActivity.this, "You successfully choose sleep time", Toast.LENGTH_SHORT).show();
                    }
                }, dateAndTime.get(Calendar.HOUR_OF_DAY), dateAndTime.get(Calendar.MINUTE), true).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTimerRunning){
                    downTimerStart(calcTimeToSleep());
                    isTimerRunning = true;
                    button.setText("STOP");
                }else {
                    countDownTimer.cancel();
                    isTimerRunning = false;
                    button.setText("RUN");
                }
            }
        });


    }

    public long calcTimeToSleep(){
        dateAndTime.setTime(new Date());
        int year = dateAndTime.get(Calendar.YEAR);
        int month = dateAndTime.get(Calendar.MONTH);
        int date = dateAndTime.get(Calendar.DATE);
        int hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateAndTime.get(Calendar.MINUTE);
        int second = dateAndTime.get(Calendar.SECOND);
        int sleepDate = hour;

        Timestamp currentMilliseconds = new Timestamp(year, month, date, hour, minute, second, 0);


        if(sleepHour < hour){
            if(sleepMinute < minute){
                sleepDate++;
            }
        }

        Timestamp sleepMilliseconds = new Timestamp(year, month, sleepDate, sleepHour, sleepMinute, 0, 0);

        long currentUTCTime = sleepMilliseconds.getTime() - currentMilliseconds.getTime();
        int TimeZoneTime = dateAndTime.getTimeZone().getOffset(currentUTCTime);

        Timestamp timeToSleepMilliseconds = new Timestamp(currentUTCTime - TimeZoneTime);
        return timeToSleepMilliseconds.getTime();

        //timeToSleep.setText(timeToSleepMilliseconds.getHours() + "|"+timeToSleepMilliseconds.getMinutes()+"|"+timeToSleepMilliseconds.getSeconds());

    }

    public void downTimerStart(long millisTime){
        countDownTimer = new CountDownTimer(millisTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Timestamp millisTimeLost = new Timestamp(millisUntilFinished);
                timeToSleep.setText(timeWithNull(millisTimeLost.getHours()) +
                        ":" + timeWithNull(millisTimeLost.getMinutes()) +
                        ":" + timeWithNull(millisTimeLost.getSeconds()));
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
            }
        };

        countDownTimer.start();
    }

    String timeWithNull(int number){
        if(number < 10){
            return "0"+number;
        }else{
            return String.valueOf(number);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SLEEP_HOUR_TIME, sleepHour);
        editor.putInt(SLEEP_MINUTE_TIME, sleepMinute);
        editor.apply();
    }
}