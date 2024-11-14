package com.example.todo.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.database.TodoDatabase;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepo;
import com.example.todo.utils.EncryptionUtils;

import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText userName,password,confirmPassword;
    private Button button;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        button = findViewById(R.id.signupBtn);
        userName = findViewById(R.id.userNameSignup);
        password = findViewById(R.id.passwordSignup);
        confirmPassword = findViewById(R.id.confirmPasswod);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, getText(R.string.empty_field_toast), Toast.LENGTH_SHORT).show();
                }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(SignupActivity.this, getText(R.string.signup_toast), Toast.LENGTH_SHORT).show();
                }else {
                    signUpUser(userName.getText().toString(),password.getText().toString());
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signUpUser(String username, String password) {

        String hashedPassword = EncryptionUtils.hashPassword(password);

        User user = new User(username, hashedPassword);

        Intent intent = new Intent(SignupActivity.this,HomeActivity.class);

        new Thread(() -> {
            UserRepo userRpo = TodoDatabase.getInstance(getApplicationContext()).userRepo();
            long rowId = userRpo.insertUser(user);

            List<User> users = userRpo.getAllUsers();

            for (User user1 : users) {
                Log.d("Database", "User: " + user1.getUsername());
            }

            runOnUiThread(() -> {
                if (rowId != -1) {
                    Toast.makeText(SignupActivity.this, getText(R.string.signup_success_toast), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, getText(R.string.signup_failed_toast), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}