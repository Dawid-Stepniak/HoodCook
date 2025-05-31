package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private OfferAdapter offerAdapter;
    private UserDatabase database;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        currentUserEmail = getIntent().getStringExtra("user_email");
        database = UserDatabase.getInstance(this);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                return true;
            } else if (id == R.id.nav_favorites) {
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
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

        new Thread(() -> {
            List<Offer> favoriteOffers = database.favoriteDao().getFavoritesForUser(currentUserEmail);
            runOnUiThread(() -> {
                offerAdapter = new OfferAdapter(favoriteOffers, currentUserEmail, database);
                favoritesRecyclerView.setAdapter(offerAdapter);
            });
        }).start();
    }
}