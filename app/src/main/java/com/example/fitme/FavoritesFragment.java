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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private RecyclerView rvEx;
    protected ExAdapter adapterE;

    //ARRAYLIST OF FAVORITE EXERCISES
    //arraylist of String arraylists with each inner arraylist being [exercise name, cal burned/min]
    //Generated in queryFavExercises()
    private ArrayList<ArrayList<String>> aList;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEx = getActivity().findViewById(R.id.rvEx);
        aList = new ArrayList<ArrayList<String>>();
        adapterE = new ExAdapter(getContext(), aList);
        LinearLayoutManager layoutManagerE = new LinearLayoutManager(getContext());
        rvEx.setLayoutManager(layoutManagerE);
        rvEx.setAdapter(adapterE);

        queryFavExercises(view, savedInstanceState);
    }

    //translate the fav_exercise from Firestore to an arraylist
    //arraylist of String arraylists with each inner arraylist being [exercise name, cal burned/min]
    public void queryFavExercises(final View view, @Nullable Bundle savedInstanceState){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();


        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String,Object> fav_exercises = (Map<String,Object>)data.get("fav_exercises");

                            Set<String> keys = fav_exercises.keySet();

                            ArrayList<String> exercise_keys = new ArrayList<String>(keys);

                            Collections.sort(exercise_keys);

                            aList = new ArrayList<ArrayList<String>>();

                            for (String key : exercise_keys){


                                ArrayList<String> exercise_item = new ArrayList<String>();

                                exercise_item.add(key);
                                exercise_item.add(Double.toString((Double)fav_exercises.get(key)));


                                aList.add(exercise_item);


                            }

                            System.out.println("Successfully loaded data to arraylist for favorite exercises");
                            System.out.println("fav exercises list:");
                            for (ArrayList<String> item : aList){
                                System.out.println(item.get(0) + " " + item.get(1));
                            }
                            adapterE.addAll(aList);
                            adapterE.notifyDataSetChanged();


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
