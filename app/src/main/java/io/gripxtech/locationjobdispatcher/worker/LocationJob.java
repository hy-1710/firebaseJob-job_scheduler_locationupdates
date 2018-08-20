package io.gripxtech.locationjobdispatcher.worker;

import android.location.Location;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.concurrent.Callable;

import io.gripxtech.locationjobdispatcher.helper.DataBaseHelper;
import io.gripxtech.locationjobdispatcher.helper.LocationData;
import io.gripxtech.locationjobdispatcher.helper.LocationJobHelper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LocationJob extends JobService {

    public static final String TAG = "worker.LocationJob";
    DataBaseHelper db;
    LocationData locationData;


    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.d("onStartJob: ");
        getLocation(job);
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        Timber.d("onStopJob: ");
        return true;
    }

    private void getLocation(final JobParameters job) {
        new LocationJobHelper(getApplicationContext(), new LocationJobHelper.OnLocationUpdatesListener() {
            @Override
            public void onLocationUpdate(Location location) {
                Timber.d("onLocationUpdate(): location: %s", location);
                databaseOperation(job, location);
            }

            @Override
            public void onLocationError(Exception e) {
                Timber.d("onLocationError(): %s", e.getMessage());
                e.printStackTrace();
                jobFinished(job, true);
            }
        });
    }

    private void databaseOperation(final JobParameters job, final Location location) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Timber.d("call: ");

                db = new DataBaseHelper(LocationJob.this);
                locationData = new LocationData();
                // Perform database operation here
                // this is background thread itself
                // don't start new Thread or AsyncTask here
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String table = locationData.CREATE_TABLE;
                Log.e(TAG, "call: Lat:" + latitude + "Long :" + longitude + "TABLE----" + table);
                long id = db.insertNote(latitude, longitude);
                Log.e(TAG, "call: ID ----" + id);


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
}
