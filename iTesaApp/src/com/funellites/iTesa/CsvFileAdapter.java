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

   private final static String getDateTime()  
   {  
	   Format formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
	   Date date = new Date();
       return formatter.format(date);  
   } 
   
   public CsvFileAdapter(String fileName) {
	   File root = new File(Environment.getExternalStorageDirectory(), "iTesa");
       if (!root.exists()) {
           root.mkdirs();
       }
       Log.d("iTesa", "CsvFileAdapter: " + root + " " + getDateTime() + fileName);
       fileName += getDateTime(); 
       file = new File(root, fileName);
   }
   
    public void open() {
    try {
        Log.d("iTesa", "CsvFileAdapter.open()");
        writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(file, true)));
        isOpen = true;
    } catch (IOException e) {
        e.printStackTrace();
        }
    }

    public void close() {
        try {
            Log.d("iTesa", "CsvFileAdapter.close()");
            writer.close();
            isOpen = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(DataItem d) {
        try {
            Log.d("iTesa", "CsvFileAdapter.write()");
            writer.append(d.t + "," + d.x + "," + d.y + "," + d.z + "," + d.n);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}