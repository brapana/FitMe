package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    Context context;
    ArrayList<ArrayList<String>> exerciseList;
    ScheduleFragment.ClickListener listener;

    public ScheduleAdapter(Context context, ArrayList<ArrayList<String>> exercises, ScheduleFragment.ClickListener listener) {
        this.context = context;
        this.exerciseList = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view, listener);
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView btnAddtoFavorites;
        private EditText etWorkoutTimeStamp;
        private EditText etWorkoutName;
        private EditText etWorkoutDuration;
        private EditText etWorkoutCaloriesBurned;
        private ArrayList<String> exerciseItem;
        //grab all the food item fields

        public ViewHolder(@NonNull View itemView, ScheduleFragment.ClickListener listener) {
            super(itemView);
            btnAddtoFavorites = itemView.findViewById(R.id.btnAddtoFavorites);
            btnAddtoFavorites.setOnClickListener(this);
            etWorkoutTimeStamp = itemView.findViewById(R.id.etWorkoutTimestamp);
            etWorkoutName = itemView.findViewById(R.id.etWorkoutName);
            etWorkoutDuration = itemView.findViewById(R.id.etWorkoutItemDuration);
            etWorkoutCaloriesBurned = itemView.findViewById(R.id.etWorkoutCaloriesBurned);
        }

        public void bind(ArrayList<String> exerciseItem){
            //populate fields
            this.exerciseItem = exerciseItem;
            //will populate the rest of the fields
        }

        @Override
        public void onClick(View v){
            if (v.getId() == btnAddtoFavorites.getId()){
                btnAddtoFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_filled));
                //TODO Brandon add le exerciseItem to favorites (if fav list not full or however this is implemented)
            }
        }
    }







}

