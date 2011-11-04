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

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class DataItem {

	CsvFileAdapter csvFileAdapter = null;
	
	final boolean SMA = true;
	final boolean WMA = false;
	
	private ArrayList<DataItem> dataArray = null; // array of data
	
	long  n;    // sample number 	
	long  t;    // timestamp [ns]
    float lat;  // Geocentric Latitude, Deg, (-90.0 -  90.0)     
    float lng;  // Geocentric Longitude, Deg, ( -180.0 - 180.0)
    float dist; // Radial distance
	float x;    // lateral [uT]
    float y;    // longitudinal [uT]
    float z;    // vertical [uT]
    float abs;  // absolute B [uT]
    float max;  // max B [uT]
    float min;  // min B [uT]
    float sma;  // simple moving average B [uT]

    /* Simple Moving Average */
    private int   size  = 100; // TODO make this as an option/setting
    private float total = 0f;
    private int   index = 0;
    private float samples[];
    
    public DataItem() {
    	setAvg(size);
    	dataArray = new ArrayList<DataItem>(); 
    }

    public DataItem(long _time, float _xB, float _yB, float _zB) {
        this.t = _time;
        this.x = _xB;
        this.y = _yB;
        this.z = _zB;
    	setAvg(size);
    	this.dataArray = new ArrayList<DataItem>();
    }
    
    public void add(long i, long t, float x,float y,float z) {
        this.n    = i;
    	this.t    = t;
        this.x    = x;
        this.y    = y;
        this.z    = z;
    	this.abs = Math.round( Math.sqrt(x*x+y*y+z*z) );
    	this.addAvg(this.abs);
    	this.sma = this.getAvg();
	    if (this.abs > this.max)
            this.max = this.abs;
	    
	    this.dataArray.add(this);
	    
	    /*if ( this.dataArray.size()  > 10 )
	    {
		   for (int k = 0; k < this.dataArray.size() ; k++) {
		       Log.d("iTesa", "k: " + k + " B.n: " + this.dataArray.remove(0).n);
		   }
	    }*/
	    
	    /*
	    if ( this.dataArray.size() > 10 ) {
	    //Log.d("iTesa", "i: " + i + " B.n: " + this.n);
    	//Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.get(size).n);
    	
	    	Log.d("iTesa", "size: " + this.dataArray.size() );
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.get(this.dataArray.size()-1).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(this.dataArray.size()-1).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.get(this.dataArray.size()-2).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(this.dataArray.size()-2).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.get(this.dataArray.size()-3).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(this.dataArray.size()-3).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.get(0).n);
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(0).n);
    	Log.d("iTesa", "size: " + this.dataArray.size() );
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(this.dataArray.size()-1).n);
    	Log.d("iTesa", "size: " + this.dataArray.size() );
    	Log.d("iTesa", "i: " + i + " B.n: " + this.dataArray.remove(this.dataArray.size()-1).n);
    	Log.d("iTesa", "size: " + this.dataArray.size() );
    	//Log.d("iTesa", "size: " + this.dataArray.size() );
	    }*/
    }

    @SuppressWarnings("unchecked")
	public DataItem getAverage() {
    	// copy the main arrayList into new object, and clear it
        ArrayList<DataItem>  _dataArray = (ArrayList<DataItem>) dataArray.clone();
        dataArray.clear();
    	int _n = 0;
    	float _x = 0.0f;
    	float _y = 0.0f;
    	float _z = 0.0f;
    	float _max = 0.0f;
	    DataItem avgB = new DataItem();
    	for (DataItem d : _dataArray) {
	    	_n++;
	    	_x += d.x;
	    	_y += d.y;
	    	_z += d.z;
		    if (d.abs > _max)
	            _max = d.abs;
	    }
    	avgB.n = _n; // number of samples used in averaging
    	avgB.t = System.currentTimeMillis();
	    avgB.x = _x/_n;
	    avgB.y = _y/_n;
	    avgB.z = _z/_n;
	    avgB.max = _max;	    
	    return avgB;
    }

    
    /** Set Simple Moving Average */
    public void setAvg(int size) {
        this.size = size;
        samples = new float[size];
        for (int i = 0; i < size; i++) samples[i] = 0f;
    }

    /** Add data to average */
    public void addAvg(float x) {
    	
    	if (SMA) { /* Simple Moving Average */
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0;
    	}
    	else if (WMA){ /* Weight Moving Average */
    	}
    }

    public float getAvg() {
        return total / size;
    }
    
    public void logFileOpen() {
    	csvFileAdapter = new CsvFileAdapter("iTesa.csv");
    	csvFileAdapter.open();
    }

    public void logFileClose() {
    	if ( csvFileAdapter.isOpen ) {
        	csvFileAdapter.close();
        	}
    }
    
    @SuppressWarnings("unchecked")
	public void logFileSave() {
        ArrayList<DataItem>  _dataArray = (ArrayList<DataItem>) dataArray.clone();
        dataArray.clear();    	
        Iterator<DataItem> itr = _dataArray.iterator();
        while(itr.hasNext()) {
		    Log.d("iTesa", " Bn: " + itr.next().n);
			//csvFileAdapter.write( itr.next() );
		}
    }
    
}