package io.gripxtech.locationjobdispatcher.helper;

import android.util.Log;

public class ApiUtils {

    //public static final String BASE_URL = "http://jsonplaceholder.typicode.com/";
    public static final String BASE_URL = "http://dsl.gipl.net/";

    public static final String TAG = ApiUtils.class.getSimpleName();

    public ApiUtils() {
    }

    public static APIService getAPIService() {


        Log.e(TAG, "getAPIService: Called-----" + RetrofitClient.getClient(BASE_URL).create(APIService.class));
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
