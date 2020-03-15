package com.example.fitme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
                    String date = sdf.format(cal.getTime());
                    date += " "+Integer.toString(hours)+":"+Integer.toString(minutes)+":00.000";
                    //TODO Brandon add date (timestamp of next workout) didn't check if time is in the past

                    System.out.println("DATE:");
                    System.out.println(date);

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-M-dd HH:mm.ss.SSS");

                    Date parsed_date = new Date();
                try {
                    parsed_date = sdf2.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                    startAlarmBroadcastReceiver(getContext(), parsed_date.getTime()+110000);


                    writeTimeToDatabase(hours, minutes);

                Toast.makeText(v.getContext(), "Next workout time set!", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).changeFragmentFromFragment(ScheduleFragment.class);
            }
        });

    }

    //sets a one-time notification at [time] milliseconds, should only allow for one at a time (new alarms overwrite old ones)
    // code from: https://stackoverflow.com/questions/45815899/localnotification-with-alarmmanager-and-broadcastreceiver-not-firing-up-in-andro
    public static void startAlarmBroadcastReceiver(Context context, long time) {
        Intent _intent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // Remove any previous pending intent.
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        System.out.println(time);
        System.out.println("set time vs system time");
        System.out.println(System.currentTimeMillis());
    }

    //write next notification time to database
    public void writeTimeToDatabase(long hours, long minutes) {
        Map<String, Object> user = new HashMap<>();


        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String time  = String.format("%d:%d", hours, minutes);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date time_date = new Date();

        try {
            sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");

        time = sdf2.format(time_date);

        user.put("next_notification", time);

        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote next notification time to database!");
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
