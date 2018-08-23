package io.gripxtech.locationjobdispatcher.worker;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import io.gripxtech.locationjobdispatcher.helper.APIService;
import io.gripxtech.locationjobdispatcher.helper.ApiUtils;
import io.gripxtech.locationjobdispatcher.helper.DataBaseHelper;
import io.gripxtech.locationjobdispatcher.helper.LocationData;
import io.gripxtech.locationjobdispatcher.helper.SyncData;
import io.gripxtech.locationjobdispatcher.prefs.AppPrefs;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SyncJob extends JobService {

    public static final String TAG = "worker.SyncJob";
    DataBaseHelper db;
    LocationData locationData;
    LocationData lastLocationData;
    SyncData syncData;

    List<LocationData> locatiobDataList = new ArrayList<>();
    AppPrefs appPrefs;
    int countData;
    private APIService mAPIService;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.d("onStartJob: ");
        databaseOperation(job);
        //getLocation(job);
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        Timber.d("onStopJob: ");
        return true;
    }

    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss a ";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Log.e(TAG, "getCurrentTimeUsingDate: dateFormat" + dateFormat);
        String formattedDate = dateFormat.format(date);

        Log.e(TAG, "Current time of the day using Date - 12 hour format:  " + formattedDate);

        return formattedDate;


    }

    private void databaseOperation(final JobParameters job) {
        Observable.fromCallable(new Callable<List<LocationData>>() {
            @Override
            public List<LocationData> call() throws Exception {
                Timber.d("call: ");
                // Perform database operation here
                // this is background thread itself
                // don't start new Thread or AsyncTask here


                db = new DataBaseHelper(SyncJob.this);
                appPrefs = new AppPrefs(SyncJob.this);
                locationData = new LocationData();

                lastLocationData = new LocationData();


                getCurrentTimeUsingDate();
                // getCurrentTimeUsingCalendar();


                //case 1: last data size 0 and db has data then select * from table
                //case 2: last data size > 0 and db have more then data then send the records
                // case 3 : last data size = db.data


                int count = db.getNotesCount();
                int syncCount = db.getSyncCount();
                Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN DB------- : " + count);


                if (count > 0) {
                    //in db there are records


                    // if (appPrefs.getLocationId() == 0) {
                    if (syncCount == 0) {
                        //no records has been sent
                        //locatiobDataList = db.getAllLocation();
                        Log.e(TAG, "call: appPrefs.getLocationId() = 0 and Inside If");


                        //so add all data into list from localdb
                        locatiobDataList.addAll(db.getAllLocation());

                        //check last location record
                        lastLocationData = locatiobDataList.get(locatiobDataList.size() - 1);
                        Log.e(TAG, "call: locationDataList size :" + locatiobDataList.size());
                        Log.e(TAG, "call: value of lastLocationData :" + lastLocationData);

                        //insert last record of arrayList in sync table for future reference

                        long id = db.insertSyncData(lastLocationData.getLocationid(), lastLocationData.getUserid(),
                                locatiobDataList.size(), getCurrentTimeUsingDate());


                        Log.e(TAG, "call: SYNCID ----" + id);

                        Log.e(TAG, "call: LAT CHECKING VALUE :----" + lastLocationData.getLatitude());


                        appPrefs.setLocationId(lastLocationData.getLocationid());
                        appPrefs.setEmployeeId(1);
                        appPrefs.setLatitude(lastLocationData.getLatitude());
                        appPrefs.setLongitude(lastLocationData.getLongitude());
                        appPrefs.setTimeStamp(lastLocationData.getTimestamp());

                        Log.e(TAG, " appPrefs.setLocationId: " + appPrefs.getLocationId());
                        Log.e(TAG, " appPrefs.setLongitude: " + appPrefs.getLatitude());
                        Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getLongitude());


                        // } else if (appPrefs.getLocationId() > 0) {
                    } else if (syncCount > 0) {
                        //means some records have been sent to server

                        Log.e(TAG, "call: appPrefs.getLocationId() > 0 and Inside If");
                        // few records has been sent
                        int lastLocationId = appPrefs.getLocationId();
                        locatiobDataList.addAll(db.getFromAllLocation(lastLocationId));
                        lastLocationData = locatiobDataList.get(locatiobDataList.size() - 1);
                        Log.e(TAG, "call: locationDataList size :" + locatiobDataList.size());
                        Log.e(TAG, "call: value of lastLocationData :" + lastLocationData);

                        long id = db.insertSyncData(lastLocationData.getLocationid(), lastLocationData.getUserid(),
                                locatiobDataList.size(), getCurrentTimeUsingDate());

                        Log.e(TAG, "call: SYNCID ----" + id);
                        Log.e(TAG, "call: LAT CHECKING VALUE :----" + lastLocationData.getLatitude());


                        appPrefs.setLocationId(lastLocationData.getLocationid());
                        appPrefs.setEmployeeId(lastLocationData.getUserid());
                        appPrefs.setLatitude(lastLocationData.getLatitude());
                        appPrefs.setLongitude(lastLocationData.getLongitude());
                        appPrefs.setTimeStamp(lastLocationData.getTimestamp());

                        Log.e(TAG, " appPrefs.setLocationId: " + appPrefs.getLocationId());
                        Log.e(TAG, " appPrefs.setLongitude: " + appPrefs.getLatitude());
                        Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getLongitude());


                    } else {
                        //do nothing

                        Log.e(TAG, "call: NO RECORDS INSOIDE DB :");
                    }


                } else {
                    // in db no records
                    appPrefs.setLocationId(0);
                    appPrefs.setTimeStamp("");
                    appPrefs.setLongitude(0.0);
                    appPrefs.setLatitude(0.0);
                    appPrefs.setUserId(0);
                    appPrefs.setEmployeeId(0);
                }


                return locatiobDataList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<LocationData>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Timber.d("onSubscribe: ");
            }

            @Override
            public void onNext(List<LocationData> locationData) {
                Timber.d("onNext: ");
                //call here retrofit to send the data
                mAPIService = ApiUtils.getAPIService();
                Log.e(TAG, "onNext: Sync LIST OF TRTACK----" + locationData);
                sendPost(locationData, job);
            }


            @Override
            public void onError(Throwable e) {
                Timber.d("onError: ");
                e.printStackTrace();
                e.getMessage();
                jobFinished(job, true);
            }

            @Override
            public void onComplete() {
                Timber.d("onComplete: ");
                // jobFinished(job, true);
            }
        });


    }

    public void showResponse(String response) {
        //do whatever whould like to do
       /* if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);*/
    }


   /* public String CallGetImeiNO(JSONObject obj) {
        String s = "";
        String responseBody = "";

        HttpParams myParams = new BasicHttpParams();
        //HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        //  HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams);
        String jsn = obj.toString();

        try {
            boolean UrlExist=true;
            //  boolean UrlExist = exists(URLS.toString());
            if(UrlExist) {

                HttpPost httppost = new HttpPost(URL.toString());
                httppost.setHeader("Content-type", "application/json");
                Log.d("MyApp", "I am here1");
                StringEntity se = new StringEntity(obj.toString());
                Log.d("MyApp", "I am here2");
                Log.d("IMEI", se.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                Log.d("MyApp", "I am here3");
                httppost.setEntity(se);
                Log.d("MyApp", "I am here4");

                HttpResponse response = httpclient.execute(httppost);
                Log.d("MyApp", "I am here5");

                String temp = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(temp);
                //   JSONObject d = jsonObject.getJSONObject("d")JSONArray jsonArray = jsonObject.optJSONArray("d");
                Log.d("MyApp", "I am here6");
                Log.d("tag", temp);
                Log.d(TAG, response.toString());
                Log.d("MyApp", "I am here7");
                s = temp;

            }
            else{
                s=null;
                Log.d("URL", "URL not found");
            }
        } catch (Exception e) {
            System.out.println(e);
            s=null;
        }
        return s;
    }
*/

    private void sendPost(final List<LocationData> locatiobDataList, final JobParameters job) {
        Log.e(TAG, "sendPost: CALLED locatiobDataList------" + locatiobDataList.size());

      /*  mAPIService.savePost(locatiobDataList).enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                Log.e(TAG, "onResponse: INSIDE .....");

                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                    Log.e(TAG, "onResponse: INSIDE IF" );

                    jobFinished(job, true);
                }
                Log.e(TAG, "onResponse: OUTSIDE IF" );

            }

            @Override
            public void onFailure(Call<List<LocationData>> call, Throwable t) {

                Log.e(TAG, "Unable to submit post to API.");
                jobFinished(job, true);
                Log.e(TAG, "onFailure: CALLED" );

            }
        });*/

        if (locatiobDataList.size() > 0) {
            mAPIService.getTrackLogList(locatiobDataList).enqueue(new Callback<List<LocationData>>() {
                @Override
                public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                    Log.e(TAG, "onResponse: CALLED----" + response.body().toString());
                }

                @Override
                public void onFailure(Call<List<LocationData>> call, Throwable t) {

                    Log.e(TAG, "onFailure: Called---");
                    jobFinished(job, true);
                }
            });

        }


    }

}
