package com.funellites.iTesa;

public class DataTelemetry { 
   public final long  t;    // timestamp [ns]
   public final float lat;  // Geocentric Latitude, Deg, (-90.0 -  90.0)     
   public final float lng;  // Geocentric Longitude, Deg, ( -180.0 - 180.0)
   //public final float dist; // Radial distance

   public DataTelemetry()
   {
      this.t = 0;
      this.lat = 0;
      this.lng = 0;
      //this.dist = 0;
   }

   public DataTelemetry(long t, float lat, float lng)
   {
      this.t = t;
      this.lat = lat;
      this.lng = lng;
   }

}
