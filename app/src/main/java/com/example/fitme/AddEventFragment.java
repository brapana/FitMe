package com.example.fitme;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment {
    private Button btnSubmitEvent;
    private TimePicker timePicker;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmitEvent = getActivity().findViewById(R.id.btnSubmitEvent);
        timePicker = getActivity().findViewById(R.id.timePickerStart);


        btnSubmitEvent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                    int hours = timePicker.getHour(); // 0-23 hours
                    int minutes = timePicker.getMinute(); // 0-59 minutes
                    Bundle b = getArguments();
                    long date = b.getLong("date");
                    Toast.makeText(v.getContext(), hours+" "+minutes+" "+Long.toString(date), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).changeFragmentFromFragment(ScheduleFragment.class);
            }
        });

    }

}
