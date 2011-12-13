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
public class Magnetometer extends DataItem implements SensorEventListener 
{
    private SensorManager sensorManager = null; 
    private Magnetometer.Callback cb = null;
    public  long n = 0;
    public  long t = 0;
    public  long delay = 0;

    public Magnetometer(Context context, Magnetometer.Callback cb) 
    {
        this.cb=cb;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) 
    {
        long TimeNew = System.nanoTime();
        long TimeOld = TimeNew;

        synchronized (this) 
        {
            switch (event.sensor.getType()) 
            { 
               case Sensor.TYPE_MAGNETIC_FIELD:
               if ( n >= Long.MAX_VALUE - 1 ) { n = 0; }
               n++;
               if (cb!=null) 
               {
                  cb.addDataMagnetometer( n,
                              event.timestamp,
                              event.values[0], 
                              event.values[1],
                              event.values[2] );
               }
               t = event.timestamp;
               TimeNew = event.timestamp;
               delay = (long)((TimeNew - TimeOld)/1000000);
               TimeOld = TimeNew;
               break;
            }
         }
      }

    public void start() 
    {
        sensorManager.registerListener( this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_FASTEST);
    }
	   
    public void stop() 
    {
        Log.d("iTesa", "Magnetometer.close() - unregister magnetometer listener");
        sensorManager.unregisterListener(this,
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    public interface Callback 
    {
        void addDataMagnetometer(long n, long t, float bx, float by, float bz);
    }
}
