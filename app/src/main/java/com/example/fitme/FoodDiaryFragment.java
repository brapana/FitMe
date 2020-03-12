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

//Library for API calls

import java.util.ArrayList;


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
    protected ArrayList<ArrayList<String>> bList;
    protected ArrayList<ArrayList<String>> lList;
    protected ArrayList<ArrayList<String>> dList;
    protected ArrayList<ArrayList<String>> sList;

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


        //get_food();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBreakfast = getActivity().findViewById(R.id.rvBreakfast);
        rvLunch = getActivity().findViewById(R.id.rvLunch);
        rvDinner = getActivity().findViewById(R.id.rvDinner);
        rvSnacks = getActivity().findViewById(R.id.rvSnacks);
        btnAddFoodItem = getActivity().findViewById(R.id.btnAddFoodItem);
        bList = new ArrayList<>();
        lList = new ArrayList<>();
        dList = new ArrayList<>();
        sList = new ArrayList<>();
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
        rvSnacks.setLayoutManager(layoutManagerD);
        rvSnacks.setAdapter(adapterS);

        //TODO Brandon: queryFood(); where lists get populated

        btnAddFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragmentFromFragment(AddFoodFragment.class);
            }
        });
    }

    private void queryFood(){
        //TODO Brandon: set all lists: bList, lList, dList, sList (breakfast, lunch, dinner, snacks respectively) #crying :)
        //here...

        //adapterB.addAll(bList);
        //adapter.notifyDataSetChanged()
        //...

    }


}
