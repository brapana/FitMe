package com.example.fitme;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ImageView btnEditProfile;
    private Dialog dialog;
    private double BMR = 0.0;
    private long _weight = 0;
    private long _height = 0;
    private long _age = 0;
    private String _gender = "";

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnEditProfile = getActivity().findViewById(R.id.btnEditProfile);
        dialog = new Dialog(getActivity());

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_profile);
                final Button btnCancel = dialog.findViewById(R.id.btnCancelProfile);
                final Button btnSubmit = dialog.findViewById(R.id.btnSubmitProfile);
                final EditText etName = dialog.findViewById(R.id.etName);
                final EditText etHeight = dialog.findViewById(R.id.etHeight);
                final EditText etWeight = dialog.findViewById(R.id.etWeight);
                final EditText etAge = dialog.findViewById(R.id.etAge);
                final EditText etGender = dialog.findViewById(R.id.etGender);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!etName.getText().toString().equals("")){
                            String name = etName.getText().toString();
                            writeName(name);
                        }
                        if (!etHeight.getText().toString().equals("")){
                            String height = etHeight.getText().toString();
                            writeHeight(Integer.parseInt(height));
                            _height = Integer.parseInt(height);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etWeight.getText().toString().equals("")){
                            String weight = etWeight.getText().toString();
                            writeWeight(Integer.parseInt(weight));
                            _weight = Integer.parseInt(weight);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etAge.getText().toString().equals("")){
                            String age = etAge.getText().toString();
                            writeAge(Integer.parseInt(age));
                            _age = Integer.parseInt(age);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etGender.getText().toString().equals("")){
                            String gender = etGender.getText().toString();
                            writeGender(gender);
                            _gender = gender;
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));

                        }
                        dialog.dismiss();
                        ((MainActivity) getActivity()).changeFragmentFromFragment(ProfileFragment.class);
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        loadData(view, savedInstanceState);

    }

    //calculates the BMR according to the formulas from here:
    // (https://www.everydayhealth.com/weight/boost-weight-loss-by-knowing-your-bmr.aspx)
    // assumes weight in pounds, height in inches, and age in years
    public void calcBMR(){

        if (_gender.toLowerCase().equals("male"))
            BMR = 66 + (6.23 * _weight) + (12.7 * _height) - (6.8 * _age);
        else
            BMR = 655 + (4.35 * _weight) + (4.7 * _height) - (4.7 * _age);

    }

    //loads data from the FireStore db into the profile strings displayed in the app
    public void loadData(final View view, @Nullable Bundle savedInstanceState){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            _weight = (long)data.get("weight");
                            _height = (long)data.get("height");
                            _age = (long)data.get("age");
                            _gender = (String)data.get("gender");
                            calcBMR();

                            //update the viewable text with the values from FireBase
                            ((TextView)view.findViewById(R.id.name)).setText((String)data.get("name"));

                            ((TextView)view.findViewById(R.id.age)).setText(String.format("%d yrs.", _age));

                            ((TextView)view.findViewById(R.id.gender)).setText((String)_gender);

                            ((TextView)view.findViewById(R.id.height)).setText(String.format("%d in.", _height));

                            ((TextView)view.findViewById(R.id.weight)).setText(String.format("%d lbs.", _weight));

                            ((TextView)view.findViewById(R.id.bmr)).setText(String.format("%.1f", BMR));

                            System.out.println("Successfully loaded data for profile view from Firestore");


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


    //write daily calorie goal
    public void writeCalGoal(int calorie_goal) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("daily_calorie_goal", calorie_goal);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote new calorie goal to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }
    //write inputted name
    public void writeName(String name) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("name", name);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote profile data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }
    //write inputted weight
    public void writeWeight(int weight) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("weight", weight);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote profile data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }
    //write inputted height
    public void writeHeight(int height) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("height", height);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote profile data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    //write inputted age
    public void writeAge(int age) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("age", age);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote profile data to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    //write inputted gender
    public void writeGender(String gender) {
        Map<String, Object> user = new HashMap<>();

        String UUID = ((MainActivity)getActivity()).get_uuid(getContext());

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("gender", gender);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote profile data to database!");
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
