package com.example.fitme;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private Button btnAddFood;
    private Button btnStartWorkout;
    private TextView etCalorieGoal;
    private TextView etTimeDuration;
    private ImageView btnEditCalorieGoal;//opens popup dialog
    private Dialog dialog;

    //arraylist of String arraylists with the following structure:
    // [timestamp, exercise_name, calories burned/min, min performed]
    // arraylist sorted by timestamp descending
    protected ArrayList<ArrayList<String>> eList = new ArrayList<ArrayList<String>>();

    //arraylist of String arraylists with each inner arralist being [delta (lower values=better reccommendation), workout name, calories burned (over x minutes)]
    protected ArrayList<ArrayList<String>> rec_workouts = new ArrayList<ArrayList<String>>();


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        writeToDatabase();

        //UNCOMMENTED THIS AFTER YOU HAVE RAN THE APP ONCE TO MAKE DATABASE OR ELSE
        //YOU MIGHT GET null keyset() ERRORS AND HAVE TO DELETE YOUR DEVICE's UUID Firestore document
//        //dummy data for exercise_history
//        String exercise_name = "running";
//        int calories_burned = 100;
//        int time_performed = 30;

        //writeExerciseToDatabase(exercise_name, calories_burned, time_performed);

        queryExercises();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData(view, savedInstanceState);

        calcWorkouts(view, savedInstanceState, 30);

        btnAddFood = getActivity().findViewById(R.id.btnAddFood);
        etCalorieGoal = getActivity().findViewById(R.id.calorieGoal);
        etTimeDuration = getActivity().findViewById(R.id.timeDuration);
        btnEditCalorieGoal = getActivity().findViewById(R.id.btnEditCalorieGoal);
        btnStartWorkout = getActivity().findViewById(R.id.btnStart);
        dialog = new Dialog(getActivity());
        final View pass_view = view;

        btnStartWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragmentFromFragment(ChooseWorkoutFragment.class);
            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationMenu;
                bottomNavigationMenu = getActivity().findViewById(R.id.bottomNavigation);
                bottomNavigationMenu.setSelectedItemId(R.id.actionFoodDiary);
                ((MainActivity) getActivity()).changeFragmentFromFragment(AddFoodFragment.class);
            }
        });

        btnEditCalorieGoal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.pop_up_goal);
                final Button btnCancel = dialog.findViewById(R.id.btnCancelDuration);
                final Button btnSubmitNewGoal = dialog.findViewById(R.id.btnSubmitNewDuration);
                final EditText etNewCalorieGoal = dialog.findViewById(R.id.etNewTimeDuration);

                btnSubmitNewGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etNewCalorieGoal.getText().toString().equals("") || Integer.parseInt(etNewCalorieGoal.getText().toString()) == 0){
                            Toast.makeText(getActivity().getApplicationContext(), "Calorie goal cannot be empty or 0! :)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //write new calorie goal to database, then refresh all values
                        writeCalorieGoalToDatabase(Integer.parseInt(etNewCalorieGoal.getText().toString()));
                        loadData(pass_view, savedInstanceState);

                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        etTimeDuration.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.pop_up_duration);
                final Button btnCancel = dialog.findViewById(R.id.btnCancelDuration);
                final Button btnSubmitNewGoal = dialog.findViewById(R.id.btnSubmitNewDuration);
                final EditText etNewDuration = dialog.findViewById(R.id.etNewTimeDuration);

                btnSubmitNewGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etNewDuration.getText().toString().equals("") || Integer.parseInt(etNewDuration.getText().toString()) <= 0){
                            Toast.makeText(getActivity().getApplicationContext(), "Time can't be empty or less than 0! :)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //TODO Brandon: update time duration in db wherever needed

                        etTimeDuration.setText(etNewDuration.getText().toString()+ " min");
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

    }
    //writes some dummy data to the Firestore db under the current UUID (this should show up in profile view)
    //data structure is users->{device UUID generated in MainActivity.java}->{data}
    //if the user already exists in the database, it does nothing.
    public void writeToDatabase() {
        // Create a new user object
        final Map<String, Object> user = new HashMap<>();
        Map<String, Object> fav_exercises = new HashMap<>();
        Map<String, Object> food_history = new HashMap<>();
        Map<String, Object> exercise_history = new HashMap<>();
        user.put("age", 21);
        user.put("gender", "male");
        user.put("height", 72);
        user.put("weight", 150);
        user.put("name", "Johnathon Wickeston");
        user.put("daily_calorie_goal", 2000);

        //calculated based on info from https://www.health.harvard.edu/diet-and-weight-loss/calories-burned-in-30-minutes-of-leisure-and-routine-activities
        //{name of exercise}, {cal burned/min}
        fav_exercises.put("Weight Lifting: general", 3.733333333);
        fav_exercises.put("Aerobics: water", 4.966666667);
        fav_exercises.put("Stretching, Yoga", 4.966666667);
        fav_exercises.put("Calisthenics: moderate", 5.566666667);
        fav_exercises.put("Riders: general", 6.2);
        fav_exercises.put("Aerobics: low impact", 6.833333333);
        fav_exercises.put("Stair Step Machine: general", 7.433333333);
        fav_exercises.put("Teaching aerobics", 7.433333333);
        fav_exercises.put("Weight Lifting: vigorous", 7.433333333);
        fav_exercises.put("Aerobics, Step: low impact", 8.666666667);
        fav_exercises.put("Aerobics: high impact", 8.666666667);
        fav_exercises.put("Bicycling, Stationary: moderate", 8.666666667);
        fav_exercises.put("Rowing, Stationary: moderate", 8.666666667);
        fav_exercises.put("Calisthenics: vigorous", 9.933333333);
        fav_exercises.put("Circuit Training: general", 9.933333333);
        fav_exercises.put("Rowing, Stationary: vigorous", 10.53333333);
        fav_exercises.put("Elliptical Trainer: general", 11.16666667);
        fav_exercises.put("Ski Machine: general", 11.76666667);
        fav_exercises.put("Aerobics, Step: high impact", 12.4);
        fav_exercises.put("Bicycling, Stationary: vigorous", 13.03333333);



        user.put("fav_exercises", fav_exercises);
        user.put("exercise_history", exercise_history);
        user.put("food_history", food_history);

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        final FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists())
                            System.out.println("User data already exists.");
                        //set document with key of the current device's UUID
                        else{
                                db.collection("users").document(UUID)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("New user, creating dummy collection.");
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);

                    }
                });




    }

    //loads data from the FireStore db into the home strings displayed in the app
    public void loadData(final View view, @Nullable Bundle savedInstanceState){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String,Object> food_history = (Map<String,Object>)data.get("food_history");

                            Set<String> keys = food_history.keySet();


                            int totalCal = 0;

                            //calculate the total calories of all logged food items from the current day
                            for (String food_date_string : keys){

                                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

                                Date food_date = null;

                                try {
                                    food_date = date_format.parse(food_date_string);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String now_string = date_format.format(Timestamp.now().toDate());

                                Date now = null;

                                try {
                                    now = date_format.parse(now_string);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (food_date.equals(now)){
                                    Map<String,Object> food = (Map<String,Object>)food_history.get(food_date_string);


                                    totalCal += (long)food.get("calories");
                                }

                            }


                            Map<String,Object> exercise_history = (Map<String,Object>)data.get("exercise_history");

                            Set<String> exercise_keys = exercise_history.keySet();

                            int totalCalBurned = 0;

                            //calculate the total calories of all logged food items from the current day
                            for (String exercise_date_string : exercise_keys){

                                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

                                Date exercise_date = null;

                                try {
                                    exercise_date = date_format.parse(exercise_date_string);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String now_string = date_format.format(Timestamp.now().toDate());

                                Date now = null;

                                try {
                                    now = date_format.parse(now_string);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (exercise_date.equals(now)){
                                    Map<String, Object> exercise = (Map<String,Object>)exercise_history.get(exercise_date_string);


                                    totalCalBurned += (long)exercise.get("calories_burned");
                                }

                            }

                            //update the viewable text with the values from FireBase

                            long calorieGoal = (long)data.get("daily_calorie_goal");

                            ((TextView)view.findViewById(R.id.calorieGoal)).setText(String.format("%d cal", calorieGoal));

                            ((TextView)view.findViewById(R.id.caloriesConsumedHome)).setText(String.format("%d cal", totalCal));

                            ((TextView)view.findViewById(R.id.caloriesBurned)).setText(String.format("%d cal", totalCalBurned));

                            long caloriesRemaining = totalCal-totalCalBurned;

                            ((TextView)view.findViewById(R.id.caloriesRemainingHome)).setText(String.format("%d cal", caloriesRemaining));



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




    //sets arraylist of String arraylists with the following structure:
    // [timestamp, exercise_name, calories burned/min, min performed]
    // arraylist sorted by timestamp descending
    //uses exercise_history Firestore document in order to populate arraylist
    private void queryExercises(){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String, Object> exercise_history = (Map<String, Object>)data.get("exercise_history");

                            Set<String> keys = exercise_history.keySet();


                            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

                            ArrayList<Date> keys_as_dates = new ArrayList<Date>();

                            Date key_as_date = new Date();

                            //convert list of keys to Date objects (so they can be sorted)
                            for (String key: keys){
                                try {
                                    key_as_date = date_format.parse(key);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                keys_as_dates.add(key_as_date);
                            }

                            Collections.sort(keys_as_dates, Collections.reverseOrder());


                            //arraylists of String arraylists with the following structure: [timestamp, food_name, calories]
                            //each outer arraylist responsible for a particular type of food (meal_time)
                            // each arraylist will be sorted by timestamp descending
                            eList = new ArrayList<ArrayList<String>>();



                            //loop over now sorted date keys and place the objects into the correct arraylist
                            for (Date key : keys_as_dates){

                                String key_string = date_format.format(key);

                                Map<String, Object> exercise_info = (Map<String, Object>)exercise_history.get(key_string);

                                //declaring inner array to be added
                                ArrayList<String> exercise_item = new ArrayList<String>();

                                exercise_item.add(key_string);
                                exercise_item.add((String)exercise_info.get("exercise"));
                                exercise_item.add(String.valueOf(exercise_info.get("calories_burned")));
                                exercise_item.add(String.valueOf(exercise_info.get("time_performed")));

                                eList.add(exercise_item);


                            }


                            System.out.println("Successfully loaded data to arraylist for exercise history from Firestore");
                            System.out.println("exercise items:");
                            for (ArrayList<String> item : eList){
                                System.out.println(item.get(0) + " " + item.get(1) + " " + item.get(2) + " " + item.get(3));
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

        //TODO: adapter stuff here? @Marissa

    }

    //write new calorie goal to the database
    public void writeCalorieGoalToDatabase(int calorieGoal) {
        Map<String, Object> user = new HashMap<>();


        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        user.put("daily_calorie_goal", calorieGoal);

        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote new calorie goal to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }


    //write performed exercise to database
    //calories_burned = cal burned per min, time_performed = time in min
    //eventually this should go in chooseworkoutfragment
    public void writeExerciseToDatabase(String exercise_name, int calories_burned, int time_performed) {
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> exercise_history = new HashMap<>();
        Map<String, Object> exercise_info = new HashMap<>();


        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

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

    //Calculates list of recommended workouts sorted by their closeness to the remaining calories
    public void calcWorkouts(final View view, @Nullable Bundle savedInstanceState, final int minutes){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();




        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String,Object> fav_exercises = (Map<String,Object>)data.get("fav_exercises");

                            Set<String> keys = fav_exercises.keySet();





                            long calories_rem = Integer.parseInt(((TextView)view.findViewById(R.id.caloriesRemainingHome)).getText().toString().split(" ")[0]);

                            //find the exercise that most closely reaches the remaining calories for
                            //the given amount of minutes

                            Map<Double, String> workouts = new HashMap<Double, String>();

                            for (String key : keys){



                               double calories_burned = (double)fav_exercises.get(key);

                               double delta = Math.abs(calories_rem-(calories_burned*minutes));

                               workouts.put(delta, key);



                            }



                            Set<Double> workouts_keys = workouts.keySet();

                            ArrayList<Double> workout_keys = new ArrayList<Double>();

                            for (Double key: workouts_keys){
                                workout_keys.add(key);
                            }

                            Collections.sort(workout_keys);

                            for (Double key : workout_keys){
                                ArrayList<String> workout_item = new ArrayList<String>();

                                workout_item.add(Double.toString(key));
                                workout_item.add(workouts.get(key));
                                //TODO: add calories burned total here

                                rec_workouts.add(workout_item);

                            }



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


}
