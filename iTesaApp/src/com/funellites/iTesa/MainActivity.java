package com.funellites.iTesa;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

	SensorManager sensorManager = null;
	protected TextView tBTextView;
	protected TextView xBTextView;
	protected TextView yBTextView;
	protected TextView zBTextView;
	protected TextView absBTextView;
    protected TextView maxBTextView;
    protected TextView debugTextView;
    float absB = 0;
    float maxB = 0;
    float xB,yB,zB;
    long  t;
    // DBAdapter dbAdapter; // FIXME

    /** Preferences */
	static final private int MENU_PREFERENCES = Menu.FIRST; 	
	private static final int SHOW_PREFERENCES = 1;
	int updateFreq = 500;  /* ms */
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Load and set preferences */
        updateFromPreferences();
        
        /* Open Database */
        /* FIXME:
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        */
        tBTextView = (TextView) findViewById(R.id.tB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        maxBTextView = (TextView) findViewById(R.id.maxB);
        debugTextView = (TextView) findViewById(R.id.debug);

        // FIXME : Timer seems to be wrong choice for UI updates.
        // Not only dynamic updates require destroying the timer object and
        // create a new one, but also apparently timers don't run when the
        // phone sleeps.
        Timer updateTimer = new Timer("bUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
        	public void run() {
        		updateGUI(); 
        		}
        	}, 0, updateFreq);        
    }
    
    @SuppressWarnings("static-access")
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		sensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    private final SensorEventListener sensorEventListener =	new SensorEventListener() {

    	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        public void onSensorChanged(SensorEvent event) {
        	synchronized (this) {
        		switch (event.sensor.getType()){ 
        		case Sensor.TYPE_MAGNETIC_FIELD:
	            	xB = event.values[0]; //lateral
	            	yB = event.values[1]; //longitudinal
	            	zB = event.values[2]; //vertical
	            	t  = event.timestamp;
	            	absB = Math.round( Math.sqrt(xB*xB+yB*yB+zB*zB) ); 
	            	if (absB > maxB)
	            		maxB = absB;
	            	break;
	            	}
        	}
        }
    };

	private void updateGUI() {
      runOnUiThread(new Runnable() {
    	  public void run() {
    		  String str = "t: " + t;
    		  tBTextView.setText(str);
    		  tBTextView.invalidate();
    		  
    		  str = "x: " + xB + " µT";
    		  xBTextView.setText(str);
    		  xBTextView.invalidate();
    		  
    		  str = "y: " + yB + " µT";
    		  yBTextView.setText(str);
    		  yBTextView.invalidate();
    		  
    		  str = "z: " + zB + " µT";
    		  zBTextView.setText(str);
    		  zBTextView.invalidate();
    		  
    		  str = "abs: " + absB + " µT";
    		  absBTextView.setText(str);
    		  absBTextView.invalidate();
    		  
    		  str = "max: " + maxB + " µT";
    		  maxBTextView.setText(str);
    		  maxBTextView.invalidate();
    		  
    		  str = "UI refresh rate: " + updateFreq;
    		  debugTextView.setText(str);
    		  debugTextView.invalidate();
    		  
    	      /* add to DB */
    	      // FIXME: shit ain't workin'
    		  /* DataItem newItem = new DataItem(xB, yB, zB, t);
    	      long row = DBAdapter.insertData(newItem); */
    	  }
      });
	};
	
	/* Menu */
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
	        	updateFromPreferences();
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