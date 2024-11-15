package com.example.todo.ui.activities;

import static com.example.todo.utils.Token.getUserId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.database.TodoDatabase;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepo;
import com.example.todo.ui.adapter.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView plusIcon;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        plusIcon = findViewById(R.id.plusIcon);

        Intent intent = new Intent(HomeActivity.this,CreateTaskActivity.class);

        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        getTasksList();
        super.onResume();
    }

    private void getTasksList(){

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Log.i("HomeActivity","token "+sharedPreferences.toString());
        String token = sharedPreferences.getString("token",null);

        if (token == null || token.isEmpty()) {
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            return;
        }

        int userId = Integer.parseInt(getUserId(token));

        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();

            try {
                taskList = taskRepo.getTasks(userId);
            }catch (Exception e){
                Log.e("filter","error"+e);
            }

            for (Task t : taskList) {
                Log.d("filter", "date: " + t.getDate());
            }
            runOnUiThread(() -> {
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                cardAdapter = new CardAdapter(taskList);
                recyclerView.setAdapter(cardAdapter);
            });
        }).start();

    }

}