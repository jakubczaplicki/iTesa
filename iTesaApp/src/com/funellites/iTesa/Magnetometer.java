package com.funellites.iTesa;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/** All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis. */ 
public class Magnetometer {
	private SensorManager sensorManager = null;
    private Magnetometer.Callback cb = null;
    public long i = 0;
    public long delay = 0;

    public Magnetometer(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    public Magnetometer(Context context,Magnetometer.Callback cb) {
    	this.cb=cb;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		SensorManager.SENSOR_DELAY_FASTEST);
    }

    private long TimeNew = java.lang.System.currentTimeMillis();
    private long TimeOld = TimeNew;
    
    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        
        public void onSensorChanged(SensorEvent event) {
        	synchronized (this) {
        		switch (event.sensor.getType()){ 
        		case Sensor.TYPE_MAGNETIC_FIELD:
        			i++;
        			TimeNew = java.lang.System.currentTimeMillis();
        			delay = TimeNew - TimeOld;
        			TimeOld = TimeNew;
        			cb.updateData(event.timestamp,
        					      event.values[0], 
        					      event.values[1],
        					      event.values[2] );
	            	break;
                }
        	}
        }
    };
    
    public void close() {
    	Log.d("iTesa", "Magnetometer.close() - unregister magnetometer listener");
    	sensorManager.unregisterListener(sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    public interface Callback {
    	/* We probably don't need to use callback. Adding elements to a queue
    	 * or an array and then accessing this queue/array from the MainActivity
    	 * would probably be good enough. But who am I to say ? :)
    	 */
        void updateData(long t, float x,float y,float z);
    }
}
