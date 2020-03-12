package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

//Library for API calls
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodDiaryFragment extends Fragment {
    private ImageView btnAddFoodItem;

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


        get_food();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddFoodItem = getActivity().findViewById(R.id.btnAddFoodItem);
        btnAddFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragmentFromFragment(AddFoodFragment.class);
            }
        });
    }

    //returned calorie counts can be weird sometimes
    public void get_food(){
        String url = "https://api.edamam.com/api/food-database/parser?app_id=396d20fe&app_key=" +
                "951e412e3df060cf1c1bd5d6208204a4&ingr=burrito";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double calories = (double)response.getJSONArray("hints").getJSONObject(0).getJSONObject("food").getJSONObject("nutrients").get("ENERC_KCAL");
                            System.out.println(calories);
                        } catch (JSONException e) {
                            System.out.println("No food/calories in API response");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("API access failed");
                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }
}
