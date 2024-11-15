package com.example.todo.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todo.entity.Task;

import java.util.List;

@Dao
public interface TaskRepo {
    @Insert
    long saveTask(Task task);

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    List<Task> getTasks(int userId);
}
