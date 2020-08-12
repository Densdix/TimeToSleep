package com.leknos.timetosleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private TextView timeToSleep;
    private TextView timeSleep;
    private ImageButton button;
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
        timeSleep.setText(timeWithNull(sleepHour)+":"+timeWithNull(sleepMinute));

        timeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sleepHour = hourOfDay;
                        sleepMinute = minute;
                        timeSleep.setText(timeWithNull(hourOfDay)+":"+timeWithNull(minute));
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
                    button.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_stop));
                    //button.setText("STOP");
                }else {
                    countDownTimer.cancel();
                    isTimerRunning = false;
                    button.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_start));
                    //button.setText("RUN");
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

        int sleepDate = date;

        //calc current time in millis
        long currentMilliseconds = dateAndTime.getTimeInMillis();
        Log.d("DIMA", "currentMilliseconds: "+currentMilliseconds);

//        long timeZone = dateAndTime.getTimeZone().getOffset(currentMilliseconds);
//        Log.d("DIMA", "timeZone: "+timeZone);
//
//        long currentLocalMilliseconds = currentMilliseconds + timeZone;
//        Log.d("DIMA", "currentLocalMilliseconds: "+currentLocalMilliseconds);

        if(sleepHour <= hour){
            if(sleepMinute <= minute){
                sleepDate++;
            }
        }

        Log.d("DIMA", "date: "+date);
        Log.d("DIMA", "sleepDate: "+sleepDate);

        dateAndTime.set(year, month, sleepDate, sleepHour, sleepMinute, 0);

        long sleepMilliseconds = dateAndTime.getTimeInMillis();
        Log.d("DIMA", "sleepMilliseconds: "+sleepMilliseconds);

        long timeToSleep = sleepMilliseconds - currentMilliseconds;
        Log.d("DIMA", "timeToSleep: "+timeToSleep);

        return timeToSleep;
    }

    public void downTimerStart(long millisTime){
        countDownTimer = new CountDownTimer(millisTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);

                timeToSleep.setText(timeWithNull(hours) +
                        ":" + timeWithNull(minutes) +
                        ":" + timeWithNull(seconds));
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                isTimerRunning = false;
                //button.setText("RUN");
                button.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_start));
                timeToSleep.setText("00:00:00");
                Intent intentVibrate = new Intent(getApplicationContext(),VibrateService.class);
                startService(intentVibrate);
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