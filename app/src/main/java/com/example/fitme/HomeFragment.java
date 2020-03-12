package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private ImageView btnEditCalorieGoal;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        writeToDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData(view, savedInstanceState);

        btnAddFood = getActivity().findViewById(R.id.btnAddFood);
        btnEditCalorieGoal = getActivity().findViewById(R.id.btnEditCalorieGoal);
        btnStartWorkout = getActivity().findViewById(R.id.btnStart);

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

                            //update the viewable text with the values from FireBase
//                            ((TextView)view.findViewById(R.id.name)).setText((String)data.get("name"));
//
                              ((TextView)view.findViewById(R.id.caloriesConsumed)).setText(String.format("%d cal", totalCal));
//
//                            ((TextView)view.findViewById(R.id.gender)).setText((String)gender);
//
//                            ((TextView)view.findViewById(R.id.height)).setText(String.format("%d in.", height));
//
//                            ((TextView)view.findViewById(R.id.weight)).setText(String.format("%d lbs.", weight));


                            //System.out.println("Successfully loaded data for profile view from Firestore");

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
