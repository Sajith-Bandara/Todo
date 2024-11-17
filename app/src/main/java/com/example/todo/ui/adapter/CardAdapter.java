package com.example.todo.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.database.TodoDatabase;
import com.example.todo.entity.Task;
import com.example.todo.repository.TaskRepo;
import com.example.todo.ui.activities.TaskViewActivity;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Task> list;
    private Context context;

    public CardAdapter(Context context, List<Task> tasks) {
        this.list = tasks;
        this.context = context;
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
            int taskId = task.getTaskId();
            String subject = task.getSubject();
            String startTime = task.getStartTime();
            String endTime = task.getEndTime();
            String status = task.getStatus();

            holder.cardSubject.setText(subject);
            holder.cardTime.setText(startTime + " - " + endTime);
            holder.cardStatus.setText(status);

            holder.cardStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeStatusConfirmation(taskId,"Done",holder);
                }
            });
            Log.i("filter","status "+status);
            switch (status) {
                case "Todo":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#DAF4FF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#32A7DA"));
                    break;
                case "Done":
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#EDE8FF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#5F33E1"));
                    break;
                default:
                    holder.cardViewHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#000000"));
                    break;
            }

            holder.itemView.setOnClickListener(v->{
                Intent intent = new Intent(v.getContext(), TaskViewActivity.class);
                intent.putExtra("taskId",taskId);
                v.getContext().startActivity(intent);
            });
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

    private void changeStatus(int taskId,String s){
        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(context).taskRepo();
            taskRepo.changStatus(taskId,s);


        }).start();
    }
    private void changeStatusConfirmation(int taskId,String status,CardViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.status_change_title))
                .setMessage(context.getString(R.string.conform_massage_status))
                .setPositiveButton(context.getString(R.string.button_Yes), (dialog, which) -> {
                    changeStatus(taskId, status);
                    holder.cardStatus.setBackgroundColor(Color.parseColor("#EDE8FF"));
                    holder.cardStatus.setTextColor(Color.parseColor("#5F33E1"));
                    holder.cardStatus.setText("Done");
                })
                .setNegativeButton(context.getString(R.string.button_No), null)
                .show();
    }
}
