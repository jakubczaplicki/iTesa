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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Magnetometer.Callback {

	protected TextView tBTextView;
	protected TextView nBTextView;
	protected TextView xBTextView;
    protected TextView yBTextView;
    protected TextView zBTextView;
    protected TextView absBTextView;
    protected TextView avgBTextView;
    protected TextView maxBTextView;
    protected TextView iTextView;
    protected CheckBox logData_cb;
    AlertDialog alertDialog;

    DataItem B = new DataItem();
    Magnetometer magnetometer = null;
    GraphView graphView = null;
    
	static final private int MENU_PREFERENCES = Menu.FIRST; 	
	private static final int SHOW_PREFERENCES = 1;
	int updateFreq = 50;  // ms

	boolean logData = false;
    
	DBAdapter dbAdapter = null; // TODO: enable DB
	// CsvFileAdapter csvFile = null;

	/** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("iTesa",this.getClass().getName()+":onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        updateFromPreferences(); // load and set preferences

        dbAdapter = new DBAdapter(this); // new DB adapter
        //csvFile   = new CsvFileAdapter();

        tBTextView = (TextView) findViewById(R.id.tB);
        nBTextView = (TextView) findViewById(R.id.nB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        avgBTextView = (TextView) findViewById(R.id.avgB);
        maxBTextView = (TextView) findViewById(R.id.maxB);
        iTextView = (TextView) findViewById(R.id.iB);
        graphView = (GraphView)this.findViewById(R.id.XYPlot);
        logData_cb = (CheckBox) findViewById(R.id.logData_cb);

        logData_cb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dbAdapter.open();                // TODO: Enable database
            	
            	logData = !logData;
            	Toast.makeText(getBaseContext(), "Logging state changed", Toast.LENGTH_SHORT).show();
            }
        });
        
        magnetometer = new Magnetometer( this, B, this );

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

    	// stop the thread. Do not use unsafe depreciated guiThread.stop(); 
    	// see : http://stackoverflow.com/questions/4756862/how-to-stop-a-thread )
        guiThread.threadRunning  = false;
        try {
			guiThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	magnetometer.close(); // unregister magnetometer listener

        dbAdapter.close(); // close the database TODO: Enable database
    }

    private long tmpBt = 0;

    /** Updates the text fields on the UI. */	
	private void updateGUI() {
        String str = "t: " + B.t + " ns";
        tBTextView.setText(str);
        str = "n: " + B.n;
        nBTextView.setText(str);
        str = "x: " + B.x + " µT";
        xBTextView.setText(str);
        str = "y: " + B.y + " µT";
        yBTextView.setText(str);
        str = "z: " + B.z + " µT";
        zBTextView.setText(str);
        str = "abs: " + B.abs + " µT";
        absBTextView.setText(str);
        str = "avg(100): " + B.sma + " µT";
        avgBTextView.setText(str);
        str = "max: " + B.max + " µT";
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
        avgBTextView.invalidate();
        maxBTextView.invalidate();
        iTextView.invalidate();
	}

	/** Updates the graph on the UI. */	
	private void updateGraph() {
	    //graphView.x = (int)(B.x);
	    //graphView.y = (int)(B.y);
	    //graphView.z = (int)(B.z);
	    graphView.updateGraph( magnetometer.B.sma );
	}
	
	/** Store data in csv file */	
	private void storeDataCsvFile() {
		
	}
	
	/** Store data in sqlite database */	
	private void storeDataSqlite(){
		Toast.makeText(getBaseContext(), "storeDataSqlite()", Toast.LENGTH_SHORT).show();
		if (logData) {
            dbAdapter.insertData(B); // store data in sqlite db (returns long row) TODO: Enable database
        }
	}
	
	/**
	 * Used by callback from Magnetometer class.
	 * 
     * Add message to the main thread to save the data
	 */	
	@Override
	public void storeData() {
        //messageHandler.sendMessage(Message.obtain(messageHandler, 3));
        messageHandler.sendMessage(Message.obtain(messageHandler, 4));
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
	           case 1: updateGUI();        break;
	           case 2: updateGraph();      break;
	           case 3: storeDataCsvFile(); break;
	           case 4: storeDataSqlite();  break;
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
        //B.setSize( Integer.parseInt(prefs.getString(Preferences.PREF_UPDATE_AVGSIZE, "10")) );
    };
}