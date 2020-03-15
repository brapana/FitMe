package com.example.fitme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    final FragmentManager fm = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationMenu;

    // these two are used for generating UUID in the function id() below
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessGoogleFit();


        //set alarm to ring in x milliseconds
        //startAlarmBroadcastReceiver(this, 10000);

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
    //Adds the latest google fit walk as a new exercise if it has not been added already
    private void accessGoogleFit() {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                .build();

        long endTime = System.currentTimeMillis();
        //long startTime = endTime - 86400000;
        long startTime = endTime - 864000000;

        System.out.println("times for google fit:");
        System.out.println(startTime);
        System.out.println(endTime);



        GoogleSignInAccount account = GoogleSignIn
                .getAccountForExtension(this, fitnessOptions);



        //get the user's name
        AccountManager am = AccountManager.get(this); // "this" references the current Context

        Account[] accounts = am.getAccounts();


        //displays emails? why?
        System.out.println("Display Name:");

        for (Account acc : accounts) {
            System.out.println(acc.toString());
        }


        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    1001, // e.g. 1
                    account,
                    fitnessOptions);

            System.out.println("requested permissions");
        }


        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_MOVE_MINUTES, DataType.TYPE_MOVE_MINUTES)
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
                        int walked_min = 0;
                        for (Bucket bucket : response.getBuckets()) {
                            if (bucket.getDataSet(DataType.AGGREGATE_MOVE_MINUTES).getDataPoints().size() > 0) {
                                //System.out.println(bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                                System.out.println("FIT TEST");
                                System.out.println(bucket.getDataSet(DataType.AGGREGATE_MOVE_MINUTES).getDataPoints().get(0).getValue(Field.FIELD_DURATION));
                                walked_min = bucket.getDataSet(DataType.AGGREGATE_MOVE_MINUTES).getDataPoints().get(0).getValue(Field.FIELD_DURATION).asInt();
                                break;
                                //System.out.println(bucket.getDataSet(DataType.TYPE_MOVE_MINUTES).getDataPoints().get(0).getValue(Field));
                            }
                        }
                        //try to add fit workout, wont add if walked_min is 0 or workout already was added
                        if (walked_min != 0)
                            addFitWorkout("Google Fit Walk", 4*walked_min, walked_min);

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

    //write performed exercise to database (this one is meant for google fit data only)
    //calories_burned = cal burned per min, time_performed = time in min
    //eventually this should go in chooseworkoutfragment
    public void writeExerciseToDatabase(String exercise_name, int calories_burned, int time_performed) {
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> exercise_history = new HashMap<>();
        Map<String, Object> exercise_info = new HashMap<>();


        String UUID = get_uuid(this);

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date time = Timestamp.now().toDate();

        exercise_info.put("exercise", exercise_name);
        exercise_info.put("calories_burned", calories_burned);
        exercise_info.put("time_performed", time_performed);

        exercise_history.put(date_format.format(time), exercise_info);

        user.put("exercise_history", exercise_history);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote exercise data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    //if google fit workout doesnt exist, adds it to the database as an exercise
    public void addFitWorkout(final String exercise_name, final int calories_burned, final int time_performed){

        final String UUID = get_uuid(this);
        FirebaseFirestore db = getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String, Object> exercise_history = (Map<String, Object>)data.get("exercise_history");

                            Set<String> exercise_keys = exercise_history.keySet();

                            for (String key: exercise_keys){
                                Map<String, Object> exercise_info = (Map<String, Object>)exercise_history.get(key);
                                if (((String)exercise_info.get("exercise")).equals(exercise_name)){
                                    return;
                                }
                            }
                            writeExerciseToDatabase(exercise_name, calories_burned, time_performed);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

    }

    //write name to database
    public void writeNameToDatabase(String name) {
        Map<String, Object> user = new HashMap<>();

        String UUID = get_uuid(this);

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        user.put("name", name);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote exercise data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }


}