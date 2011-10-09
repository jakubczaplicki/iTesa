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
    float absB = 0;
    float maxB = 0;
    float xB,yB,zB;
    long  t;
    // DBAdapter dbAdapter; // FIXME

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        Timer updateTimer = new Timer("bUpdate");
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
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
    	
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
    		  str = "x: " + xB + " µT";
    		  xBTextView.setText(str);
    		  str = "y: " + yB + " µT";
    		  yBTextView.setText(str);
    		  str = "z: " + zB + " µT";
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
    		  
    	      /* add to DB */
    	      // FIXME: shit ain't workin'
    		  /* DataItem newItem = new DataItem(xB, yB, zB, t);
    	      long row = DBAdapter.insertData(newItem); */
    	  }
      });
	}	
}