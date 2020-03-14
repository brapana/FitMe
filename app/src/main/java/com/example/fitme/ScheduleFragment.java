package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    public interface ClickListener {
        void onPositionClicked(int position);
    }

    private Button btnAddtoCalendar;
    //private ArrayList<ArrayList<String>> workoutHistoryList;
    private RecyclerView rvSchedule;
    private ScheduleAdapter adapter;
    private ClickListener listener;

    // FOR SHOWING HISTORY OF EXERCISES PERFORMED
    // GENERATED IN queryExercises()
    //arraylist of String arraylists with the following structure:
    // [timestamp, exercise_name, calories burned/min, min performed]
    // arraylist sorted by timestamp descending
    private ArrayList<ArrayList<String>> workoutHistoryList;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddtoCalendar = getActivity().findViewById(R.id.addToCalendar);
        rvSchedule = getActivity().findViewById(R.id.rvSchedule);
        listener = new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }
        };
        workoutHistoryList = new ArrayList<ArrayList<String>>();

        adapter = new ScheduleAdapter(getContext(), workoutHistoryList, listener);
        LinearLayoutManager layoutManagerB = new LinearLayoutManager(getContext());
        rvSchedule.setLayoutManager(layoutManagerB);
        rvSchedule.setAdapter(adapter);

        btnAddtoCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragmentFromFragment(AddEventFragment.class);
            }
        });
        queryExercises();
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
                            workoutHistoryList = new ArrayList<ArrayList<String>>();



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

                                workoutHistoryList.add(exercise_item);


                            }


                            System.out.println("Successfully loaded data to arraylist for exercise history from Firestore");
                            System.out.println("exercise items:");
                            for (ArrayList<String> item : workoutHistoryList){
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
