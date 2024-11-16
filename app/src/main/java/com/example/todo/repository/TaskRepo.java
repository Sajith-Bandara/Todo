package com.example.todo.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todo.entity.Task;

import java.util.List;

@Dao
public interface TaskRepo {
    @Insert
    long saveTask(Task task);

    @Update
    int updateTask(Task task);

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    int deleteTask(int taskId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND date= :date ORDER BY startTime ASC")
    List<Task> getTasks(int userId,String date);

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    Task getTaskById(int taskId);

    @Query("UPDATE tasks SET status = :status WHERE taskId = :taskId")
    int changStatus(int taskId, String status);
}
