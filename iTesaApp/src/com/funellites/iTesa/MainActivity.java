package com.funellites.iTesa;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
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
    long t;
    DataItem B = new DataItem(t, xB, yB, zB);
    float absB = 0;
    float maxB = 0;
    
    Magnetometer magnetometer = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tBTextView = (TextView) findViewById(R.id.tB);
        xBTextView = (TextView) findViewById(R.id.xB);
        yBTextView = (TextView) findViewById(R.id.yB);
        zBTextView = (TextView) findViewById(R.id.zB);
        absBTextView = (TextView) findViewById(R.id.absB);
        maxBTextView = (TextView) findViewById(R.id.maxB);
        iTextView = (TextView) findViewById(R.id.i);
        
        magnetometer = new Magnetometer( this , this );
        
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
        /*sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		sensorManager.SENSOR_DELAY_GAME);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
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
    	  }
      });
	}
	@Override
	public void updateData(long time, float x, float y, float z, int i) {
		B.t = time; // timestamp;
	    B.x = x;    // lateral
	    B.y = y;    // longitudinal
	    B.z = z;    // vertical
	    absB = Math.round( Math.sqrt(xB*xB+yB*yB+zB*zB) ); 
	    if (absB > maxB)
            maxB = absB;
	    
        String str = "i: " + i;
        iTextView.setText(str);
    }

}