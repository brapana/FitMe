package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    Context context;
    ArrayList<ArrayList<String>> exerciseList;

    public ScheduleAdapter(Context context, ArrayList<ArrayList<String>> exercises) {
        this.context = context;
        this.exerciseList = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> exerciseItem = exerciseList.get(position);
        holder.bind(exerciseItem);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    // Clean all elements of the recycler
    public void clear(){
        exerciseList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<ArrayList<String>> exerciseList){
        this.exerciseList.addAll(exerciseList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView etWorkoutTimeStamp;
        private TextView etWorkoutName;
        private TextView etWorkoutDuration;
        private TextView etWorkoutCaloriesBurned;
        //grab all the food item fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etWorkoutTimeStamp = itemView.findViewById(R.id.etWorkoutTimestamp);
            etWorkoutName = itemView.findViewById(R.id.etWorkoutName);
            etWorkoutDuration = itemView.findViewById(R.id.etWorkoutItemDuration);
            etWorkoutCaloriesBurned = itemView.findViewById(R.id.etWorkoutCaloriesBurned);
        }

        public void bind(ArrayList<String> exerciseItem){
            //populate fields
            etWorkoutTimeStamp.setText(exerciseItem.get(0));
            etWorkoutName.setText(exerciseItem.get(1));
            etWorkoutCaloriesBurned.setText(exerciseItem.get(2) +" cal burned");
            etWorkoutDuration.setText(exerciseItem.get(3)+" min");
        }
    }







}

