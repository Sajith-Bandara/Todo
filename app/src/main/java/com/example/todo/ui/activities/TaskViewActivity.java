package com.example.todo.ui.activities;

import static com.example.todo.utils.Token.getUserId;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.todo.repository.TaskRepo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class TaskViewActivity extends AppCompatActivity {

    private EditText subject,description;
    private Button buttonEdit, buttonDelete;
    private TextInputEditText startTimePicker,endTimePicker,date;
    private Calendar selectedDate = Calendar.getInstance();
    private int startHour, startMinute, endHour, endMinute;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_view);

        Intent homeIntent = getIntent();
        int taskId = homeIntent.getIntExtra("taskId",0);

        subject = findViewById(R.id.subjectView);
        description = findViewById(R.id.descriptionView);
        date = findViewById(R.id.date_pickerView);
        startTimePicker = findViewById(R.id.start_time_pickerView);
        endTimePicker = findViewById(R.id.end_time_pickerView);
        buttonEdit = findViewById(R.id.editTask);
        buttonDelete = findViewById(R.id.deleteTask);

        getTask(taskId);
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
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subject.getText().toString().isEmpty() || description.getText().toString().isEmpty()){
                    Toast.makeText(TaskViewActivity.this,getText(R.string.empty_field_toast),Toast.LENGTH_SHORT).show();
                }else if(!isEndTimeGreater()) {
                    Toast.makeText(TaskViewActivity.this, getText(R.string.invalid_time_toast), Toast.LENGTH_SHORT).show();

                }else if(!isDateValid()){
                    Toast.makeText(TaskViewActivity.this, getText(R.string.invalid_date_toast), Toast.LENGTH_SHORT).show();

                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences("app_prefs",MODE_PRIVATE);
                    String token = sharedPreferences.getString("token",null);

                    Task task = new Task(subject.getText().toString(),description.getText().toString(),
                            date.getText().toString(), startTimePicker.getText().toString(),
                            endTimePicker.getText().toString(),"Todo",Integer.parseInt(getUserId(token))
                    );
                    task.setTaskId(taskId);
                    taskSaveConfirmation(task);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation(taskId);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getTask(int taskId){

        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();
            Task  task = taskRepo.getTaskById(taskId);

            runOnUiThread(()->{
                subject.setText(task.getSubject());
                description.setText(task.getDescription());
                startTimePicker.setText(task.getStartTime());
                endTimePicker.setText(task.getEndTime());
                date.setText(task.getDate());

                String[] startTimeParts = task.getStartTime().split(":");
                startHour = Integer.parseInt(startTimeParts[0]);
                startMinute = Integer.parseInt(startTimeParts[1]);

                String[] endTimeParts = task.getEndTime().split(":");
                endHour = Integer.parseInt(endTimeParts[0]);
                endMinute = Integer.parseInt(endTimeParts[1]);
            });

        }).start();
    }


    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String formattedDate = selectedYear+ "-" + (selectedMonth + 1) + "-" + selectedDay;
                    date.setText(formattedDate);
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

        String displayedDate = date.getText().toString();
        if (!displayedDate.isEmpty()) {
            String[] parts = displayedDate.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int year = Integer.parseInt(parts[2]);
            selectedDate.set(year, month, day);
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        return !selectedDate.before(today);
    }


    private boolean isEndTimeGreater() {
        if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            return false;
        }
        return true;
    }

    private void updateTask(Task t){
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Task task = t;

        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();

            int row = taskRepo.updateTask(task);
            Intent intent = new Intent(TaskViewActivity.this,HomeActivity.class);

            runOnUiThread(()->{
                if(row >0){
                    Toast.makeText(TaskViewActivity.this,getText(R.string.task_update_toast),Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }else {
                    Toast.makeText(TaskViewActivity.this,getText(R.string.task_update_error_toast),Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

    }

    private void deleteTask(int taskId){
        new Thread(()->{
            TaskRepo taskRepo = TodoDatabase.getInstance(getApplicationContext()).taskRepo();
            int row = taskRepo.deleteTask(taskId);

            Intent intent = new Intent(TaskViewActivity.this,HomeActivity.class);

            runOnUiThread(()->{
                if(row >0){
                    Toast.makeText(TaskViewActivity.this,getText(R.string.task_delete_toast),Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }else {
                    Toast.makeText(TaskViewActivity.this,getText(R.string.task_delete_error_toast),Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }


    private void deleteConfirmation(int taskId) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.conform_massage_delete))
                .setPositiveButton(getString(R.string.button_Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTask(taskId);
                    }
                })
                .setNegativeButton(R.string.button_No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    private void taskSaveConfirmation(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.update_data_title))
                .setMessage(getString(R.string.conform_massage_save))
                .setPositiveButton(getString(R.string.button_Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateTask(task);
                    }
                })
                .setNegativeButton(R.string.button_No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }
}