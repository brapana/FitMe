package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


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
}
