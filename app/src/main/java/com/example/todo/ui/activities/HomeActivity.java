package com.example.todo.ui.activities;

import static com.example.todo.utils.Token.getUserId;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    private ImageView plusIcon;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<Task> taskList = new ArrayList<>();
    private TextView yMonth,yDate,yYear,tMonth,tDate,tYear,toMonth,toDate,toYear;
    private View calender,yesterdayLayout,tomorrowLayout;
    private LocalDate today,tomorrow,yesterday;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        plusIcon = findViewById(R.id.plusIcon);

        yMonth = findViewById(R.id.yMonthHome);
        yDate = findViewById(R.id.yDateHome);
        yYear = findViewById(R.id.yYearHome);

        tMonth = findViewById(R.id.tMonthHome);
        tDate = findViewById(R.id.tDateHome);
        tYear = findViewById(R.id.tYearHome);

        toMonth = findViewById(R.id.toMonthHome);
        toDate = findViewById(R.id.toDateHome);
        toYear = findViewById(R.id.toYearHome);

        calender = findViewById(R.id.homeCalender);

        tomorrowLayout =  findViewById(R.id.tomorrowHomeLayout);
        yesterdayLayout = findViewById(R.id.yesterdayHomeLayout);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
        tomorrowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDates(tomorrow);
            }
        });
        yesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDates(yesterday);
            }
        });

        today = LocalDate.now();
        setDates(today);

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
        getTasksList(today.toString());
        super.onResume();
    }

    private void getTasksList(String date){

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token",null);

        if (token == null || token.isEmpty()) {
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            return;
        }

        int userId = Integer.parseInt(getUserId(token));

        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();

            try {
                taskList = taskRepo.getTasks(userId,date);
            }catch (Exception e){
                Log.e("filter","error"+e);
            }

            for (Task t : taskList) {
                Log.d("filter", "date: " + t.getDate());
            }
            runOnUiThread(() -> {
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                cardAdapter = new CardAdapter(this,taskList);
                recyclerView.setAdapter(cardAdapter);
            });
        }).start();

    }

    private void setDates(LocalDate today) {
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        String tMonthName = today.format(formatter);
        String yMonthName = yesterday.format(formatter);
        String toMonthName = tomorrow.format(formatter);

        tMonth.setText(tMonthName);
        tDate.setText(String.valueOf(today.getDayOfMonth()));
        tYear.setText(String.valueOf(today.getYear()));

        yMonth.setText(yMonthName);
        yDate.setText(String.valueOf(yesterday.getDayOfMonth()));
        yYear.setText(String.valueOf(yesterday.getYear()));

        toMonth.setText(toMonthName);
        toDate.setText(String.valueOf(tomorrow.getDayOfMonth()));
        toYear.setText(String.valueOf(tomorrow.getYear()));

        getTasksList(today.toString());
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                        setDates(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
}