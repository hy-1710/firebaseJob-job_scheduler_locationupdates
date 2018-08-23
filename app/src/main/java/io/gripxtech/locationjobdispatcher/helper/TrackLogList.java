package io.gripxtech.locationjobdispatcher.helper;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrackLogList {

    @SerializedName("TrackLog")
    private ArrayList<LocationData> locationDataArrayList;

    public ArrayList<LocationData> getLocationDataArrayList() {
        return locationDataArrayList;
    }

    public void setLocationDataArrayList(ArrayList<LocationData> locationDataArrayList) {
        this.locationDataArrayList = locationDataArrayList;
    }
}
