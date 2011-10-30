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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBAdapter {

	private static final int    DB_VERSION = 1;
	private static final String DB_NAME    = "itesadb.sqlite";
	private static final String DB_TABLE   = "magneticfield";
	public static final String KEY_ID      = "_id";
	public static final String KEY_LAT     = "lat";
	public static final String KEY_LNG     = "lng";
	public static final String KEY_TIME    = "time";
	public static final String KEY_XB      = "xB";
	public static final String KEY_YB      = "yB";
	public static final String KEY_ZB      = "zB";
	
	public boolean isOpen = false;

	private SQLiteDatabase db;
	private final Context context;
	private ITesaDBOpenHelper dbHelper;	
	
	public DBAdapter(Context _context){
		this.context = _context;
	    dbHelper = new ITesaDBOpenHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	public void close() {
		db.close();
	}
	
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
		isOpen = true;
	}
	
	public long insertData(DataItem _data) {
	  ContentValues newValues = new ContentValues(); // Create a new row of values to insert
	  newValues.put( KEY_TIME, _data.t );
	  newValues.put( KEY_XB,   _data.x );
	  newValues.put( KEY_YB,   _data.y );
	  newValues.put( KEY_ZB,   _data.z );
	  return db.insert(DB_TABLE, null, newValues); // Insert the row
	}
	
    public boolean removeData(long _rowIndex) {
    	// Remove a row from DB based on its index
    	return db.delete(DB_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	
	public boolean updateData(long _rowIndex, DataItem _data) {
		  ContentValues newValues = new ContentValues();
		  newValues.put( KEY_TIME, _data.t );
		  newValues.put( KEY_XB,   _data.x );
		  newValues.put( KEY_YB,   _data.y );
		  newValues.put( KEY_ZB,   _data.z );
	      return db.update(DB_TABLE, newValues, KEY_ID + "=" + _rowIndex, null) > 0;		  
		}

	// Return data element from DB
    public DataItem getDataItem(long _rowIndex) throws SQLException {
        Cursor cursor = db.query(true, DB_TABLE, 
                                 new String[] {KEY_ID, KEY_XB, KEY_YB, KEY_ZB, KEY_TIME},
                                 KEY_ID + "=" + _rowIndex, null, null, null, 
                                 null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            throw new SQLException("No data item found for row: " + _rowIndex);
          }
          long time = cursor.getLong(cursor.getColumnIndex(KEY_TIME));
          float xB = cursor.getFloat(cursor.getColumnIndex(KEY_XB));
          float yB = cursor.getFloat(cursor.getColumnIndex(KEY_YB));
          float zB = cursor.getFloat(cursor.getColumnIndex(KEY_ZB));
          DataItem result = new DataItem(time, xB, yB, zB);
          return result;
        }

	
	/*** Helper ***/
	
	private static class ITesaDBOpenHelper extends SQLiteOpenHelper {
		
        public ITesaDBOpenHelper(Context context, String name,
				                   CursorFactory factory, int version) { 
			super(context, name, factory, version); 
			}

        @Override
	    public void onCreate(SQLiteDatabase _db) {
          dropAndCreate(_db);
	    }

		// SQL Statement to create a new database.
		private static final String DATABASE_CREATE = "create table " + DB_TABLE + " (" +
		    KEY_ID   + " integer primary key autoincrement not null, " +
		    KEY_TIME + " long," +
		    KEY_XB   + " float," +
		    KEY_YB   + " float," +
		    KEY_ZB   + " float" +
		    ");";
		
    	protected void dropAndCreate(SQLiteDatabase _db) {
    		_db.execSQL("drop table if exists " + DB_TABLE + ";");
    		_db.execSQL(DATABASE_CREATE);
    	}        

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
		  Log.w("TestApp dbAdapter", "Upgrading from version " + 
		        _oldVersion + " to " +
		        _newVersion + ", which will destroy all old data");

          // Drop the old table.
		  _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		  // Create a new one.
		  onCreate(_db);
		}
	}	
}
