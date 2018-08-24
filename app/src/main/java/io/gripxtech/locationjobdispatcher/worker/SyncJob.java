package io.gripxtech.locationjobdispatcher.worker;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import io.gripxtech.locationjobdispatcher.helper.APIService;
import io.gripxtech.locationjobdispatcher.helper.DataBaseHelper;
import io.gripxtech.locationjobdispatcher.helper.LocationData;
import io.gripxtech.locationjobdispatcher.helper.SyncData;
import io.gripxtech.locationjobdispatcher.prefs.AppPrefs;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class SyncJob extends JobService {

    public static final String TAG = "worker.SyncJob";

    private static final String SOAP_ACTION = "http://tempuri.org/SyncTracklog";
    private static final String METHOD_NAME = "Tracklog";
    private static final String NAMESPACE = "http://www.w3schools.com/webservices/";


    DataBaseHelper db;
    LocationData locationData;
    LocationData lastLocationData;
    SyncData syncData;

    //main data list for sync Data
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



                //case 1: last data size 0 and db has data then select * from table
                //case 2: last data size > 0 and db have more then data then send the records
                // case 3 : last data size = db.data

                //when there is no data in db then stop the service


                int count = db.getNotesCount();
                int syncCount = db.getSyncCount();
                Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN TRACK TABLE------- : " + count);
                Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN SYNC TABLE------- : " + syncCount);


                if (count > 0) {
                    //in db there are records


                    if (syncCount == 0) {
                        //no records has been sent
                        //locatiobDataList = db.getAllLocation();
                        Log.e(TAG, "call: appPrefs.getLocationId() = 0 and Inside If");

                        lastLocationData = new LocationData();

                        //so add all data into list from localdb
                        locatiobDataList.addAll(db.getAllLocation());


                    } else if (syncCount > 0) {
                        //means some records have been sent to server


                        // few records has been sent
                        int lastLocationId = appPrefs.getLocationId();

                        Log.e(TAG, "call: lastlocationId---- for checking sync " + lastLocationId);
                        lastLocationData = new LocationData();
                        if (lastLocationId > 0) {
                            locatiobDataList.addAll(db.getFromAllLocation(lastLocationId));

                        } else {

                        }


                        if (locatiobDataList.size() > 0) {
                            //arryIndexBound exception
                            lastLocationData = locatiobDataList.get(locatiobDataList.size() - 1);

                        }

                        Log.e(TAG, "call: value of lastLocationData :" + lastLocationData);
/*
                        long id = db.insertSyncData(lastLocationData.getLocationid(), lastLocationData.getUserid(),
                                locatiobDataList.size(), getCurrentTimeUsingDate());*/

                        //   Log.e(TAG, "call: SYNCID ----" + id);
                        Log.e(TAG, "call: LAT CHECKING VALUE :----" + lastLocationData.getLatitude());


                        // appPrefs.setLocationId(lastLocationData.getLocationid());
                 /*       appPrefs.setEmployeeId(lastLocationData.getUserid());
                        appPrefs.setLatitude(lastLocationData.getLatitude());
                        appPrefs.setLongitude(lastLocationData.getLongitude());
                        appPrefs.setTimeStamp(lastLocationData.getTimestamp());
                        appPrefs.setLocationId(lastLocationData.getLocationid());
*/


                    } else if (count == syncCount) {
                        //all records sync

                        Log.e(TAG, "call: NO RECORDS INSOIDE DB for sync :");
                        jobFinished(job, true);
                    } else {
                        jobFinished(job, true);

                    }
                    Log.e(TAG, " appPrefs.setLocationId: " + appPrefs.getLocationId());
                    Log.e(TAG, " appPrefs.setLongitude: " + appPrefs.getLatitude());
                    Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getLongitude());


                } else {
                    // in db no records
                    appPrefs.setLocationId(0);
                    appPrefs.setTimeStamp("");
                    appPrefs.setLongitude(0.0);
                    appPrefs.setLatitude(0.0);
                    appPrefs.setUserId(0);
                    appPrefs.setEmployeeId(0);
                    jobFinished(job, true);
                }

                sendPost(locatiobDataList, job);

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
                //   mAPIService = ApiUtils.getAPIService();
                Log.e(TAG, "onNext: Sync LIST OF TRTACK----" + locationData);
                //  sendPost(locationData, job);
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


    private void sendPost(final List<LocationData> locatiobDataList, final JobParameters job) {
        Log.e(TAG, "sendPost: CALLED locatiobDataList------" + locatiobDataList.size());


        if (locatiobDataList.size() > 0) {

            Log.e(TAG, "sendPost: CHECK Size of Data list for service call---" + locatiobDataList);
            Log.e(TAG, "sendPost: CHEck DATA ---- " + locatiobDataList);


            try {

                //getting JSONArray
                JSONArray jsonArray = new JSONArray();

                for (LocationData data : locatiobDataList) {

                    JSONObject Tracklog = new JSONObject();
                    Tracklog.put("UserId", data.getUserid());
                    Tracklog.put("Latitude", data.getLatitude());
                    Tracklog.put("Longitude", data.getLongitude());
                    Tracklog.put("CreatedOn", data.getTimestamp());

                    jsonArray.put(Tracklog);

                }

                Log.e(TAG, "sendPost: checking value of JSON string :" + jsonArray.toString());


                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("text/xml");
                okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,
                        //  "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <soap:Body>\r\n    <SyncTracklog xmlns=\"http://tempuri.org/\">\r\n      <Tracklog>[{\"UserId\":1,\"Latitude\":23.2228585,\"Longitude\":72.6472104,\"CreatedOn\":\"2018-08-23 12:24:42 PM \"},{\"UserId\":1,\"Latitude\":23.2228585,\"Longitude\":72.6472104,\"CreatedOn\":\"2018-08-23 12:27:16 PM \"}]</Tracklog>\r\n    </SyncTracklog>\r\n  </soap:Body>\r\n</soap:Envelope>");
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <soap:Body>\r\n    <SyncTracklog xmlns=\"http://tempuri.org/\">\r\n      <Tracklog>" + jsonArray.toString() + "</Tracklog>\r\n    </SyncTracklog>\r\n  </soap:Body>\r\n</soap:Envelope>");

                Request request = new Request.Builder()
                        .url("http://dsl.gipl.net/gsplvtsmobile.asmx")
                        .post(body)
                        .addHeader("content-type", "text/xml")
                        .addHeader("soapaction", "http://tempuri.org/SyncTracklog")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "d2ab2773-4ab8-5f6e-984c-ed4bfbc74836")
                        .build();


                try {


                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    okhttp3.Response response = client.newCall(request).execute();

                    Log.e(TAG, "sendPost: REspose send" + response.message().toString());

                    if (response.isSuccessful()) {


                        //add last data in synclog table

                        //case 1: last data size 0 and db has data then select * from table
                        //case 2: last data size > 0 and db have more then data then send the records
                        // case 3 : last data size = db.data

                        //when there is no data in db then stop the service


                        Log.e(TAG, "sendPost: IS Successfull");

                        int count = db.getNotesCount();
                        int syncCount = db.getSyncCount();
                        Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN DB------- : " + count);
                        Log.e(TAG, "call: CHECK TOTAL NUMBERS OF RECORDS IN DB------- : " + syncCount);


                        //if response successfully then insert last send record in SyncLog table for back up
                        lastLocationData = locatiobDataList.get(locatiobDataList.size() - 1);
                        Log.e(TAG, "call: locationDataList size :" + locatiobDataList.size());
                        Log.e(TAG, "call: value of lastLocationData :" + lastLocationData);

                        //insert last record of arrayList in sync table for future reference

                        long id = db.insertSyncData(lastLocationData.getLocationid(), lastLocationData.getUserid(),
                                locatiobDataList.size(), getCurrentTimeUsingDate());


                        Log.e(TAG, "call: SYNCID ----" + id);

                        appPrefs.setLocationId(lastLocationData.getLocationid());
                        appPrefs.setEmployeeId(1);
                        appPrefs.setLatitude(lastLocationData.getLatitude());
                        appPrefs.setLongitude(lastLocationData.getLongitude());
                        appPrefs.setTimeStamp(lastLocationData.getTimestamp());

                        Log.e(TAG, "sendPost: AFTER INSTERING IN SYNCLOG TABLE VALUE IS-------->");
                        Log.e(TAG, " appPrefs.setLocationId: " + appPrefs.getLocationId());
                        Log.e(TAG, " appPrefs.setLongitude: " + appPrefs.getLatitude());
                        Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getLongitude());
                        Log.e(TAG, " appPrefs.setLatitude: " + appPrefs.getEmployeeId());


                    } else {
                        Log.e(TAG, "sendPost: IS not SUCCESSFUL");
                        Log.e(TAG, "sendPost: " + response.message());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }




         /*   RequestEnvelope requestEnvelop = new RequestEnvelope();
            RequestBody requestBody = new RequestBody();
            RequestModel requestModel = new RequestModel();
            requestModel.locationData = locatiobDataList;
            requestModel.cityNameAttribute = "http://tempuri.org/";
            requestBody.getWeatherbyCityName = requestModel;
            requestEnvelop.body = requestBody;

            mAPIService.getTrackLogList(requestBody).enqueue(new Callback<List<LocationData>>() {
            mAPIService.getTrackLogList(locatiobDataList).enqueue(new Callback<List<LocationData>>() {

                @Override
                public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                    Log.e(TAG, "onResponse: " + response.body());
                    //   Log.e(TAG, "onResponse: CALLED----" + response.body().toString());

                    if (response.isSuccessful()) {

                    }
                }

                @Override
                public void onFailure(Call<List<LocationData>> call, Throwable t) {

                    Log.e(TAG, "onFailure: Called---");
                    jobFinished(job, true);
                }
            });

        }*/


        }
    }

    private void xmlserviceCall() {
        /*String URL = "http://dsl.gipl.net/gsplvtsmobile.asmx";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("TrackLog", locatiobDataList); // adding method property here serially

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.implicitTypes = true;
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        httpTransport.debug = true;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
        } catch (HttpResponseException e) {
            // TODO Auto-generated catch block
            Log.e("HTTPLOG", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("IOLOG", e.getMessage());
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            Log.e("XMLLOG", e.getMessage());
            e.printStackTrace();
        } //send request

        Object result = null;
        try {
            result = (Object) envelope.getResponse();
            Log.i("RESPONSE", String.valueOf(result)); // see output in the console
        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            Log.e("SOAPLOG", e.getMessage());
            e.printStackTrace();*/

        // return null;
    }


}

