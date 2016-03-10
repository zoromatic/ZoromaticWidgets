/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zoromatic.widgets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_LOCATION_ID = "location_id";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_NAME = "name";
    
    private static final String TAG = "SQLiteDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
        "create table locations (_id integer primary key autoincrement, "
        + "location_id integer not null, latitude real not null, longitude real not null, name text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "locations";
    private static final int DATABASE_VERSION = 3;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS locations");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public SQLiteDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the locations database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public SQLiteDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new location using data provided. If the location is
     * successfully created return the new rowId for that location, otherwise return
     * a -1 to indicate failure.
     * 
     * @param location_id
     * @param latitude
     * @param longitude
     * @param name
     * @return rowId or -1 if failed
     */
    public long createLocation(long location_id, double latitude, double longitude, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOCATION_ID, location_id);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_LONGITUDE, longitude);
        initialValues.put(KEY_NAME, name);        

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the location with the given rowId
     * 
     * @param rowId id of locations to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLocation(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all locations in the database
     * 
     * @return Cursor over all locations
     */
    public Cursor fetchAllLocations() {

        return mDb.query(DATABASE_TABLE, 
        		new String[] {KEY_ROWID, KEY_LOCATION_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_NAME}, 
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the location that matches the given rowId
     * 
     * @param rowId id of location to retrieve
     * @return Cursor positioned to matching location, if found
     * @throws SQLException if location could not be found/retrieved
     */
    public Cursor fetchLocation(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, 
            		new String[] {KEY_ROWID, KEY_LOCATION_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_NAME}, 
            		KEY_ROWID + "=" + rowId, 
            		null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the location using the details provided. The location to be updated is
     * specified using the rowId, and it is altered to use the values passed in
     * 
     * @param rowId id of location to update
     * @param location_id
     * @param latitude
     * @param longitude
     * @param name
     * @return true if the location was successfully updated, false otherwise
     */
    public boolean updateLocation(long rowId, long location_id, double latitude, double longitude, String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_LOCATION_ID, location_id);
        args.put(KEY_LATITUDE, latitude);
        args.put(KEY_LONGITUDE, longitude);
        args.put(KEY_NAME, name);        

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
