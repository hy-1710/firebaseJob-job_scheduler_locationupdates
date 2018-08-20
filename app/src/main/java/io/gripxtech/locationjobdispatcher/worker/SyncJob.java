package io.gripxtech.locationjobdispatcher.worker;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.gripxtech.locationjobdispatcher.helper.APIService;
import io.gripxtech.locationjobdispatcher.helper.ApiUtils;
import io.gripxtech.locationjobdispatcher.helper.DataBaseHelper;
import io.gripxtech.locationjobdispatcher.helper.LocationData;
import io.gripxtech.locationjobdispatcher.prefs.AppPrefs;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;
import timber.log.Timber;

public class SyncJob extends JobService {

    public static final String TAG = "worker.SyncJob";
    DataBaseHelper db;
    LocationData locationData;
    LocationData lastLocationData;

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


    private void databaseOperation(final JobParameters job) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Timber.d("call: ");
                // Perform database operation here
                // this is background thread itself
                // don't start new Thread or AsyncTask here


                db = new DataBaseHelper(SyncJob.this);
                locationData = new LocationData();
                lastLocationData = new LocationData();


                //case 1: last data size 0 and db has data then select * from table
                //case 2: last data size > 0 and db have more then data then send the records
                // case 3 : last data size = db.data


                int count = db.getNotesCount();
                Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN DB------- : " + count);
                if (count > 0) {
                    //in db there are records

                    if (appPrefs.getLocationId() == 0) {
                        //no records has been sent
                        //locatiobDataList = db.getAllLocation();

                        locatiobDataList.addAll(db.getAllLocation());


                    } else if (appPrefs.getLocationId() > 0) {
                        // few records has been sent
                        int lastLocationId = appPrefs.getLocationId();
                        locatiobDataList.addAll(db.getFromAllLocation(lastLocationId));


                    } else {
                        //do nothing

                    }

                    lastLocationData = locatiobDataList.get(locatiobDataList.size() - 1);

                    appPrefs.setLocationId(lastLocationData.getLocationid());
                    appPrefs.setEmployeeId(1);
                    appPrefs.setLatitude(lastLocationData.getLatitude());
                    appPrefs.setLongitude(lastLocationData.getLongitude());
                    appPrefs.setTimeStamp(lastLocationData.getTimestamp());

                    Log.e(TAG, " appPrefs.setLocationId: " + appPrefs.getLocationId());
                    Log.e(TAG, " appPrefs.setLongitude: " + appPrefs.getLatitude());
                    Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getLongitude());


                } else {
                    // in db no records
                    appPrefs.setLocationId(0);
                    appPrefs.setTimeStamp("");
                    appPrefs.setLongitude(0.0);
                    appPrefs.setLongitude(0.0);
                    appPrefs.setUserId(0);
                    appPrefs.setEmployeeId(0);
                }


                appPrefs = new AppPrefs(SyncJob.this);

                Log.e(TAG, "call: location size--- : " + locatiobDataList.size());

                if (locatiobDataList.size() > 0) {

                } else {
                    if (db.getAllLocation().size() > 0) {
                        //data is in db but not add in List
                        String title = "";
                        String body = "";
                        mAPIService = ApiUtils.getAPIService();

                        sendPost(title, body, job);


                    } else {
                        //there is no data inside
                    }
                }

                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                Timber.d("onSubscribe: ");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Timber.d("onNext: ");
                //call here retrofit to send the data


            }

            @Override
            public void onError(Throwable e) {
                Timber.d("onError: ");
                e.printStackTrace();
                jobFinished(job, true);
            }

            @Override
            public void onComplete() {
                Timber.d("onComplete: ");
                jobFinished(job, true);
            }
        });
    }


    private void sendPost(String title, String body, final JobParameters job) {

        mAPIService.savePost(title, body, 1).enqueue(new Callback<POST>() {
            @Override
            public void onResponse(Call<POST> call, Response<POST> response) {

                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());

                    jobFinished(job, true);
                }

            }

            @Override
            public void onFailure(Call<POST> call, Throwable t) {

                Log.e(TAG, "Unable to submit post to API.");
                jobFinished(job, true);

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
}
