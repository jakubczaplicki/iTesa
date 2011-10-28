package com.funellites.iTesa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.sqlite.SQLiteException;

public class CsvFileAdapter {
  
   FileWriter writer;
   File file;
  
   public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
   public void open() throws SQLiteException {
      try {
		writer = new FileWriter(file ,true);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

  public void write() {
      
     try {
       writer.write("iTesa CSV Test");
       writer.write(System.getProperty("line.separator"));
       writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}