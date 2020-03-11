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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;


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
    public void writeToDatabase() {
        // Create a new user
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> fav_exercises = new HashMap<>();
        user.put("age", 21);
        user.put("gender", "male");
        user.put("height", 72);
        user.put("weight", 150);
        user.put("name", "Johnathon Wickeston");
        user.put("daily_calorie_goal", 2000);
        user.put("fav_exercises", fav_exercises);


        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            System.out.println("Dummy data successfully written to Firestore!");
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
