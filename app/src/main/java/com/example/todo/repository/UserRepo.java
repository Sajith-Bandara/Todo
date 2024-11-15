package com.example.todo.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todo.entity.User;

import java.util.List;

@Dao
public interface UserRepo {
    @Insert
    long saveUser(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT COUNT(*) FROM users WHERE username = :username ")
    int usernameExists(String username);
}
