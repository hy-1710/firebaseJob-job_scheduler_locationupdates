package io.gripxtech.locationjobdispatcher;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.gripxtech.locationjobdispatcher.databinding.ActivityMainBinding;
import io.gripxtech.locationjobdispatcher.helper.LocationActivityHelper;
import io.gripxtech.locationjobdispatcher.worker.LocationJob;
import io.gripxtech.locationjobdispatcher.worker.SyncJob;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    ActivityMainBinding binding;

    LocationActivityHelper locationActivityHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Get Location
        locationActivityHelper = new LocationActivityHelper(this, new LocationActivityHelper.OnLocationUpdatesListener() {
            @Override
            public void onLocationUpdate(Location location) {
                if (location != null) {
                    Log.e(TAG, "onLocationUpdate: " + location);
                    Timber.i("location is %s", location);
                    binding.tvLocation.setText("Lat is :" + location.getLatitude() + "Long ia : " + location.getLongitude());
                    binding.tvNextLocation.setText("Next Lat is :" + location.getLatitude() + "Long ia : " + location.getLongitude());

                }
            }

            @Override
            public void onLocationError(Exception e) {
                e.printStackTrace();
                Utils.showMessage(MainActivity.this, e.getMessage());
            }
        });

        locationActivityHelper.create();

        // Start Firebase Job
        scheduleLocationJob();
        //start sync job
        scheduleSyncJob();
    }

    private void scheduleSyncJob() {
        Log.e(TAG, "scheduleSyncJob: Called-----");

        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        // Passing Job Parameter
        // Bundle extras = new Bundle();
        // extras.putString("aString", "aString");
        // extras.putInt("aInt", 1);
        // extras.putDouble("aDouble", 2.0);

        Job syncJob = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)

                //the JobService that will be called
                .setService(SyncJob.class)
                //.setExtras(extras)

                //uniquely identifies the job
                .setTag(SyncJob.TAG)

                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)

                // We are mentioning that the job is periodic.
                .setRecurring(true)

                // Run between 10 - 12 minutes from now.
                .setTrigger(Trigger.executionWindow(7 * 60, 9 * 60))
                //.setTrigger(Trigger.executionWindow(30, 60))

                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)

                .build();

        dispatcher.mustSchedule(syncJob);
    }

    @Override
    protected void onPause() {
        // Remove location updates to save battery.
        locationActivityHelper.pause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        locationActivityHelper.resume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationActivityHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationActivityHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void scheduleLocationJob() {
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        // Passing Job Parameter
        // Bundle extras = new Bundle();
        // extras.putString("aString", "aString");
        // extras.putInt("aInt", 1);
        // extras.putDouble("aDouble", 2.0);

        Job locationJob = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)

                //the JobService that will be called
                .setService(LocationJob.class)
                //.setExtras(extras)

                //uniquely identifies the job
                .setTag(LocationJob.TAG)

                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)

                // We are mentioning that the job is periodic.
                .setRecurring(true)

                // Run between 10 - 12 minutes from now.
                .setTrigger(Trigger.executionWindow(5 * 60, 7 * 60))
                //.setTrigger(Trigger.executionWindow(30, 60))

                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)

                .build();

        dispatcher.mustSchedule(locationJob);
    }
}
