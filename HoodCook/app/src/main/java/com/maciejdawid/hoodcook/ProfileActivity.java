package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstNameTextView, lastNameTextView, emailTextView;
    private Button logoutButton;
    private UserDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        logoutButton = findViewById(R.id.logoutButton);
        database = UserDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        if (userEmail != null) {
            loadUserData(userEmail);
        } else {
            Toast.makeText(this, "Nie znaleziono danych użytkownika", Toast.LENGTH_SHORT).show();
            finish();
        }

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomePageActivity.class));
                return true;
            } else if (id == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.putExtra("user_email", userEmail);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_add) {
                Intent intent = new Intent(this, AddOfferActivity.class);
                intent.putExtra("user_email", userEmail);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserData(String email) {
        new Thread(() -> {
            User user = database.userDao().getUserByEmail(email);
            runOnUiThread(() -> {
                if (user != null) {
                    firstNameTextView.setText(user.firstName);
                    lastNameTextView.setText(user.lastName);
                    emailTextView.setText(user.email);
                } else {
                    Toast.makeText(this, "Nie znaleziono danych użytkownika", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}