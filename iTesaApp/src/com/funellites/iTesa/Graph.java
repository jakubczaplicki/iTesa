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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Graph {
	public final static String TAG = "iTesa";

    private static final String KEY_LAT     = "lat";
    private static final String KEY_LNG     = "lng";
    private static final String KEY_ABSB    = "absB";
    
    private static final String FILENAME    = "atlas.png";
	
    static DBAdapter dbAdapter;
	
    public Graph(DBAdapter _dbAdapter ) 
    {
        dbAdapter = _dbAdapter;
    }

   private static final int WIDTH = 1440;
   private static final int HEIGHT = 720;
   private static final int STRIDE = WIDTH;   // must be >= WIDTH
   
    public void createBitmap() 
    {
        //db related
    	long lastRow = dbAdapter.getLastCursor();
        Cursor cursor = dbAdapter.getDataTelemetry( lastRow );
     	Log.d(TAG, "Last row was : "+ lastRow);

        // load png bitmap
    	String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path + "/" + FILENAME);

        Bitmap bitmap = null;
		try {
			InputStream inStream = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(inStream).copy(Bitmap.Config.ARGB_8888, true);
		} catch (FileNotFoundException e1) {
			Log.d(TAG,"File not found - create new !");
			e1.printStackTrace();
			try {
				File fileOrig = new File(path + "/" + "earth.png");
				InputStream inStream;
				inStream = new FileInputStream(fileOrig);
				bitmap = BitmapFactory.decodeStream(inStream).copy(Bitmap.Config.ARGB_8888, true);
			} catch (FileNotFoundException e) {
				Log.d(TAG,"Can't even create new bitmap !");
				e.printStackTrace();
			}
		}
		
		// modify bitmap based on data from sqlite
     	int[] colors = new int[ WIDTH * HEIGHT ];
		bitmap.getPixels(colors, 0, STRIDE, 0, 0, WIDTH , HEIGHT );

        if (cursor.moveToFirst())
        {
         	do 
          	{
           	    float lng = cursor.getFloat(cursor.getColumnIndex(KEY_LNG));
           	    float lat = cursor.getFloat(cursor.getColumnIndex(KEY_LAT));
           	    float absB = cursor.getFloat(cursor.getColumnIndex(KEY_ABSB));
                int r = (int) ( (double) absB * 255.0 / 100.0);
                int g = (int) ( (double) absB * 255.0 / 100.0);
                int b = 255 - Math.min(r, g);
                int a = 255;
                int x = (int) ( (double) lng * ( (double) (WIDTH-1) / 360.0 ) );
                int y = (int) ( (double) lat * ( (double) (HEIGHT-1) / 180.0 ) );
                    
                if ((y * WIDTH + x) < (WIDTH * HEIGHT) )
                    colors[y * WIDTH + x] = (a << 24) | (r << 16) | (g << 8) | b;
                else
                	Log.d(TAG,"Array out of bounds !!");
          	} while(cursor.moveToNext());
          	dbAdapter.updateCursors( lastRow + (long) cursor.getPosition() );
           	Log.d(TAG, "Last cursor pos: "+ (lastRow + cursor.getPosition()) );
        }
		
        bitmap.setPixels(colors, 0, STRIDE, 0, 0, WIDTH , HEIGHT );
        
        Log.d(TAG,path + "/" + FILENAME);
        Log.d(TAG, "Saving png file to " + file);
        
        try {
        	OutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream);
            outStream.flush(); 
            outStream.close();
            }
        catch (IOException e) { e.printStackTrace(); }       
    }

}
