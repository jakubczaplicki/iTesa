package com.funellites.iTesa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

    private NotificationManager nManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Context context = getApplicationContext();
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        magnetometer = new Magnetometer(this, this);
        showNotification();
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
    
    private void showNotification() {
        Notification notification = new Notification(R.drawable.icon, "iTesa", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, "iTesa", "iTesa is now running in the background!", contentIntent);
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        nManager.notify(1, notification);
    }
}