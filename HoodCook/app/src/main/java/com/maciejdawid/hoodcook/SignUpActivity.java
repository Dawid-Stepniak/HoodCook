package com.maciejdawid.hoodcook;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {
    EditText firstNameET, surnameET, emailET, passwordET;
    Button signUpBtn;
    UserDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameET = findViewById(R.id.firstName);
        surnameET = findViewById(R.id.surname);
        emailET = findViewById(R.id.signup_email);
        passwordET = findViewById(R.id.signp_password);
        signUpBtn = findViewById(R.id.signup_btn_final);

        db = UserDatabase.getInstance(this);

        signUpBtn.setOnClickListener(v -> {
            String firstName = firstNameET.getText().toString().trim();
            String surname = surnameET.getText().toString().trim();
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(firstName, surname, email, password);
            Executors.newSingleThreadExecutor().execute(() -> {
                db.userDao().insertUser(user);
                Log.d("DB_DEBUG", "Zarejestrowano użytkownika: " + email + ", " + password);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Rejestracja zakończona sukcesem!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });
    }
}