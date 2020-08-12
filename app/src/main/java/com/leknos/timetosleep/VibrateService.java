package com.leknos.timetosleep;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;


public class VibrateService extends IntentService {

    public VibrateService() {
        super("VibrateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
