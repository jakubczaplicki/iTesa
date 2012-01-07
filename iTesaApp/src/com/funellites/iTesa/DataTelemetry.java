package com.funellites.iTesa;


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
        this.t = t+1000;

        //this.lng = (float) ((double)(n - ((n/108001)*108000)) * (360.0/108000.0) ); // ~ 108000 samples per 90 minutes (1 orbit), assuming 1 sample per ~50ms
        //this.lat = (float) ((90.0*Math.sin(( (double)n * (360.0/108000.0) )*(360.0/320.0)*(Math.PI/180.0)))+90.0); // position should fit in a 'box' of x:(0,360) y:(0,180)
        
        //this.lng = (float) ((double)(n - ((n/108001)*2880)) * (360.0/2880.0) ); // ~ 108000 samples per 90 minutes (1 orbit), assuming 1 sample per ~50ms
        //this.lat = (float) ((90.0*Math.sin(( (double)n * (360.0/2880.0) )*(360.0/321.0)*(Math.PI/180.0)))+90.0); // position should fit in a 'box' of x:(0,360) y:(0,180)
        
        this.lng = (float) ( ((double)n - ((double)(n/1441) * 1440.0 )) * (360.0/1440.0) );
        double r = Math.random()*4-2;
        this.lat = (float) ( ((90.0*Math.sin( (double)(n/1440))/5.0 + ((double)n*(360.0/1440.0)*(Math.PI/180.0))))+90.0 + r);
        
        //this.dist = 0;
        //Log.d(TAG, "n: " + n + " lng: " + this.lng + " lat: " + this.lat );
   }
   
    public DataTelemetry getTelemetryData()
    {
    	DataTelemetry data = new DataTelemetry();   
	   //TODO: read telemetry file data
	   return data;
    }
}
