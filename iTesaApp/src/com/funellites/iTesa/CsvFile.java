package com.funellites.iTesa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class CsvFile {
	public final static String TAG = "iTesa";

    private static final String KEY_LAT     = "lat";
    private static final String KEY_LNG     = "lng";
    private static final String KEY_ABSB    = "absB";

    private BufferedWriter writer;
    private File file;
    public boolean isOpen = false;
    public static final long FLUSH_TIME = 60000; // Flush the BufferedWriter every minute
    private long lastLog = 0;
   
    static DBAdapter dbAdapter;
   
    /*private final static String getDateTime()  
    {  
        Format formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
        Date date = new Date();
        return formatter.format(date);  
    }*/
   
   public CsvFile(DBAdapter _dbAdapter, String _dataDirName, String _csvFileName ) 
   {
       String dataDirName = _dataDirName;
       String csvFileName = _csvFileName;
       dbAdapter = _dbAdapter;
	   
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
            File root = new File(Environment.getExternalStorageDirectory(), dataDirName);
            if (!root.exists()) {
                root.mkdirs();
            }
            //Log.d("iTesa", "CsvFileAdapter: " + root + " " + getDateTime() + csvFileName);
            //csvFileName = getDateTime() + "_" + fileName;
            Log.d(TAG, "CsvFile: " + root + "/" + csvFileName);
            file = new File(root, csvFileName);
		}
   }
   
   private void openWriter() 
   {
      try {
         Log.d(TAG, "CsvFile.openWriter()");
         writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file, true) ));
         this.isOpen = true;
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

    private void closeWriter() {
        try {
            Log.d(TAG, "CsvFile.closeWriter()");
            writer.close();
            this.isOpen = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
   public void updateFile() {
	   Log.d(TAG, "CsvFile.updateFile()");
	   openWriter();

       /* Database related code */
       long lastRow = dbAdapter.getLastCursor();
       Cursor cursor = dbAdapter.getDataTelemetry( lastRow );

       long time = System.currentTimeMillis();
       long delay = time - lastLog;
       
       if (cursor.moveToFirst())
       {
        	do 
         	{
                float lng = cursor.getFloat(cursor.getColumnIndex(KEY_LNG));
          	    float lat = cursor.getFloat(cursor.getColumnIndex(KEY_LAT));
          	    float absB = cursor.getFloat(cursor.getColumnIndex(KEY_ABSB));

          	    try {
                    writer.append(lng + "," + lat + "," + absB);
                    writer.newLine();
                    if (delay > FLUSH_TIME)
                        writer.flush();
          	    } catch (IOException e) {
          	         Log.e(TAG, "IOException in append()", e);
                }
         	} while(cursor.moveToNext());
       }
       lastLog = time;
       Log.d(TAG, "CSV file write time: " + (System.currentTimeMillis() - time));
       
	   closeWriter();
   }
}
