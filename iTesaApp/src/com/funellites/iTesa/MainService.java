package com.funellites.iTesa;

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
    //private Queue<DataMagnetometer> dataQueue = null;
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
    	Log.d(TAG,this.getClass().getName()+":onCreate()");
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        magnetometer = new Magnetometer(this);
        //dataQueue    = new LinkedList<DataMagnetometer>();
        dbAdapter    = new DBAdapter(this);
        dbAdapter.open();
        graph = new Graph( dbAdapter );
        makeThread();
        showNotification();
    }

    @Override
    public void onDestroy() 
    {
    	Log.d(TAG,this.getClass().getName()+":onDestroy()");
        RUNNING = false;
        threadSaveData.threadRunning  = false;
        threadUpdateBitmap.threadRunning  = false;
        try { threadSaveData.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        try { threadUpdateBitmap.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        
        if ( dbAdapter.isOpen ) 
        {
            dbAdapter.close();
        }
        magnetometer.stop();
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
        Log.d( TAG, "Received start id " + startId + ": " + intent);

        if (!RUNNING) {
        	magnetometer.start();
            RUNNING = true;
        }
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }
    
    /*** Thread functionality ***/
    private ThreadSaveData threadSaveData;
    private ThreadUpdateBitmap threadUpdateBitmap;

    /** Creates the thread - this method is invoked from onCreate() */
    private void makeThread() {
       threadSaveData     = new ThreadSaveData();
       threadUpdateBitmap = new ThreadUpdateBitmap();
       threadSaveData.start();
       threadUpdateBitmap.start();
    }

    /** Thread class for saving position data to the sqlite db */
    class ThreadSaveData extends Thread 
    {
        public boolean threadRunning = true;
        private long status = 0;

        public ThreadSaveData() {}

        @Override
        public void run() 
        {
        	Log.d("iTesa", "Run: ThreadSaveDataTel()");
            int n=1;
            int i=1;
            while (true) 
            {
            	if (!threadRunning)
            	{
        		    return;
                }

                try
                {
                    Thread.sleep(50);
                    n++;
                } catch(Exception e) { e.printStackTrace(); }

                if (n==20) //  20 * 50 ms = 1000 ms = 1 sec
                {
                	n=1;
            	    //Log.d(TAG, "Storing simulated position data + sensor data (sample: " + magnetometer.n + " )");
            	    /* TODO: add code to fetch data from the telemetry file */
            	    i=i+3;
                    DataTelemetry dataTelemetry = new DataTelemetry( magnetometer.dataMag.t , i );
                    DataMagnetometer dataMagnetometer = magnetometer.dataMag;

                    status = dbAdapter.insertDataTelemetry(dataTelemetry, dataMagnetometer);
                    if ( -1 == status )
         	            Log.d("iTesa", "db.insert failed");
                }
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
            int n=1;
            while (true) 
            {
        	    if (!threadRunning) 
        	    {
        	        return; 
        	    }

        	    try 
        	    {
        	        Thread.sleep(50);
        	        n++;
        	    } catch(Exception e) { e.printStackTrace(); }

                if (n==600) // 1200*50 ms = 60000 ms = 1 min
                {
                	n=1;
            	    Log.d(TAG, "Creating bitmap");
                    graph.createBitmap();
                    //Toast doesn't work inside this thread - something to do with loopers - no idea what
                    //Toast.makeText(getBaseContext(), "Creating bitmap", Toast.LENGTH_SHORT).show();
                }
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
