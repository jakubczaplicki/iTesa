package com.funellites.itesa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LogService extends Service implements Magnetometer.Callback {
    public final static String TAG = "iTesa";
    public static long instance = 0;
    private Magnetometer magnetometer;
    private boolean RUNNING = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Context context = getApplicationContext();
        magnetometer = new Magnetometer(this, this);
    }

    @Override
    public void onDestroy() {
        RUNNING = false;
        magnetometer.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i( TAG, "Received start id " + startId + ": " + intent);

        if (!RUNNING) {
        	magnetometer.start();
            RUNNING = true;
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public void addData(long n, long t, float bx, float by, float bz){
        Log.i( TAG, "#n: " + n);
    }
    
}