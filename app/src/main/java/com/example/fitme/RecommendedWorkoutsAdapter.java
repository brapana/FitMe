package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        private TextView etRecommendedName;
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
            etCaloriesBurnedPerMinute.setText("Burn ~"+Integer.toString((int)Math.round(Double.parseDouble(recommendedItem.get(3)))) +" cals");
            chosenWorkout = recommendedItem;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == cvRecommended.getId()){
                //TODO Brandon: add recommended workout to workout history (chosen workout: an item from the recommended list they clicked on)
                Toast.makeText(context, "You selected a workout! See your schedule to view your workout.", Toast.LENGTH_SHORT).show();
                ((MainActivity) v.getContext()).changeFragmentFromFragment(HomeFragment.class);

                writeExerciseToDatabase(chosenWorkout.get(1), (int)Double.parseDouble(chosenWorkout.get(3)), Integer.parseInt(chosenWorkout.get(2)));
            }
        }
    }

    //write performed exercise to database
    //calories_burned = cal burned per min, time_performed = time in min
    //eventually this should go in chooseworkoutfragment
    public void writeExerciseToDatabase(String exercise_name, int calories_burned, int time_performed) {
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> exercise_history = new HashMap<>();
        Map<String, Object> exercise_info = new HashMap<>();


        String UUID = MainActivity.get_uuid(context);

        System.out.println("UUID is: " + UUID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date time = Timestamp.now().toDate();

        exercise_info.put("exercise", exercise_name);
        exercise_info.put("calories_burned", calories_burned);
        exercise_info.put("time_performed", time_performed);

        exercise_history.put(date_format.format(time), exercise_info);

        user.put("exercise_history", exercise_history);

        //set (overwrite) document with key of the current device's UUID
        db.collection("users").document(UUID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully wrote exercise data to database!");
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

