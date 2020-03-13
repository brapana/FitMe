package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
public class ChooseWorkoutFragment extends Fragment {
    //The user will be taken to this page once they click start workout to select a workout
    public interface ClickListener {
        void onPositionClicked(int position);
    }

    //private ArrayList<ArrayList<String>> recommendedList;
    private ClickListener listener;
    private RecommendedWorkoutsAdapter adapter;
    private RecyclerView rvRecommendedWorkouts;

    // FOR SHOWING LIST OF RECOMMENDED EXERCISES FOR THE USER TO CHOOSE FROM
    // GENERATED IN calcWorkouts
    //arraylist of String arraylists with each inner arraylist being [delta (lower values=better reccommendation), workout name, minutes_performed, calories burned (minutes performed * cal burned/min)]
    private ArrayList<ArrayList<String>> recommendedList;

    public ChooseWorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRecommendedWorkouts = view.findViewById(R.id.rvRecommendedWorkouts);
        recommendedList = new ArrayList<ArrayList<String>>();
        listener = new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }
        };
        adapter = new RecommendedWorkoutsAdapter(getContext(), recommendedList, listener);
        LinearLayoutManager layoutManagerB = new LinearLayoutManager(getContext());
        rvRecommendedWorkouts.setLayoutManager(layoutManagerB);
        rvRecommendedWorkouts.setAdapter(adapter);

        //TODO Brandon: le recommended workouts recommendedList
        //System.out.println((int)loadWorkoutMin());
        //the below function calls calcWorkouts after its done
        loadWorkoutMin(view, savedInstanceState);

    }

    //loads workout_min from database
    public long loadWorkoutMin(final View view, @Nullable final Bundle savedInstanceState){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        final long[] workout_min = {0};

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            workout_min[0] = (long)data.get("workout_min");

                            System.out.println((long)data.get("workout_min"));

                            calcWorkouts(view, savedInstanceState, (int)workout_min[0]);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
        return workout_min[0];
    }

    //Calculates list of recommended workouts sorted by their closeness to the remaining calories
    //arraylist of String arraylists with each inner arraylist being [delta (lower values=better reccommendation), workout name, minutes_performed, calories burned (minutes performed * cal burned/min)]
    // arraylist sorted by delta ascending
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



                            //long calories_rem = Integer.parseInt(((TextView)view.findViewById(R.id.caloriesRemainingHome)).getText().toString().split(" ")[0]);

                            long calories_rem = HomeFragment.getCalRem();

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
                                workout_item.add(Integer.toString(minutes));
                                double cal_per_min= (double) fav_exercises.get((String)workouts.get(key));
                                workout_item.add(Double.toString(cal_per_min * minutes));

                                recommendedList.add(workout_item);

                            }

                            System.out.println("Successfully loaded data to arraylist for recommended workouts");
                            System.out.println("recommended workouts list:");
                            for (ArrayList<String> item : recommendedList){
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


    }
}
