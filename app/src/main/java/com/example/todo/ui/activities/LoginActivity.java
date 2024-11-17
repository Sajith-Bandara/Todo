package com.example.todo.ui.activities;

import static com.example.todo.utils.Token.createToken;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todo.R;
import com.example.todo.database.TodoDatabase;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepo;
import com.example.todo.utils.EncryptionUtils;

public class LoginActivity extends AppCompatActivity {

    private TextView switchToSignup;
    private Button button;
    private EditText password,userName;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.signinBtn);
        userName = findViewById(R.id.userNameLogin);
        password = findViewById(R.id.passwordLogin);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userName.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this,getText(R.string.empty_field_toast),Toast.LENGTH_SHORT).show();
                }else {
                    Log.i("filter","name: "+userName.getText().toString()+" pass: "+password.getText().toString());
                    loginUser(userName.getText().toString(), password.getText().toString());
                }
            }
        });


        switchToSignup = findViewById(R.id.toSignup);
        Intent signupIntent = new Intent(this,SignupActivity.class);

        switchToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(signupIntent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser(String username, String password) {
        new Thread(() -> {

            UserRepo userRepo = TodoDatabase.getInstance(getApplicationContext()).userRepo();
            if (userRepo.usernameExists(username) == 0) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, getText(R.string.user_not_toast), Toast.LENGTH_SHORT).show());
            } else {
                User user = userRepo.getUserByUsername(username);
                if (user != null && EncryptionUtils.verifyPassword(password, user.getPassword())) {
                    String token = createToken(user.getId(), user.getUsername());
                    saveTokenToSharedPreferences(token);

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, getText(R.string.login_success_toast), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, getText(R.string.login_failed_toast), Toast.LENGTH_SHORT).show();
                    });
                }
            }


        }).start();
    }

    public void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("token", token);
        editor.apply();
    }

}