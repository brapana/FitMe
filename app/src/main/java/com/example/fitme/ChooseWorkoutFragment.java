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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseWorkoutFragment extends Fragment {
    //The user will be taken to this page once they click start workout to select a workout
    public interface ClickListener {
        void onPositionClicked(int position);
    }

    private ArrayList<ArrayList<String>> recommendedList;
    private ClickListener listener;
    private RecommendedWorkoutsAdapter adapter;
    private RecyclerView rvRecommendedWorkouts;

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

        //TODO Brandon: le recommended workouts
        //queryRecommendedWorkouts();

    }
}
