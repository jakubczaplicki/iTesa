package com.funellites.iTesa;

public class DataMagnetometer {
   public final long  t;    // timestamp [ns]
   public final float x;    // lateral [uT]
   public final float y;    // longitudinal [uT]
   public final float z;    // vertical [uT]
   public float abs;        // absolute B [uT]
   public float max;        // max B [uT]

   public DataMagnetometer()
   {
      this.t = 0;
      this.x = 0;
      this.z = 0;
      this.y = 0;
   }

   public DataMagnetometer(long t, float x, float y, float z)
   {
      this.t = t;
      this.x = x;
      this.z = z;
      this.y = y;
      this.abs = Math.round( Math.sqrt(x*x+y*y+z*z) );
      if (this.abs > this.max)
         this.max = this.abs;
   }
}

