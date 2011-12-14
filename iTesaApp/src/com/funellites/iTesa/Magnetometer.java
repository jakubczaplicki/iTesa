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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/** All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis. */ 
public class Magnetometer implements SensorEventListener 
{
    private SensorManager sensorManager = null; 
    public  long n = 0;
    public  long delay = 0;
    private SMA  sma;
    public DataMagnetometer dataMag = null;

    public Magnetometer(Context context) 
    {
        sma  = new SMA(100);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    long TimeNew = System.nanoTime();
    long TimeOld = TimeNew;

    @Override
    public void onSensorChanged(SensorEvent event) 
    {
        synchronized (this) 
        {
            switch (event.sensor.getType()) 
            { 
                case Sensor.TYPE_MAGNETIC_FIELD:
                if ( n >= Long.MAX_VALUE - 1 ) { n = 0; }
                n++;
                DataMagnetometer data = new DataMagnetometer( event.timestamp,
                                                              event.values[0], 
                                                              event.values[1],
                                                              event.values[2] );
          	    sma.addData( data.abs );
        	    data.abs = sma.getAvg();
        	   
        	    dataMag = data; // public 
               
                TimeNew = event.timestamp;
                delay = (long)((TimeNew - TimeOld)/1000000);
                TimeOld = TimeNew;
                break;
            }
        }
    }

    public void start() 
    {
    	Log.d("iTesa", "Magnetometer.start() - register magnetometer listener");
        sensorManager.registerListener( this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_FASTEST);
    }
	   
    public void stop() 
    {
        Log.d("iTesa", "Magnetometer.stop() - unregister magnetometer listener");
        sensorManager.unregisterListener(this,
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    /*** Simple Moving Average ***/
    class SMA {
        private int   size; // TODO make this as an option/setting
        private float total = 0f;
        private int   index = 0;
        private float samples[];
        
        /** Construct and set Simple Moving Average */
        public SMA(int _size) {
           size = _size;
           samples = new float[size];
           for (int i = 0; i < size; i++) samples[i] = 0f;
        }

        /** Add data to average */
        public void addData(float x) 
        {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0;
        }
        
        public float getAvg() {
           return total / size;
         }
    }
}
