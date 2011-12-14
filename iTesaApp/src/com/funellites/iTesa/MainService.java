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

public class MainService extends Service {
    public final static String TAG = "iTesa";
    public static long instance = 0;
    private Magnetometer magnetometer;
    private Queue<DataMagnetometer> dataQueue = null;
    private DBAdapter dbAdapter;
    private Graph     graph;

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
        magnetometer = new Magnetometer(this);
        dataQueue    = new LinkedList<DataMagnetometer>();
        dbAdapter    = new DBAdapter(this);
        dbAdapter.open();
        graph = new Graph( dbAdapter );
        makeThread();
        showNotification();
    }

    @Override
    public void onDestroy() 
    {
        RUNNING = false;
        //threadSaveDataMag.threadRunning  = false;
        threadSaveDataTel.threadRunning  = false;
        threadUpdateBitmap.threadRunning  = false;
        //try { threadSaveDataMag.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        try { threadSaveDataTel.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        try { threadUpdateBitmap.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        
        if ( dbAdapter.isOpen ) 
        {
            dbAdapter.close();
        }
        magnetometer.stop();
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
    
    /*** Thread functionality - saving data ***/
    //private ThreadSaveDataMag threadSaveDataMag; // save magnetometer data
    private ThreadSaveTelemetry threadSaveDataTel; //save telemetry data
    private ThreadUpdateBitmap threadUpdateBitmap;

    /** Creates the thread (this method is invoked from onCreate()) */
    private void makeThread() {
       //threadSaveDataMag = new ThreadSaveDataMag();
       threadSaveDataTel = new ThreadSaveTelemetry();
       threadUpdateBitmap = new ThreadUpdateBitmap();
       //threadSaveDataMag.start();
       threadSaveDataTel.start();
       threadUpdateBitmap.start();
    }

    /** Thread class for saving magnetic data to the sqlite db */
    class ThreadSaveDataMag extends Thread 
    {
       public boolean threadRunning = true;
       private long status = 0;

       public ThreadSaveDataMag() {}

       @Override
       public void run() 
       {
          Log.d("iTesa", "Run: ThreadSaveDataMag()");
          while (threadRunning) 
          {
              //Log.d("iTesa", "elements in queue:" + dataQueue.size());
              //if( !dataQueue.isEmpty() )
        	  if ( magnetometer.dataMag != null )
              {
                  Log.d(TAG, "Storing magnetic data");

            	  status = dbAdapter.insertDataMagnetometer( magnetometer.dataMag );
         	      if ( -1 == status )
         	          Log.d("iTesa", "db.insert failed");            	 

              }
              try { Thread.sleep(2000); } catch(Exception e) { e.printStackTrace(); }
        	  //Log.d("iTesa", "db.insert status :" + status);
          }
       }
    }

    /** Thread class for saving position data to the sqlite db */
    class ThreadSaveTelemetry extends Thread 
    {
        public boolean threadRunning = true;
        private long status = 0;

        public ThreadSaveTelemetry() {}

        @Override
        public void run() 
        {
        	Log.d("iTesa", "Run: ThreadSaveDataTel()");
            while (threadRunning) 
            {
                try { Thread.sleep(2000); } catch(Exception e) { e.printStackTrace(); }

            	Log.d(TAG, "Storing simulated position data + sensor data");
            	/* TODO: add code to fetch data from the telemetry file */
                DataTelemetry dataTelemetry = new DataTelemetry( magnetometer.dataMag.t , magnetometer.n );
                DataMagnetometer dataMagnetometer = magnetometer.dataMag;

                status = dbAdapter.insertDataTelemetry(dataTelemetry, dataMagnetometer);
                if ( -1 == status )
         	        Log.d("iTesa", "db.insert failed");
            }
        }
    }
    
    /** Thread class for updating the bitmap */
    class ThreadUpdateBitmap extends Thread 
    {
       public boolean threadRunning = true;
       public ThreadUpdateBitmap() {}
       @Override
       public void run() 
       {
            Log.d("iTesa", "Run: ThreadUpdateBitmap()");
            while (threadRunning) 
            {
                try { Thread.sleep(30000); } catch(Exception e) { e.printStackTrace(); }
            	Log.d(TAG, "Creating bitmap");
                graph.createBitmap();
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
    
}