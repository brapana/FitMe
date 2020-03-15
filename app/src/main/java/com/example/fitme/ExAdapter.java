package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExAdapter extends RecyclerView.Adapter<ExAdapter.ViewHolder>{
    Context context;
    ArrayList<ArrayList<String>> exList;

    public ExAdapter(Context context, ArrayList<ArrayList<String>> exes) {
        this.context = context;
        this.exList = exes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> exItem =  exList.get(position);
        holder.bind(exItem);
    }

    @Override
    public int getItemCount() {
        return exList.size();
    }

    public void clear(){
        exList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ArrayList<String>> exList){
        this.exList.addAll(exList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView etFavoritesName;
        private TextView etFavoritesCalBurnedPerMinute;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //set all the fields
            etFavoritesName= itemView.findViewById(R.id.etFavoritesName);
            etFavoritesCalBurnedPerMinute = itemView.findViewById(R.id.etFavoritesCalsBurned);

        }

        public void bind(ArrayList<String> exerciseItem){
            //populate fields
            etFavoritesName.setText(exerciseItem.get(0));
            etFavoritesCalBurnedPerMinute.setText(Integer.toString((int)Math.round(Double.parseDouble(exerciseItem.get(1)))) +" cals/min");
        }
    }
}
