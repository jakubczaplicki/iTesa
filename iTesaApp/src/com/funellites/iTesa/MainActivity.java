package com.funellites.iTesa;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

	SensorManager sensorManager = null;
	protected TextView tBTextView;
	protected TextView xBTextView;
	protected TextView yBTextView;
	protected TextView zBTextView;
	protected TextView absBTextView;
    protected TextView maxBTextView;
    TextView outMFN;
    float xB,yB,zB; // TODO: move to DataItem constructor
    long  t;

    DataItem B = new DataItem(t, xB, yB, zB);

    float absB = 0;
    float maxB = 0;
    DBAdapter dbAdapter;
    
    Timer updateTimer = new Timer("bUpdate");
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Create/Open Database */
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        
        tBTextView = (TextView) findViewById(R.id.tB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        maxBTextView = (TextView) findViewById(R.id.maxB);

        updateTimer.scheduleAtFixedRate(new TimerTask() {
        	public void run() {
        		updateGUI(); 
        		}
        	}, 0, 500);
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

        // Stop the timer - updateGUI() before closing database
        updateTimer.cancel();
        // Close the database
        dbAdapter.close();
    }
    
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
    	
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        public void onSensorChanged(SensorEvent event) {
        	synchronized (this) {
        		switch (event.sensor.getType()){ 
        		case Sensor.TYPE_MAGNETIC_FIELD:
	            	B.x = event.values[0]; //lateral
	            	B.y = event.values[1]; //longitudinal
	            	B.z = event.values[2]; //vertical
	            	B.t  = event.timestamp;
	            	absB = Math.round( Math.sqrt(B.x*B.x+B.y*B.y+B.z*B.z) ); 
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
    		  String str = "t: " + B.t;
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

    		  tBTextView.invalidate();
    		  xBTextView.invalidate();
    		  yBTextView.invalidate();
    		  zBTextView.invalidate();
    		  absBTextView.invalidate();
    		  maxBTextView.invalidate();
    		  
    	      /* add to Database */
    	      dbAdapter.insertData(B); // returns long row
    	  }
      });
	}	
}