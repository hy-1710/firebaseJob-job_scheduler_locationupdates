package io.gripxtech.locationjobdispatcher.helper;

import android.util.Log;

public class LocationData {

    public static final String TABLE_NAME = "tblLocation";

    public static final String LOCATION_ID = "locationId";
    public static final String USERID = "userId";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    // public static final String TIME = "time";
    public static final String COLUMN_TIMESTAMP = "timeStamp";
    public static final String TAG = LocationData.class.getSimpleName();
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USERID + " INTEGER,"
                    + LATITUDE + " REAL,"
                    + LONGITUDE + " REAL,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    private int locationid;
    private int userId;
    private double latitude;
    private double longitude;
    private String timestamp;


    public LocationData(int locationd, int userId, double latitude, double longitude, String timestamp) {
        this.locationid = locationid;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public LocationData() {

        Log.e(TAG, "LocationData:  CREATE TABLE" + CREATE_TABLE);
    }


    public int getLocationid() {
        return locationid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public int getUserid() {
        return userId;
    }

    public void setUserid(int userid) {
        this.userId = userid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
