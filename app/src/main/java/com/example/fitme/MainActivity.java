package com.example.fitme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;


//Firebase + extra stuff added by Brandon

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    final FragmentManager fm = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationMenu;

    // these two are used for generating UUID in the function id() below
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessGoogleFit();

        bottomNavigationMenu = findViewById(R.id.bottomNavigation);

        bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.actionFavorites:
                        fragment = new FavoritesFragment();
                        break;
                    case R.id.actionFoodDiary:
                        fragment = new FoodDiaryFragment();
                        break;
                    case R.id.actionSchedule:
                        fragment = new ScheduleFragment();
                        break;
                    case R.id.actionProfile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.actionHome:
                    default:
                        fragment = new HomeFragment();
                }
                fm.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationMenu.setSelectedItemId(R.id.actionHome);
    }

    //used for fragments/other activities to retrieve the FirebaseFireStore object
    // (avoids creating a new db connection for every fragment/activity)
    public FirebaseFirestore getFS(){
        return db;
    }

    //generate UUID for device if it does not exist (used to identify user), else just return it
    //should be constant per device, but may disappear if application is uninstalled and reinstalled
    //code from https://medium.com/@ssaurel/how-to-retrieve-an-unique-id-to-identify-android-devices-6f99fd5369eb
    public synchronized static String get_uuid(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = java.util.UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    public void changeFragmentFromFragment(Class fragmentClass){
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    //accesses Google Fit data, prompts user for authentication and permissions
    //if there is no current login
    private void accessGoogleFit() {

        //System.out.println("account email: " + account.getAccount());

//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        final long endTime = cal.getTimeInMillis();
//        //cal.add(Calendar.YEAR, -1);
//        long startTime = cal.getTimeInMillis() - 86400000;

        long endTime = System.currentTimeMillis();
        long startTime = endTime - 86400000;

        System.out.println("times for google fit:");
        System.out.println(startTime);
        System.out.println(endTime);


        GoogleSignInAccount account = GoogleSignIn
                .getAccountForExtension(this, fitnessOptions);


        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    1001, // e.g. 1
                    account,
                    fitnessOptions);

            System.out.println("requested permissions");
        }

        //System.out.println(account.getEmail());





        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                //.bucketBySession()
                .build();




        Task<DataReadResponse> response = Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse response) {
                        // Use response data here
                        System.out.println("got fitness data");
                        for (Bucket bucket : response.getBuckets()) {
                            if (bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints().size() > 0)
                                System.out.println(bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());

                        }

                        //TODO: put this step count into exercise history (once per day)
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed to get fitness data");
                        System.out.println(e);
                    }
                });







    }



//    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//
//            long total = 0;
//
//
//            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(account,DataType.TYPE_STEP_COUNT_DELTA);
//            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
//            if (totalResult.getStatus().isSuccess()) {
//                DataSet totalSet = totalResult.getTotal();
//                total = totalSet.isEmpty()
//                        ? 0
//                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
//            } else {
//                Log.w("fit", "There was a problem getting the step count.");
//            }
//
//            Log.i("fit", "Total steps: " + total);
//
//            return null;
//        }
//    }

}