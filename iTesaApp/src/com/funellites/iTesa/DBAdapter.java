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
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBAdapter {

public final static String TAG = "iTesa";
private static final int    DB_VERSION = 1;
private static final String DB_NAME    = "itesadb.sqlite";
private static final String DB_TABLE     = "telemetry";
private static final String DB_TABLE_CUR = "cursors";
private static final String KEY_ID      = "_id";
private static final String KEY_TIME    = "time";
private static final String KEY_LAT     = "lat";
private static final String KEY_LNG     = "lng";
private static final String KEY_XB      = "xB";
private static final String KEY_YB      = "yB";
private static final String KEY_ZB      = "zB";
private static final String KEY_ABSB    = "absB";
private static final String KEY_CUR     = "cursor";

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
    insertDataCursors();
}

/*** Telemetry database ***/

public long insertDataTelemetry(DataTelemetry dataPos, DataMagnetometer dataMag) {
    ContentValues newValues = new ContentValues(); // Create a new row of values to insert
    newValues.put( KEY_TIME, dataPos.t );
    newValues.put( KEY_LNG,  dataPos.lng );
    newValues.put( KEY_LAT,  dataPos.lat );
    newValues.put( KEY_XB,   dataMag.x );
    newValues.put( KEY_YB,   dataMag.y );
    newValues.put( KEY_ZB,   dataMag.z );
    newValues.put( KEY_ABSB, dataMag.abs );    
    
    long ret = db.insert(DB_TABLE, null, newValues); // Insert the row
    Log.d(TAG, "row: " + ret + 
    		   " lng: " + dataPos.lng +
    		   " lat: " + dataPos.lat +
    		   " abs: " + dataMag.abs);
    return ret;
}

//Remove a row from DB based on its index
public boolean removeDataTelemetry(long _rowIndex) {
    return db.delete(DB_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
}

public long getNoOfRowsTelemetry() {
    String sql = "SELECT COUNT(*) FROM " + DB_TABLE;
    SQLiteStatement statement = db.compileStatement(sql);
    long count = statement.simpleQueryForLong();
    return count;
}

public Cursor getDataTelemetry(long _rowIndex) throws SQLException {
    Cursor cursor = db.query(true, DB_TABLE, 
                             new String[] {KEY_ID, KEY_TIME, KEY_LNG, KEY_LAT, KEY_ABSB},
                             KEY_ID + ">=" + _rowIndex, null, null, null, null, null);
    if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
        throw new SQLException("No data item found for row: " + _rowIndex);
    }

    return cursor;
}

/*** Cursors database ***/

//Add initial value
public long insertDataCursors() {
    ContentValues newValues = new ContentValues();
    newValues.put( KEY_CUR,  0 );
    return db.insert(DB_TABLE_CUR, null, newValues);
}

//Update values with the most recent cursor positions
public boolean updateCursors(long cursor) {
    ContentValues newValues = new ContentValues();
    newValues.put( KEY_CUR, cursor );
    return db.update(DB_TABLE_CUR, newValues, KEY_ID + "=1", null) > 0;  
}

//Return last cursor
public long getLastCursor() throws SQLException {
	long ret = 0;
    Cursor cursor = db.query(true, DB_TABLE_CUR, 
                             new String[] {KEY_ID, KEY_CUR},
                             KEY_ID + "=1", null, null, null, null, null);
    if ((cursor.getCount() == 0) || !cursor.moveToFirst()) 
    {
        ret = 0;
    }
    else
    {
    	ret = cursor.getLong(cursor.getColumnIndex(KEY_CUR));
    }
    return ret;
}

/*** Helper ***/

private static class ITesaDBOpenHelper extends SQLiteOpenHelper {
    public ITesaDBOpenHelper(Context context, String name, CursorFactory factory, int version) { 
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
         KEY_LNG  + " float," +
         KEY_LAT  + " float," +
         KEY_XB   + " float," +
         KEY_YB   + " float," +
         KEY_ZB   + " float," +
         KEY_ABSB   + " float" +
         ");";

    private static final String DATABASE_CUR_CREATE = "create table " + DB_TABLE_CUR + " (" +
        KEY_ID   + " integer primary key autoincrement not null, " +
        KEY_CUR + " long" +
        ");";

    protected void dropAndCreate(SQLiteDatabase _db) {
        _db.execSQL("drop table if exists " + DB_TABLE + ";");
        _db.execSQL(DATABASE_CREATE);
        _db.execSQL("drop table if exists " + DB_TABLE_CUR + ";");
        _db.execSQL(DATABASE_CUR_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        Log.w("TestApp dbAdapter", "Upgrading from version " +
              _oldVersion + " to " +
              _newVersion + ", which will destroy all old data");
        // Drop the old table.
        _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CUR);
        // Create a new one.
        onCreate(_db);
        }
    }
}
