package com.funellites.iTesa;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements Magnetometer.Callback {

    protected TextView tBTextView;
    protected TextView xBTextView;
    protected TextView yBTextView;
    protected TextView zBTextView;
    protected TextView absBTextView;
    protected TextView maxBTextView;
    protected TextView iTextView;

    float xB,yB,zB; // TODO: move to DataItem constructor
    long t = 0;
    float absB = 0;
    float maxB = 0;

    DataItem B = new DataItem(t, xB, yB, zB);
    
    Magnetometer magnetometer = null;
    GraphView graphView = null;
    
    // DBAdapter dbAdapter; TODO: Enable DB
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("iTesa", "MainActivity:onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Create/Open Database */
        /* TODO: Enable database
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        */

        tBTextView = (TextView) findViewById(R.id.tB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        maxBTextView = (TextView) findViewById(R.id.maxB);
        iTextView = (TextView) findViewById(R.id.iB);
        graphView = (GraphView)this.findViewById(R.id.XYPlot);

        magnetometer = new Magnetometer( this , this );

        //start a thread to refresh UI
        makeThread();

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        		              WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        		              WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		              WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
    	Log.d("iTesa", "MainActivity:onStop()");
    	super.onStop();

        // TODO: check that it actually works
    	guiThread.stop();

    	// Close the database
        /* TODO: Enable database
        dbAdapter.close();
        */
    }
    
    private long tmpBt = 0;
    
	private void updateGUI() {
        String str = "t: " + B.t + " ns";
        tBTextView.setText(str);
        str = "x: " + B.x + " µT";
        xBTextView.setText(str);
        str = "y: " + B.y + " µT";
        yBTextView.setText(str);
        str = "z: " + B.z + " µT";
        zBTextView.setText(str);
        str = "abs: " + absB + " µT";
        absBTextView.setText(str);
        str = "max: " + maxB + " µT";
        maxBTextView.setText(str);
        if ( magnetometer.i != 0 ) {
            //Log.w("iTesa", "t=" + ((B.t - tmpBt)/1000000)/magnetometer.i);
            str = "smpl.rate: " + ((B.t - tmpBt)/1000000)/magnetometer.i + " ms";
            iTextView.setText(str);
        }

        tBTextView.invalidate();
        xBTextView.invalidate();
        yBTextView.invalidate();
        zBTextView.invalidate();
        absBTextView.invalidate();
        maxBTextView.invalidate();
        iTextView.invalidate();

        tmpBt = B.t;
        magnetometer.i = 0;
    	      
        /* store data in sqlite db */
        /* TODO: Enable database
        dbAdapter.insertData(B); // returns long row
        */
	}

	private void updateGraph() {
	    graphView.x = (int)(B.x);
	    graphView.y = (int)(B.y);
	    graphView.z = (int)(B.z);
	    graphView.invalidate();
	}
	
	@Override
	public void updateData(long time, float x, float y, float z) {
		B.t = time; // timestamp;
	    B.x = x;    // lateral
	    B.y = y;    // longitudinal
	    B.z = z;    // vertical
	    absB = Math.round( Math.sqrt(B.x*B.x+B.y*B.y+B.z*B.z) ); 
	    if (absB > maxB)
            maxB = absB;

	    /* If we still use the Callback, then here would be the place to add data to queue.
	     * Read the elements from queue for plot in other thread. Does it make sense ?   
	     * http://developer.android.com/reference/java/util/Queue.html
	     */
	    
    }

	private GuiThread guiThread;
	
    private void makeThread() {
        Log.d("iTesa", "makeThread()");
        guiThread = new GuiThread();
        guiThread.start();
    }
	
    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {
	   
        @Override
        public void handleMessage(Message msg) {  
            switch(msg.what) {
	        //handle update
	        case 1:
                updateGUI();
	        case 2:
	        	updateGraph();
	        break;
            }
        }   
    };

    class GuiThread extends Thread {
        
        public GuiThread() {
        }
        
        @Override
        public void run() {
            while (true) {
              //Send update to the main thread
              messageHandler.sendMessage(Message.obtain(messageHandler, 1));
              messageHandler.sendMessage(Message.obtain(messageHandler, 2));
              
              try {
                 Thread.sleep(25);
              } catch(Exception e) {
                 e.printStackTrace();
              }
            }
        }
    }
}