package com.example.todo.ui.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.entity.Task;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Task> list;

    public CardAdapter(List<Task> tasks) {
        this.list = tasks;
        Log.i("filter","list"+this.list.toString());
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        try {
            Task task = list.get(position);
            String subject = task.getSubject();
            String startTime = task.getStartTime();
            String endTime = task.getEndTime();
            String status = task.getStatus();

            holder.cardSubject.setText(subject);
            holder.cardTime.setText(startTime + " - " + endTime);
            holder.cardStatus.setText(status);

            switch (status) {
                case "Todo":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#DAF4FF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#32A7DA"));
                    break;
                case "Done":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#EDE8FF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#5F33E1"));
                    break;
                case "Miss":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#FFE3DA"));
                    holder.cardStatus.setTextColor(Color.parseColor("#CD7258"));
                    break;
                case "In Progress":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#FFF1D6"));
                    holder.cardStatus.setTextColor(Color.parseColor("#CF9A32"));
                    break;
                default:
                    holder.cardViewHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#000000"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardSubject, cardTime, cardStatus;
        View cardViewHolder;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSubject = itemView.findViewById(R.id.cardTitle);
            cardTime = itemView.findViewById(R.id.cardTime);
            cardStatus = itemView.findViewById(R.id.cardStatus);
            cardViewHolder = itemView;
        }
    }
}
