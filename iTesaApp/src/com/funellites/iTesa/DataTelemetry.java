package com.funellites.iTesa;

import android.util.Log;


public class DataTelemetry { 
    public final static String TAG = "iTesa";
    public final long  t;    // timestamp [ns]
    public final float lng;  // Geocentric Longitude, Deg, ( -180.0 - 180.0)
    public final float lat;  // Geocentric Latitude, Deg, (-90.0 -  90.0)     
    //public final float dist; // Radial distance

    public DataTelemetry()
    {    	
        this.t = 0;
        this.lng = 0;
        this.lat = 0;
        //this.dist = 0;
     }

    public DataTelemetry(long t, float lng, float lat)
    {
        this.t = t;
        this.lng = lng;
        this.lat = lat;
    }

    // simulate the satellite position based on magnetometer sample number (sounds weird - doesn't it ?) 
    public DataTelemetry( long t, long n )
    {
        this.t = t;
        
        double r = Math.random()*4-2;        
        long p = 5000;
        long q = n/p;
        
        double x = ( (double) ( n - q * p ) ) * ( 2.0 * Math.PI / ( (double) p ) ) + ( ( (double) q ) * 0.1 );
        this.lng = (float) ( ( (double) (n - q * p) ) * 360.0 / ( (double) p ) );
        this.lat = (float) ( 90.0 * Math.sin( x ) + 90.0);
        
        //Log.d(TAG, "q: " + q + " x: " + x + " lat: " + this.lat ); //this.dist = 0;
        //Log.d(TAG, "n: " + n + " lng: " + this.lng + " lat: " + this.lat );
   }
   
    public DataTelemetry getTelemetryData()
    {
    	DataTelemetry data = new DataTelemetry();   
	   //TODO: read telemetry file data
	   return data;
    }
}
