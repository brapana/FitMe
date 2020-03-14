package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecommendedWorkoutsAdapter extends RecyclerView.Adapter<RecommendedWorkoutsAdapter.ViewHolder> {
    Context context;
    ArrayList<ArrayList<String>> recommendedList;
    ChooseWorkoutFragment.ClickListener listener;

    public RecommendedWorkoutsAdapter(Context context, ArrayList<ArrayList<String>> recommendedList, ChooseWorkoutFragment.ClickListener listener) {
        this.context = context;
        this.recommendedList = recommendedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommended_workout_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> recommendedItem = recommendedList.get(position);
        holder.bind(recommendedItem);
    }

    @Override
    public int getItemCount() {
        return recommendedList.size();
    }

    // Clean all elements of the recycler
    public void clear(){
        recommendedList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<ArrayList<String>> recommendedList){
        this.recommendedList.addAll(recommendedList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //grab all the food item fields
        private EditText etRecommendedName;
        private TextView etCaloriesBurnedPerMinute;
        private CardView cvRecommended;
        private ArrayList<String> chosenWorkout;

        public ViewHolder(@NonNull View itemView, ChooseWorkoutFragment.ClickListener listener) {
            super(itemView);
            //set all the fields
            etRecommendedName = itemView.findViewById(R.id.etRecommendedName);
            etCaloriesBurnedPerMinute = itemView.findViewById(R.id.etCaloriesBurnedPerMinute);
            cvRecommended = itemView.findViewById(R.id.cvRecommended);
            cvRecommended.setOnClickListener(this);
        }

        public void bind(ArrayList<String> recommendedItem){
            //populate fields
            etRecommendedName.setText(recommendedItem.get(1));
            etCaloriesBurnedPerMinute.setText(recommendedItem.get(2));
            chosenWorkout = recommendedItem;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == cvRecommended.getId()){
                //TODO Brandon: add recommended workout to workout history
                //chosenWorkout is the workout array they chose, has the same info as each item in the recommended workouts list passed to the adapter
                Toast.makeText(context, "You selected a workout! See your schedule to view your workout.", Toast.LENGTH_SHORT).show();

                ((MainActivity) v.getContext()).changeFragmentFromFragment(HomeFragment.class);
            }
        }
    }

}

