package com.funellites.iTesa;

public class DataMagnetometer {
   public final long  n;    // sample number 	
   public final long  t;    // timestamp [ns]
   //public final float lat;  // Geocentric Latitude, Deg, (-90.0 -  90.0)     
   //public final float lng;  // Geocentric Longitude, Deg, ( -180.0 - 180.0)
   //public final float dist; // Radial distance
   public final float x;    // lateral [uT]
   public final float y;    // longitudinal [uT]
   public final float z;    // vertical [uT]
   public float abs;  // absolute B [uT]
   public float max;  // max B [uT]
   //public final float min;  // min B [uT]
   //public final float sma;  // simple moving average B [uT]

   public DataMagnetometer()
   {
      this.n = 0;
      this.t = 0;
      this.x = 0;
      this.z = 0;
      this.y = 0;
      //setAvg(size);
   }

   public DataMagnetometer(long n, long t, float x, float y, float z)
   {
      this.n = n;
      this.t = t;
      this.x = x;
      this.z = z;
      this.y = y;
  	  this.abs = Math.round( Math.sqrt(x*x+y*y+z*z) );
      if (this.abs > this.max)
         this.max = this.abs;

  	  //this.addAvg(B.abs);
  	  //this.B.sma = B.getAvg();
      //setAvg(size);
   }

	   /* Simple Moving Average */
	   final boolean SMA = true;
	   final boolean WMA = false;
	   private int   size  = 100; // TODO make this as an option/setting
	   private float total = 0f;
	   private int   index = 0;
	   private float samples[];
	     
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

}
