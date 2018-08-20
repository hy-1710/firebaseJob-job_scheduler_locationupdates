package io.gripxtech.locationjobdispatcher.helper;

public class ApiUtils {

    public static final String BASE_URL = "http://jsonplaceholder.typicode.com/";


    public ApiUtils() {
    }

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
