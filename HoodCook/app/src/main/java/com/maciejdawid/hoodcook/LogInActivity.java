package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private Button loginBtn;
    private UserDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailET = findViewById(R.id.signup_email);
        passwordET = findViewById(R.id.signp_password);
        loginBtn = findViewById(R.id.signup_btn_final);

        db = UserDatabase.getInstance(this);

        loginBtn.setOnClickListener(v -> {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                User user = db.userDao().getUser(email, password);
                if (user != null) {

                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("user_email", email)
                            .apply();

                    Intent intent = new Intent(LogInActivity.this, HomePageActivity.class);
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Nieprawidłowy email lub hasło", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}