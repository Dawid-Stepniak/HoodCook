package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText searchBar;
    RecyclerView offersRecyclerView;
    OfferAdapter offerAdapter;
    List<Offer> allOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        searchBar = findViewById(R.id.searchBar);
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String currentUserEmail = getIntent().getStringExtra("user_email");
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                return true;
            } else if (id == R.id.nav_add) {
                Intent intent = new Intent(HomePageActivity.this, AddOfferActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            }
            return false;
        });

        UserDatabase database = UserDatabase.getInstance(this);

        new Thread(() -> {
            List<Offer> offers = database.offerDao().getAllOffers();
            runOnUiThread(() -> {
                offerAdapter = new OfferAdapter(offers, currentUserEmail, database);
                offersRecyclerView.setAdapter(offerAdapter);
            });
        }).start();
    }
}
