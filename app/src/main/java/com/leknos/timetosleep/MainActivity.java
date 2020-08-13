package com.leknos.timetosleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import static com.leknos.timetosleep.Utils.timeWithNull;


public class MainActivity extends AppCompatActivity {
    private TextView timeToSleep;
    private TextView timeSleep;
    private ImageButton button;

    private SharedPreferences sharedPreferences;
    private SleepTimer sleepTimer;

    public static final String SLEEP_HOUR_TIME = "hour_time";
    public static final String SLEEP_MINUTE_TIME = "minute_time";
    public static final String TAG = "MyMainActivity";

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
        //init timer
        sleepTimer = new SleepTimer(this, sleepHour, sleepMinute);

        //default by first run
        timeSleep.setText(getString(R.string.sleep_time, timeWithNull(sleepHour), timeWithNull(sleepMinute)));

        timeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateAndTime = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sleepHour = hourOfDay;
                        sleepMinute = minute;
                        sleepTimer.setSleepHour(sleepHour);
                        sleepTimer.setSleepMinute(sleepMinute);
                        timeSleep.setText(getString(R.string.sleep_time, timeWithNull(sleepHour), timeWithNull(sleepMinute)));
                        Toast.makeText(MainActivity.this, "You successfully choose sleep time", Toast.LENGTH_SHORT).show();
                        if(sleepTimer.isTimerRunning()){
                            sleepTimer.reloadTimer();
                        }
                    }
                }, dateAndTime.get(Calendar.HOUR_OF_DAY), dateAndTime.get(Calendar.MINUTE), true).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepTimer.isTimerRunning()) {
                    sleepTimer.stopTimer();
                } else {
                    sleepTimer.startTimer();
                }
                changeButtonBackground(sleepTimer.isTimerRunning());
            }
        });

    }

    public void changeButtonBackground(boolean isTimerRunning){
        if(isTimerRunning){
            button.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_stop));
        }else{
            button.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_start));
        }
    }


    public void changeSleepTimerText(String text){
        timeToSleep.setText(text);
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