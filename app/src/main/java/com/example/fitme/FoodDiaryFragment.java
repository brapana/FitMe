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
import android.widget.ImageView;
import android.widget.TextView;

//Library for API calls
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodDiaryFragment extends Fragment {
    private ImageView btnAddFoodItem;

    private RecyclerView rvBreakfast;
    private RecyclerView rvLunch;
    private RecyclerView rvDinner;
    private RecyclerView rvSnacks;
    protected BreakfastAdapter adapterB;
    protected LunchAdapter adapterL;
    protected DinnerAdapter adapterD;
    protected SnackAdapter adapterS;

    //arraylists of String arraylists with the following structure: [timestamp, food_name, calories]
    //each outer arraylist responsible for a particular type of food (meal_time)
    // each arraylist will be sorted by timestamp descending
    protected ArrayList<ArrayList<String>> sList = new ArrayList<ArrayList<String>>();
    protected ArrayList<ArrayList<String>> bList = new ArrayList<ArrayList<String>>();
    protected ArrayList<ArrayList<String>> lList = new ArrayList<ArrayList<String>>();
    protected ArrayList<ArrayList<String>> dList = new ArrayList<ArrayList<String>>();


    public FoodDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_diary, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        queryFood();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBreakfast = getActivity().findViewById(R.id.rvBreakfast);
        rvLunch = getActivity().findViewById(R.id.rvLunch);
        rvDinner = getActivity().findViewById(R.id.rvDinner);
        rvSnacks = getActivity().findViewById(R.id.rvSnacks);
        btnAddFoodItem = getActivity().findViewById(R.id.btnAddFoodItem);
        //bList = new ArrayList<>();
        //lList = new ArrayList<>();
        //dList = new ArrayList<>();
        //sList = new ArrayList<>();
        adapterB = new BreakfastAdapter(getContext(), bList);
        adapterL = new LunchAdapter(getContext(), lList);
        adapterD = new DinnerAdapter(getContext(), dList);
        adapterS = new SnackAdapter(getContext(), sList);

        LinearLayoutManager layoutManagerB = new LinearLayoutManager(getContext());
        rvBreakfast.setLayoutManager(layoutManagerB);
        rvBreakfast.setAdapter(adapterB);

        LinearLayoutManager layoutManagerL = new LinearLayoutManager(getContext());
        rvLunch.setLayoutManager(layoutManagerL);
        rvLunch.setAdapter(adapterL);

        LinearLayoutManager layoutManagerD = new LinearLayoutManager(getContext());
        rvDinner.setLayoutManager(layoutManagerD);
        rvDinner.setAdapter(adapterD);

        LinearLayoutManager layoutManagerS = new LinearLayoutManager(getContext());
        rvSnacks.setLayoutManager(layoutManagerS);
        rvSnacks.setAdapter(adapterS);

        //TODO Brandon: queryFood(); where lists get populated

        btnAddFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragmentFromFragment(AddFoodFragment.class);
            }
        });
    }


    //sets arraylists of String arraylists with the following structure: [timestamp, food_name, calories]
    //each outer arraylist responsible for a particular type of food (meal_time)
    // each arraylist sorted by timestamp descending
    //uses food_history Firestore document in order to populate arraylists
    private void queryFood(){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            Map<String, Object> food_history = (Map<String, Object>)data.get("food_history");

                            Set<String> keys = food_history.keySet();


                            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

                            ArrayList<Date> keys_as_dates = new ArrayList<Date>();

                            Date key_as_date = new Date();

                            //convert list of keys to Date objects (so they can be sorted)
                            for (String key: keys){
                                try {
                                    key_as_date = date_format.parse(key);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                keys_as_dates.add(key_as_date);
                            }

                            Collections.sort(keys_as_dates, Collections.reverseOrder());


                            //arraylists of String arraylists with the following structure: [timestamp, food_name, calories]
                            //each outer arraylist responsible for a particular type of food (meal_time)
                            // each arraylist will be sorted by timestamp descending
                            sList = new ArrayList<ArrayList<String>>();
                            bList = new ArrayList<ArrayList<String>>();
                            lList = new ArrayList<ArrayList<String>>();
                            dList = new ArrayList<ArrayList<String>>();


                            //loop over now sorted date keys and place the objects into the correct arraylist
                            for (Date key : keys_as_dates){

                                String key_string = date_format.format(key);

                                Map<String, Object> food_info = (Map<String, Object>)food_history.get(key_string);

                                //declaring inner array to be added
                                ArrayList<String> food_item = new ArrayList<String>();

                                food_item.add(key_string);
                                food_item.add((String)food_info.get("food"));
                                food_item.add(String.valueOf(food_info.get("calories")));

                                String meal_time = (String)food_info.get("meal_time");

                                //put the food item into the correct arraylist
                                switch(meal_time){
                                    case "Snacks":
                                        sList.add(food_item);
                                        break;
                                    case "Breakfast":
                                        bList.add(food_item);
                                        break;
                                    case "Lunch":
                                        lList.add(food_item);
                                        break;
                                    case "Dinner":
                                        dList.add(food_item);
                                        break;
                                }


                            }


                            System.out.println("Successfully loaded data for food diary from Firestore");
                            for (ArrayList<String> item : dList){
                                System.out.println("dinner items:");
                                System.out.println(item.get(0) + " " + item.get(1) + " " + item.get(2));
                            }
                            for (ArrayList<String> item : bList){
                                System.out.println("breakfast items:");
                                System.out.println(item.get(0) + " " + item.get(1) + " " + item.get(2));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

        //TODO: to be used in the future? @Marissa
//        adapterS.addAll(sList);
//        adapterS.notifyDataSetChanged();
//
//        adapterB.addAll(bList);
//        adapterB.notifyDataSetChanged();
//
//        adapterL.addAll(lList);
//        adapterL.notifyDataSetChanged();
//
//        adapterD.addAll(dList);
//        adapterD.notifyDataSetChanged();
    }


}
