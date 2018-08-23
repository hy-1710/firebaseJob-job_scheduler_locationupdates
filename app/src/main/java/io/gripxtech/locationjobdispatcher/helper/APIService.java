package io.gripxtech.locationjobdispatcher.helper;

import java.util.List;

import io.gripxtech.locationjobdispatcher.webservice.request.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

  @Headers({

          "SOAPAction : http://tempuri.org/SyncTracklog",
          "Content-Type: text/xml; charset=UTF-8",
          // "Accept-Charset: utf-8"
  })

  @POST("/gsplvtsmobile.asmx")
    // @POST(" /gsplvtsmobile.asmx")
//  Call<List<LocationData>> getTrackLogList(@Body String checking);
    // Call<List<LocationData>> getTrackLogList(@Body List<LocationData> trackLogList);
  Call<List<LocationData>> getTrackLogList(@Body RequestBody requestBody);
  // public ResponseEnvelope uploadRequest(@Body RequestEnvelope body);


  // Call<List<LocationData>> getTrackLogList(@Body List<LocationData> trackLogList );
  // Call<List<LocationData>> savePost(List<LocationData> locatiobDataList);

  // if this wont work then check headers value

}
