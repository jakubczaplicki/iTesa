/*
 * Copyright (C) 2011 The iTesa Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.funellites.iTesa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    
	static final private int MENU_PREFERENCES = Menu.FIRST; 	
	private static final int SHOW_PREFERENCES = 1;
	int updateFreq = 50;  // ms
    
    // DBAdapter dbAdapter; // TODO: enable DB
	
	/** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("iTesa", "MainActivity:onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        updateFromPreferences(); // load and set preferences

        // dbAdapter = new DBAdapter(this); // create/open Database
        // dbAdapter.open();                // TODO: Enable database

        tBTextView = (TextView) findViewById(R.id.tB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        maxBTextView = (TextView) findViewById(R.id.maxB);
        iTextView = (TextView) findViewById(R.id.iB);
        graphView = (GraphView)this.findViewById(R.id.XYPlot);

        magnetometer = new Magnetometer( this , this );

        makeThread(); //start a thread to refresh UI

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        		              WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        		              WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		              WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("iTesa", "MainActivity:onResume()");
        
        updateFromPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("iTesa", "MainActivity:onPause()");
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	Log.d("iTesa", "MainActivity:onStop()");
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Log.d("iTesa", "MainActivity:onDestroy()");

    	// stop the thread. Do not use unsafe deprecated guiThread.stop(); 
    	// see : http://stackoverflow.com/questions/4756862/how-to-stop-a-thread )
        guiThread.threadRunning  = false;
        try {
			guiThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	magnetometer.close(); // unregister magnetometer listener

        // dbAdapter.close(); // close the database TODO: Enable database
    }
    
    private long tmpBt = 0;

    /** Updates the text fields on the UI. */	
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
            // I don't understand this number !! why is it ~50 ms ?
        	// see http://stackoverflow.com/questions/5060628/android-sensor-delay-fastest-isnt-fast-enough
        	str = "smpl.rate: " + ((B.t - tmpBt)/1000000)/magnetometer.i + " ms";
            tmpBt = B.t;
            magnetometer.i = 0;
        	// why is this number ~ 0 ms ?
        	// str = "smpl.rate: " + magnetometer.delay + " ms";
            iTextView.setText(str);
        }

        tBTextView.invalidate();
        xBTextView.invalidate();
        yBTextView.invalidate();
        zBTextView.invalidate();
        absBTextView.invalidate();
        maxBTextView.invalidate();
        iTextView.invalidate();
    	      
        // dbAdapter.insertData(B); // store data in sqlite db (returns long row) TODO: Enable database
	}

	/** Updates the graph on the UI. */	
	private void updateGraph() {
	    graphView.x = (int)(B.x);
	    graphView.y = (int)(B.y);
	    graphView.z = (int)(B.z);
	    graphView.invalidate();
	}
	
	/**
	 * Used by callback from Magnetometer class.
	 * 
     * If we still use the Callback, then here would be the place to add data to queue.
	 * Read the elements from queue for plot in other thread. Does it make sense ?   
	 * http://developer.android.com/reference/java/util/Queue.html
	 */	
	@Override
	public void updateData(long time, float x, float y, float z) {
		B.t = time; // timestamp;
	    B.x = x;    // lateral
	    B.y = y;    // longitudinal
	    B.z = z;    // vertical
	    absB = Math.round( Math.sqrt(B.x*B.x+B.y*B.y+B.z*B.z) ); 
	    if (absB > maxB)
            maxB = absB;
    }

	
	
    /*************************************************************************
	 * Main GUI thread functionality
	 *************************************************************************/	
	private GuiThread guiThread;
	
	/** Creates the thread (this method is invoked from onCreate()) */
	private void makeThread() {
        Log.d("iTesa", "makeThread()");
        guiThread = new GuiThread();
        guiThread.start();
    }
	
	/** Instantiating the Handler associated with the main thread */
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

    /** Thread class for refreshing the UI */
    class GuiThread extends Thread {
        
        public boolean threadRunning = true;

		public GuiThread() {
        }
        
        @Override
        public void run() {
            while (threadRunning) {
              //Send update to the main thread
              messageHandler.sendMessage(Message.obtain(messageHandler, 1));
              messageHandler.sendMessage(Message.obtain(messageHandler, 2));
              
              try {
                 Thread.sleep(updateFreq);
              } catch(Exception e) {
                 e.printStackTrace();
              }
            }
        }
    }

    /*************************************************************************
	 * Menu and preferences functionality
	 *************************************************************************/	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   super.onCreateOptionsMenu(menu);
	   menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
	   return true;
	 };

    public boolean onOptionsItemSelected(MenuItem item) {
	    super.onOptionsItemSelected(item);

	    switch (item.getItemId()) {
	        case (MENU_PREFERENCES): {
	        	Intent i = new Intent(this, Preferences.class);
	        	startActivityForResult(i, SHOW_PREFERENCES);
	        	return true;
            }
        }
        return false;
    };

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateFreq = Integer.parseInt(prefs.getString(Preferences.PREF_UPDATE_FREQ, "1000"));
    };
}