package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class AddOfferActivity extends AppCompatActivity {

    private TextInputEditText productNameInput, priceInput;
    private AutoCompleteTextView categoryDropdown;
    private Button addOfferButton;
    private UserDatabase database;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        currentUserEmail = "anna.nowak@example.com"; // Tymczasowo - logowanie

        database = UserDatabase.getInstance(this);

        productNameInput = findViewById(R.id.productNameInput);
        priceInput = findViewById(R.id.priceInput);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        addOfferButton = findViewById(R.id.addOfferButton);

        // kategorie
        String[] categories = new String[]{"Posiłek", "Warzywa", "Owoce", "Nabiał", "Mięso", "Konfitury", "Inne"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryDropdown.setAdapter(adapter);
        categoryDropdown.setKeyListener(null);
        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
        addOfferButton.setOnClickListener(v -> addNewOffer());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_add);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(getApplicationContext(), FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                return true;
            } else if (id == R.id.nav_add) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void addNewOffer() {
        String productName = productNameInput.getText().toString().trim();
        String category = categoryDropdown.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        if (productName.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                Toast.makeText(this, "Cena musi być większa od zera", Toast.LENGTH_SHORT).show();
                return;
            }

            Offer newOffer = new Offer(productName, category, price, currentUserEmail);

            new Thread(() -> {
                database.offerDao().insertOffer(newOffer);
                runOnUiThread(() -> {
                    Toast.makeText(AddOfferActivity.this, "Oferta dodana pomyślnie", Toast.LENGTH_SHORT).show();
                    clearForm();
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Wprowadź poprawną cenę", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        productNameInput.setText("");
        categoryDropdown.setText("");
        priceInput.setText("");
        productNameInput.requestFocus();
    }
}