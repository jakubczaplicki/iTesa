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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class Graph {
	public final static String TAG = "iTesa";

    private static final String KEY_LAT     = "lat";
    private static final String KEY_LNG     = "lng";
    private static final String KEY_ABSB    = "absB";
	
    static DBAdapter dbAdapter;
	
    public Graph(DBAdapter _dbAdapter) 
    {
        dbAdapter = _dbAdapter;
    }

   private static final int WIDTH = 640;
   private static final int HEIGHT = 480;
   private static final int STRIDE = 640;   // must be >= WIDTH
   
   /** Draw a semi-transparent bitmap (PNG), later put it on top of the contour map of the Earth*/
   private static int[] createAtlas() 
   {
        int[] colors = new int[(WIDTH+1) * (HEIGHT+1)];
        //DataMagnetometer dataMag = new DataMagnetometer();
        //DataTelemetry dataPos = new DataTelemetry();
        //long rowsMag = dbAdapter.getNoOfRowsMagnetometer();
        //long rowsPos = dbAdapter.getNoOfRowsTelemetry();

        long lastRow = dbAdapter.getLastCursor();

        
        for (int x=0; x < ( WIDTH * HEIGHT ); x++) {
            colors[x] = (0 << 24) | (255 << 16) | (255 << 8) | 255;		
        }

        Cursor cursor = dbAdapter.getDataTelemetry( lastRow );
     	Log.d(TAG, "Last row was : "+ lastRow);
            
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
                int x = (int) ( (double) lng * ( (double) WIDTH / 360.0 ) );
                int y = (int) ( (double) lat * ( (double) HEIGHT / 180.0 ) );
                    
                if ((y * WIDTH + x) < (WIDTH * HEIGHT) )
                    colors[y * WIDTH + x] = (a << 24) | (r << 16) | (g << 8) | b;
                else
                	Log.d(TAG,"Array out of bounds !!");
          	} while(cursor.moveToNext());
          	dbAdapter.updateCursors( lastRow + (long) cursor.getPosition() );
           	Log.d(TAG, "Last cursor pos: "+ (lastRow + cursor.getPosition()) );
        }
        
        /*
       for (int y = 0; y < HEIGHT; y++) {
           for (int x = 0; x < WIDTH; x++) {
               int r = x * 255 / (WIDTH - 1);
               int g = y * 255 / (HEIGHT - 1);
               int b = 255 - Math.min(r, g);
               int a = Math.max(r, g);
               colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
           }
       }*/
       return colors;
   }
          
    private Bitmap mBitmap;
    private int[]  mColors;
    public void createBitmap() 
    {
        mColors = createAtlas();
        int[] colors = mColors;
        mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        mBitmap.setPixels(colors, 0, STRIDE, 0, 0, WIDTH, HEIGHT);
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(path, "atlas.png");
        Log.d(TAG, "Saving png file to " + file);
        try {
            outStream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream);
            outStream.flush(); 
            outStream.close();
            }
        catch (IOException e) { e.printStackTrace(); }       
    }

}
