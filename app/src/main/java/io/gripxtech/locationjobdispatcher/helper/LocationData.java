package io.gripxtech.locationjobdispatcher.helper;

import android.util.Log;

import com.google.gson.annotations.SerializedName;


public class LocationData {

    public static final String TABLE_NAME = "Tracklog";

    public static final String TRACKLOG_ID = "TracklogId";
    public static final String USERID = "UserId";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String FLAG = "flag";
    // public static final String TIME = "time";
    public static final String COLUMN_CREATEON = "CreatedOn";
    public static final String TAG = LocationData.class.getSimpleName();
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + TRACKLOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USERID + " INTEGER,"
                    + LATITUDE + " REAL,"
                    + LONGITUDE + " REAL,"
                    + COLUMN_CREATEON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";


    @SerializedName("TracklogId")
    private int locationid;

    @SerializedName("UserId")
    private int userId;
    @SerializedName("")
    private double latitude;
    private double longitude;
    private String timestamp;


    public LocationData(int userId, double latitude, double longitude, String timestamp) {

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

    @Override
    public String toString() {
        return "Tracklog{" +
                //  "TracklogId='" + locationid + '\'' +
                ", UserId='" + userId + '\'' +
                ", Latitude=" + latitude + '\'' +
                ", Longitude=" + longitude + '\'' +
                ", CreatedOn=" + timestamp + '\'' +


                '}';
    }
}
