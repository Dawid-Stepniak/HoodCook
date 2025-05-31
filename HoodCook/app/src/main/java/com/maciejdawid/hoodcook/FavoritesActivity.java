package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private OfferAdapter offerAdapter;
    private UserDatabase database;
    private String currentUserEmail;
    private List<Offer> favoriteOffers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        currentUserEmail = getIntent().getStringExtra("user_email");
        database = UserDatabase.getInstance(this);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerAdapter = new OfferAdapter(favoriteOffers, currentUserEmail, database);
        favoritesRecyclerView.setAdapter(offerAdapter);

        if(currentUserEmail == null) {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            currentUserEmail = prefs.getString("user_email", null);
            Log.w("USER_EMAIL", "Pobrano email z SharedPreferences: " + currentUserEmail);
        }

        if(currentUserEmail == null) {
            Toast.makeText(this, "Błąd: brak danych użytkownika", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("USER_EMAIL", "Zalogowany użytkownik: " + currentUserEmail);

        loadFavorites();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                return true;
            } else if (id == R.id.nav_favorites) {
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(getApplicationContext(), AddOfferActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            }
            return false;
        });

    }
    private void loadFavorites() {
        Log.d("FAV_DEBUG", "Rozpoczynanie ładowania ulubionych dla: " + currentUserEmail);
        new Thread(() -> {
            try {


                List<Offer> offers = database.favoriteDao().getFavoritesForUser(currentUserEmail);
                Log.d("FAV_DEBUG", "Znalezione oferty: " + offers.size());

                runOnUiThread(() -> {
                    if (offers == null) {
                        Log.e("FAV_DEBUG", "Otrzymano null z bazy!");
                    }
                    offerAdapter.updateList(offers != null ? offers : Collections.emptyList());
                });
            } catch (Exception e) {
                Log.e("FAV_ERROR", "Błąd bazy danych", e);
            }
        }).start();
    }
}