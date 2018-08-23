package io.gripxtech.locationjobdispatcher.helper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {


  @Headers({
          "Content-Type: application/soap+xml",
          "Accept-Charset: utf-8"
  })
  @POST("/SyncTracklog")
  Call<List<LocationData>> getTrackLogList(@Body List<LocationData> trackLogList);
  // public ResponseEnvelope uploadRequest(@Body RequestEnvelope body);


  // Call<List<LocationData>> getTrackLogList(@Body List<LocationData> trackLogList );
  // Call<List<LocationData>> savePost(List<LocationData> locatiobDataList);
}
