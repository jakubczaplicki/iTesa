package com.funellites.iTesa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class CsvFileAdapter {
  
   FileWriter writer;
   File file;

   private  final static String getDateTime()  
   {  
       SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_hhmmss");  
       return df.format(new Date());  
   } 
   
   public CsvFileAdapter(String fileName) {
	   File root = new File(Environment.getExternalStorageDirectory(), "iTesa");
       if (!root.exists()) {
           root.mkdirs();
       }
       Log.d("iTesa", "CsvFileAdapter: " + root + " " + getDateTime() + fileName);
       file = new File(root, fileName);
   }
   
   public void open() throws SQLiteException {
      try {
  		Log.d("iTesa", "CsvFileAdapter.open()");
		writer = new FileWriter(file ,true);
	} catch (IOException e) {
		e.printStackTrace();
	}
   }

   public void close() {
		try {
			Log.d("iTesa", "CsvFileAdapter.close()");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

  public void write(DataItem _data) {
     try {
    	 Log.d("iTesa", "CsvFileAdapter.write()");
    	 writer.write(String.valueOf(_data.t));
    	 writer.write(",");
    	 writer.write(String.valueOf(_data.x));
    	 writer.write(",");
    	 writer.write(String.valueOf(_data.y));
    	 writer.write(",");
    	 writer.write(String.valueOf(_data.z));
    	 writer.write(",");
    	 writer.write(String.valueOf(_data.n));
    	 writer.write(System.getProperty("line.separator"));
         writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}