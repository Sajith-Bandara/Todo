package com.example.todo.ui.activities;

import static com.example.todo.utils.Token.getUserId;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todo.R;
import com.example.todo.database.TodoDatabase;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepo;
import com.example.todo.repository.UserRepo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText subject,description;
    private Button button;
    private TextInputEditText startTimePicker,endTimePicker,date;
    private Calendar selectedDate = Calendar.getInstance();
    private int startHour, startMinute, endHour, endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        subject = findViewById(R.id.subject);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date_picker);
        startTimePicker = findViewById(R.id.start_time_picker);
        endTimePicker = findViewById(R.id.end_time_picker);
        button = findViewById(R.id.addTask);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
        startTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker("start");
            }
        });
        endTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker("end");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(subject.getText().toString().isEmpty() || description.getText().toString().isEmpty()){
                    Toast.makeText(CreateTaskActivity.this,getText(R.string.empty_field_toast),Toast.LENGTH_SHORT).show();
                }else if(!isEndTimeGreater()) {
                    Toast.makeText(CreateTaskActivity.this, getText(R.string.invalid_time_toast), Toast.LENGTH_SHORT).show();

                }else if(!isDateValid()){
                    Toast.makeText(CreateTaskActivity.this, getText(R.string.invalid_date_toast), Toast.LENGTH_SHORT).show();

                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences("app_prefs",MODE_PRIVATE);
                    String token = sharedPreferences.getString("token",null);

                    Task task = new Task(subject.getText().toString(),description.getText().toString(),
                                        date.getText().toString(), startTimePicker.getText().toString(),
                                        endTimePicker.getText().toString(),"Todo",Integer.parseInt(getUserId(token))
                                        );
                    taskSaveConfirmation(task);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        String selectedDate = selectedYear+ "-" + (selectedMonth + 1) + "-" + selectedDay;
                        date.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    private void timePicker(String type) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String timeType = type;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        String selectedTime = selectedHour + ":" + selectedMinute;

                        if(timeType.equals("start")) {
                            startHour = selectedHour;
                            startMinute = selectedMinute;
                            startTimePicker.setText(selectedTime);
                        }
                        else {
                            endHour = selectedHour;
                            endMinute = selectedMinute;
                            endTimePicker.setText(selectedTime);
                        }

                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }
    private boolean isDateValid() {
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        if (selectedDate.before(today)) {
            return false;
        }
        return true;
    }

    private boolean isEndTimeGreater() {
        if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            return false;
        }
        return true;
    }

    private void saveTask(Task t){
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        String token = sharedPreferences.getString("token",null);
        Task task = t;
        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();

            long row= taskRepo.saveTask(task);

            Intent intent = new Intent(CreateTaskActivity.this,HomeActivity.class);

            runOnUiThread(()->{
                if(row != -1){
                    Toast.makeText(CreateTaskActivity.this,getText(R.string.task_save_toast),Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CreateTaskActivity.this,getText(R.string.task_save_error_toast),Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.un_save_title))
                .setMessage(getString(R.string.conform_massage_back))
                .setPositiveButton(getString(R.string.button_Yes), (dialog, which) -> {
                    CreateTaskActivity.super.onBackPressed();
                    finish();
                })
                .setNegativeButton(getString(R.string.button_No), null)
                .show();
    }

    private void taskSaveConfirmation(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.save_data_title))
                .setMessage(getString(R.string.conform_massage_save))
                .setPositiveButton(getString(R.string.button_Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveTask(task);
                    }
                })
                .setNegativeButton(R.string.button_No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }
}