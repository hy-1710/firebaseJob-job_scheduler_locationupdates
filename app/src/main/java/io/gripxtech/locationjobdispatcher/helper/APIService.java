package io.gripxtech.locationjobdispatcher.helper;

import java.util.List;

import io.gripxtech.locationjobdispatcher.webservice.request.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

  @Headers({

          //  "soapaction : http://tempuri.org/SyncTracklog",
          "content-type: text/json; charset=UTF-8",
          // "Accept-Charset: utf-8"
  })

  @POST("/gsplvtsmobile.asmx/SyncTrackLog")


    // Call<List<LocationData>> getTrackLogList(@Body List<LocationData> trackLogList);
  Call<List<LocationData>> getTrackLogList(@Body RequestBody requestBody);


}
