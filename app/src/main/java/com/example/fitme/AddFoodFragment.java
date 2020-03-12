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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodFragment extends Fragment {
    private Button btnSubmitFoodItem;
    private EditText etFoodName;
    private EditText etFoodCalories;
    private RadioGroup rbgFoodTime;
    private String foodTime = "";

    public AddFoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSubmitFoodItem = getActivity().findViewById(R.id.btnSubmitFoodItem);
        etFoodName = getActivity().findViewById(R.id.etFoodName);
        etFoodCalories = getActivity().findViewById(R.id.etFoodCalories);
        rbgFoodTime = getActivity().findViewById(R.id.rbgFoodTime);

        btnSubmitFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodTime.equals("") || etFoodName.getText().toString().isEmpty() || etFoodCalories.getText().toString().isEmpty()){
                    //check if all submission fields are filled and a meal time is chosen
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill in all fields! :)",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //TODO Brandon: add data to db
                    String mealTime = foodTime;
                    String foodName = etFoodName.getText().toString();
                    int foodCalories = Integer.parseInt(etFoodCalories.getText().toString());

                    writeFoodToDatabase(mealTime, foodName, foodCalories);


                    ((MainActivity) getActivity()).changeFragmentFromFragment(FoodDiaryFragment.class);
                }

            }
        });

        //Updates foodTime variable when a meal time is selected
        rbgFoodTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.rbBreakfast) {
                    foodTime = "Breakfast";
                } else if(checkedId == R.id.rbLunch) {
                    foodTime = "Lunch";
                } else if(checkedId == R.id.rbDinner){
                    foodTime = "Dinner";
                } else{
                    foodTime = "Snacks";
                }
            }

        });

        etFoodName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    //detects when user clicks out of the food name text box
                    //search api and set the calories in the calorie field
                    //user can still change the calorie field
                    Log.i("FOODTAG", etFoodName.getText().toString()); //Logs the input so you can visually see whats happening
                    etFoodCalories.setText(Integer.toString(etFoodName.getText().toString().length())); //setting calories to string length for now
                }
            }
        });
    }

    public void writeFoodToDatabase(String mealTime, String foodName, int foodCalories) {
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> food_history = new HashMap<>();
        Map<String, Object> food_info = new HashMap<>();


        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date time = Timestamp.now().toDate();

        food_info.put("food", foodName);
        food_info.put("calories", foodCalories);
        food_info.put("meal_time", mealTime);

        food_history.put(date_format.format(time), food_info);

        user.put("food_history", food_history);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote meal data to database!");
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
