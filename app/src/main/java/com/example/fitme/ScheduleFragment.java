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

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    public interface ClickListener {
        void onPositionClicked(int position);
    }

    private Button btnAddtoCalendar;
    private ArrayList<ArrayList<String>> workoutHistoryList;
    private RecyclerView rvSchedule;
    private ScheduleAdapter adapter;
    private ClickListener listener;

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

        //TODO Brandon u know what to do 8^)
        //queryWorkoutHistory();

    }
}
