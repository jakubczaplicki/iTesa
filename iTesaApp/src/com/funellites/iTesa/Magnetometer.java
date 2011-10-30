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

import com.funellites.iTesa.DataItem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/** All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis. */ 
public class Magnetometer {
	private SensorManager sensorManager = null;
    private Magnetometer.Callback cb    = null;
    public  long i = 0;
    private long n = 0;
    public  long delay = 0;
    DataItem B = null;

    public Magnetometer(Context context, DataItem dataB, Magnetometer.Callback cb) {
    	this.B  = dataB;
    	this.cb = cb;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener( sensorEventListener,
        		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		SensorManager.SENSOR_DELAY_FASTEST);
    }

    private long TimeNew = System.nanoTime();
    private long TimeOld = TimeNew;
    
    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
                
        public void onSensorChanged(SensorEvent event) {
        	synchronized (this) {
        		switch (event.sensor.getType()) { 
        		case Sensor.TYPE_MAGNETIC_FIELD:
        			n++;

        			B.add(n,
                          event.timestamp,
                          event.values[0], 
                          event.values[1],
                          event.values[2] );

        			TimeNew = event.timestamp;
        			delay = (long)((TimeNew - TimeOld)/1000000);
                    /*if ( ( n % 10 ) == 0 ) {
        			   TimeOld = TimeNew;
                       Log.d("iTesa", "onSensorChanged(): " + delay + " ms");
                       cb.storeData();
                    }*/
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
    	// Callback to store data. Add message to the main thread to get the data from 
    	// DataItem and save it to either sqlite or CSV file without slowing down
    	// data acquisition
        //void storeData();
    }
}
