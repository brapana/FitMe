package com.example.fitme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //set all the fields
        }

        public void bind(ArrayList<String> foodItem){
            //populate fields
        }
    }
}
