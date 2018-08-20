package io.gripxtech.locationjobdispatcher.prefs;


import android.content.Context;
import android.support.annotation.NonNull;

public class MainPrefs extends BasePrefs {

    private final String CurrentDrawerItemID = "CurrentDrawerItemID";

    /**
     * @param TAG     name for SharedPreferences
     * @param context context
     */
    public MainPrefs(@NonNull String TAG, @NonNull Context context) {
        super(TAG, context);
    }

    public int getCurrentDrawerItemID() {
        return getInt(CurrentDrawerItemID/*, R.id.action_home*/);
    }

    public void setCurrentDrawerItemID(int currentDrawerItemID) {
        putInt(CurrentDrawerItemID, currentDrawerItemID);
    }
}
