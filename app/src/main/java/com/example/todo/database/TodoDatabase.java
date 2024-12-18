package com.example.todo.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepo;
import com.example.todo.repository.UserRepo;

@Database(entities = {User.class, Task.class}, version = 3)
public abstract class TodoDatabase extends RoomDatabase {

    private static TodoDatabase instance;

    public abstract UserRepo userRepo();
    public abstract TaskRepo taskRepo();

    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoDatabase.class, "todo_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
