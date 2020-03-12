package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BreakfastAdapter extends RecyclerView.Adapter<BreakfastAdapter.ViewHolder> {
    Context context;
    ArrayList<ArrayList<String>> foodList;

    public BreakfastAdapter(Context context, ArrayList<ArrayList<String>> foods) {
        this.context = context;
        this.foodList = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> foodItem = foodList.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Clean all elements of the recycler
    public void clear(){
        foodList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<ArrayList<String>> foodList){
        this.foodList.addAll(foodList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //grab all the food item fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //set all the fields
        }

        public void bind(ArrayList<String> foodItem){
            //populate fields
        }
    }

}

