package com.example.fitme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChooseWorkoutActivity extends AppCompatActivity {
    //This is the activity that will pop up when the user clicks on start workout
    //Recycler view will show a list of workouts to select
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_workout);
    }
}
