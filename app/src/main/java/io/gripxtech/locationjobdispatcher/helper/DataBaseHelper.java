package io.gripxtech.locationjobdispatcher.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String TAG = DataBaseHelper.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "location_db";
    private static final String TABLE_LOCATION = "Tracklog";
    private static final String TABLE_CONFIGURATION = "tblConfiguration";
    private static final String TABLE_CON_LOG = "tblConLog";


    private String mLastUpdateTime;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss a ";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Log.e(TAG, "getCurrentTimeUsingDate: dateFormat" + dateFormat);
        String formattedDate = dateFormat.format(date);

        Log.e(TAG, "Current time of the day using Date - 12 hour format:  " + formattedDate);

        return formattedDate;


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(LocationData.CREATE_TABLE);
        sqLiteDatabase.execSQL(SyncData.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationData.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SyncData.TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);

    }

    public long insertNote(double latitude, double longitude) {
        // get writable database as we want to write data

        Log.e(TAG, "insertNote: CALLED ----");
        Log.e(TAG, "insertNote: LAT :" + latitude);
        Log.e(TAG, "insertNote: LONG : " + longitude);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        mLastUpdateTime = getCurrentTimeUsingDate();
        Log.e(TAG, "insertNote: " + mLastUpdateTime);
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocationData.USERID, 1);
        values.put(LocationData.LATITUDE, latitude);
        values.put(LocationData.LONGITUDE, longitude);
        values.put(LocationData.COLUMN_CREATEON, mLastUpdateTime);

        Log.e(TAG, "insertNote:  TABLE NAME : " + LocationData.CREATE_TABLE);

        // insert row
        long id = db.insert(LocationData.TABLE_NAME, null, values);
        Log.e(TAG, "insertNote: inserted record successfully :" + id);

        // close db connection
        db.close();


       /* ContentValues cv = new ContentValues();
        cv.put(SyncData.LAST_TRACKLOG_ID, );*/


        // return newly inserted row id
        return id;
    }

    public long insertSyncData(int lastLocationId, int userId, int totalSyncRecordSize, String createdOn) {
        // get writable database as we want to write data

        Log.e(TAG, "insertSyncData: CALLED ----");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        mLastUpdateTime = getCurrentTimeUsingDate();
        Log.e(TAG, "insertSyncData: " + mLastUpdateTime);
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(SyncData.LAST_TRACKLOG_ID, lastLocationId);
        values.put(SyncData.USERID, userId);
        values.put(SyncData.TOTAL_SYNCRECORD, totalSyncRecordSize);
        values.put(LocationData.COLUMN_CREATEON, mLastUpdateTime);

        Log.e(TAG, "insertNote:  TABLE NAME : " + SyncData.CREATE_TABLE);

        // insert row
        long id = db.insert(SyncData.TABLE_NAME, null, values);
        Log.e(TAG, "insertSyncData: inserted record successfully :" + id);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public LocationData getLocationData(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LocationData.TABLE_NAME,
                new String[]{LocationData.TRACKLOG_ID, LocationData.USERID, LocationData.LATITUDE, LocationData.LONGITUDE, LocationData.COLUMN_CREATEON},
                LocationData.TRACKLOG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        LocationData location = new LocationData(
                //cursor.getInt(cursor.getColumnIndex(LocationData.TRACKLOG_ID)),
                cursor.getInt(cursor.getColumnIndex(LocationData.USERID)),
                cursor.getDouble(cursor.getColumnIndex(LocationData.LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(LocationData.LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(LocationData.COLUMN_CREATEON)));

        // close the db connection
        cursor.close();

        return location;
    }

    public SyncData getSyncData(long syncId) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(SyncData.TABLE_NAME,
                new String[]{SyncData.LAST_TRACKLOG_ID, SyncData.USERID, SyncData.TOTAL_SYNCRECORD, SyncData.COLUMN_CREATEON},
                SyncData.SYNCID + "=?",
                new String[]{String.valueOf(syncId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        SyncData syncData = new SyncData(
                cursor.getInt(cursor.getColumnIndex(SyncData.SYNCID)),
                cursor.getInt(cursor.getColumnIndex(SyncData.LAST_TRACKLOG_ID)),
                cursor.getInt(cursor.getColumnIndex(SyncData.USERID)),
                cursor.getInt(cursor.getColumnIndex(SyncData.TOTAL_SYNCRECORD)),
                cursor.getString(cursor.getColumnIndex(SyncData.COLUMN_CREATEON)));

        // close the db connection
        cursor.close();

        return syncData;
    }

    public List<LocationData> getAllLocation() {
        List<LocationData> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + LocationData.TABLE_NAME + " ORDER BY " +
                LocationData.COLUMN_CREATEON + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                locationData.setLocationid(cursor.getInt(cursor.getColumnIndex(LocationData.TRACKLOG_ID)));
                locationData.setUserid(cursor.getInt(cursor.getColumnIndex(LocationData.USERID)));
                locationData.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationData.LATITUDE)));
                locationData.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationData.LONGITUDE)));
                locationData.setTimestamp(cursor.getString(cursor.getColumnIndex(LocationData.COLUMN_CREATEON)));

                notes.add(locationData);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }


    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + LocationData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public List<LocationData> getFromAllLocation(int fromId) {
        List<LocationData> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + LocationData.TABLE_NAME + " WHERE " + LocationData.TRACKLOG_ID +
                " > " + fromId;
        //+" ORDER BY " +   LocationData.COLUMN_TIMESTAMP + " DESC";
        Log.e(TAG, "getFromAllLocation: check query -----" + selectQuery);


        //select * from tblLocation  where locationId  between 33 and 87 order by locationId desc

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                locationData.setLocationid(cursor.getInt(cursor.getColumnIndex(LocationData.TRACKLOG_ID)));
                locationData.setUserid(cursor.getInt(cursor.getColumnIndex(LocationData.USERID)));
                locationData.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationData.LATITUDE)));
                locationData.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationData.LONGITUDE)));
                locationData.setTimestamp(cursor.getString(cursor.getColumnIndex(LocationData.COLUMN_CREATEON)));

                notes.add(locationData);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getSyncCount() {
        String countQuery = "SELECT  * FROM " + SyncData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateLatitude(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationData.LATITUDE, locationData.getLatitude());
        values.put(LocationData.LONGITUDE, locationData.getLongitude());
        values.put(LocationData.COLUMN_CREATEON, locationData.getTimestamp());

        // updating row
        return db.update(LocationData.TABLE_NAME, values, LocationData.TRACKLOG_ID + " = ?",
                new String[]{String.valueOf(locationData.getLocationid())});
    }

    public void deleteNote(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LocationData.TABLE_NAME, LocationData.TRACKLOG_ID + " = ?",
                new String[]{String.valueOf(locationData.getLocationid())});
        db.close();
    }

}
