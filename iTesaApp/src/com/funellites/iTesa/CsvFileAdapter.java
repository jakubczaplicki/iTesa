package com.funellites.iTesa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class CsvFileAdapter {
  
   private BufferedWriter writer;
   private File file;
   public boolean isOpen = false;
   public static final long FLUSH_TIME = 60000; // Flush the BufferedWriter every minute
   private long lastLog = 0;
   
   private final static String getDateTime()  
   {  
      Format formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
      Date date = new Date();
      return formatter.format(date);  
   } 
   
   public CsvFileAdapter(String dirName, String fileName) 
   {
        @SuppressWarnings("unused")
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if ( mExternalStorageWriteable )
		{
            File root = new File(Environment.getExternalStorageDirectory(), dirName);
            if (!root.exists()) {
                root.mkdirs();
            }
            Log.d("iTesa", "CsvFileAdapter: " + root + " " + getDateTime() + fileName);
            fileName = getDateTime() + "_" + fileName; 
            file = new File(root, fileName);
		}
   }
   
   public void openWriter() 
   {
      try {
         Log.d("iTesa", "CsvFileAdapter.open()");
         writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file, true) ));
         this.isOpen = true;
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void closeWriter() {
      try {
         Log.d("iTesa", "CsvFileAdapter.close()");
         writer.close();
         this.isOpen = false;
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void write(DataMagnetometer d) {
      long time = System.currentTimeMillis();
      long delay = time - lastLog;
      try {
         if ( d == null ) {
            Log.d("iTesa", "CSV log file - nothing to write");        
         } else {
            writer.append(d.t + "," + d.x + "," + d.y + "," + d.z + "," + d.abs + "," + d.max);
            writer.newLine();
            if (delay > FLUSH_TIME)
            writer.flush();
            lastLog = time;
        }
      }
      catch (IOException e) {
         Log.e("iTesa", "IOException in append()", e);
      }
   }
}
