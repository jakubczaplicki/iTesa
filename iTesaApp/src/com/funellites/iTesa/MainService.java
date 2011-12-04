package com.funellites.iTesa;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service implements Magnetometer.Callback {
    public final static String TAG = "iTesa";
    public static long instance = 0;
    private Magnetometer magnetometer;
    private Queue<DataMagnetometer> dataQueue = null;
    private SMA sma;
    private DBAdapter dbAdapter;

    private boolean RUNNING = false;

    private NotificationManager nManager;
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        magnetometer = new Magnetometer(this, this);
        dataQueue    = new LinkedList<DataMagnetometer>();
        sma          = new SMA(100);
        dbAdapter    = new DBAdapter(this);
        dbAdapter.open();
        makeThread();
        showNotification();
    }

    @Override
    public void onDestroy() 
    {
        RUNNING = false;
        magnetometer.stop();
        
        threadSaveDataMag.threadRunning  = false;
        try { threadSaveDataMag.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        
        if ( dbAdapter.isOpen ) 
        {
            dbAdapter.close();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
        Log.i( TAG, "Received start id " + startId + ": " + intent);

        if (!RUNNING) {
        	magnetometer.start();
            RUNNING = true;
        }
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    public void addDataMagnetometer(long n, long t, float bx, float by, float bz)
    {
        //Log.i( TAG, "#n: " + n);
        DataMagnetometer dataItem = new DataMagnetometer(t, bx, by, bz);
        dataQueue.add(dataItem);
    }
    
    /*** Thread functionality - saving data ***/
    private ThreadSaveDataMag threadSaveDataMag;

    /** Creates the thread (this method is invoked from onCreate()) */
    private void makeThread() {
       Log.d("iTesa", "ThreadSaveDataMag()");
       threadSaveDataMag = new ThreadSaveDataMag();
       threadSaveDataMag.start();
    }

    /** Thread class for refreshing the UI */
    class ThreadSaveDataMag extends Thread 
    {
       public boolean threadRunning = true;
       private long status = 0;

       public ThreadSaveDataMag() {}

       @Override
       public void run() 
       {
          while (threadRunning) 
          {
             //Log.d("iTesa", "elements in queue:" + dataQueue.size());
             if( !dataQueue.isEmpty() )
             {
            	 //Log.d("iTesa", "saving elements to database");
            	 DataMagnetometer tmp = new DataMagnetometer();
            	 for (int i=0;  i < dataQueue.size(); i++)
            	 {
            		 tmp = dataQueue.remove();
                     sma.addData( tmp.abs );
            	 }
            	 tmp.abs = sma.getAvg();
            	 status = dbAdapter.insertDataMagnetometer( tmp );
         	     if ( -1 == status )
         	         Log.d("iTesa", "db.insert failed");            	 
            	 //Log.d("iTesa", "db.insert status :" + status);
            	 //Log.d("iTesa", "data saved in db");
             }

             try { Thread.sleep(1000); } catch(Exception e) { e.printStackTrace(); }
          }
       }
    }
     


    /*** Show notification after starting the service ***/

    private void showNotification()
    {
        Notification notification = new Notification(R.drawable.icon, "iTesa", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, "iTesa", "iTesa is now running in the background!", contentIntent);
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        nManager.notify(1, notification);
    }
    
    /*** Simple Moving Average ***/
    class SMA {
        private int   size; // TODO make this as an option/setting
        private float total = 0f;
        private int   index = 0;
        private float samples[];
        
        /** Construct and set Simple Moving Average */
        public SMA(int _size) {
           size = _size;
           samples = new float[size];
           for (int i = 0; i < size; i++) samples[i] = 0f;
        }

        /** Add data to average */
        public void addData(float x) 
        {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0;
        }
        
        public float getAvg() {
           return total / size;
         }
    }
}