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

public class DataItem {

	long  t;    // timestamp [ns]
    float x;    // lateral [uT]
    float y;    // longitudinal [uT]
    float z;    // vertical [uT]
    float abs; // absolute B [uT]
    float max; // max B [uT]
    float min; // min B [uT]
    float sma; // simple moving average B [uT]

    /* Simple Moving Average */
    private int size;
    private float total = 0f;
    private int index = 0;
    private float samples[];
    
    public DataItem() {
    	setSMA(10);
    }

    public DataItem(long _time, float _xB, float _yB, float _zB) {
        t = _time;
        x = _xB;
        y = _yB;
        z = _zB;
    	setSMA(10);
    }
    
    public void add(long t, float x,float y,float z) {
        this.t    = t;
        this.x    = x;
        this.y    = y;
        this.z    = z;
    	this.abs = Math.round( Math.sqrt(x*x+y*y+z*z) );
    	this.addSMA(this.abs);
    	this.sma = this.getAverage();
	    if (this.abs > this.max)
            this.max = this.abs;
    }

    public void setSMA(int size) {
        this.size = size;
        samples = new float[size];
        for (int i = 0; i < size; i++) samples[i] = 0f;
    }

    public void addSMA(float x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) index = 0;
    }

    public float getAverage() {
        return total / size;
    }
}