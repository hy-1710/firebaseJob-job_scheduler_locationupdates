package io.gripxtech.locationjobdispatcher.helper;

import android.util.Log;

public class SyncData {

    public static final String TABLE_NAME = "Synclog";

    public static final String LAST_TRACKLOG_ID = "LastTracklogId";
    public static final String SYNCID = "SyncId";
    public static final String USERID = "UserId";
    public static final String TOTAL_SYNCRECORD = "TotalSyncRecord";

    public static final String FLAG = "flag";
    // public static final String TIME = "time";
    public static final String COLUMN_CREATEON = "CreatedOn";
    public static final String TAG = SyncData.class.getSimpleName();
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + SYNCID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + LAST_TRACKLOG_ID + " INTEGER ,"
                    + USERID + " INTEGER,"
                    + TOTAL_SYNCRECORD + " INTEGER,"

                    + COLUMN_CREATEON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    private int syncID;
    private int LastTracklogId;
    private int userId;
    private int totalSync;
    private String timestamp;


    public SyncData() {

        Log.e(TAG, "LocationData:  CREATE TABLE" + CREATE_TABLE);
    }

    public SyncData(int syncID, int lastTracklogId, int userId, int totalSync, String timestamp) {
        this.syncID = syncID;
        this.LastTracklogId = lastTracklogId;
        this.userId = userId;
        this.totalSync = totalSync;
        this.timestamp = timestamp;
    }

    public int getSyncID() {
        return syncID;
    }

    public void setSyncID(int syncID) {
        this.syncID = syncID;
    }

    public int getLastTracklogId() {
        return LastTracklogId;
    }

    public void setLastTracklogId(int lastTracklogId) {
        LastTracklogId = lastTracklogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalSync() {
        return totalSync;
    }

    public void setTotalSync(int totalSync) {
        this.totalSync = totalSync;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SyncData{" +
                "locationid='" + LastTracklogId + '\'' +
                ", userid='" + userId + '\'' +
                ", syncID='" + syncID + '\'' +
                ", totalSync=" + totalSync + '\'' +

                ", timeStamp=" + timestamp + '\'' +


                '}';
    }
}
