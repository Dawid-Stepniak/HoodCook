package com.maciejdawid.hoodcook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.signup_btn_final);
        Button signUpButton = findViewById(R.id.sign_up_btn);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        RandomQuote();
    }

    private void RandomQuote() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.adviceslip.com/advice");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    JSONObject slip = json.getJSONObject("slip");
                    String advice = slip.getString("advice");

                    runOnUiThread(() -> {
                        TextView quoteText = findViewById(R.id.api);
                        quoteText.setText("\"" + advice);
                    });
                } else {
                    throw new IOException("HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("API", "Błąd połączenia", e);
                runOnUiThread(() -> {
                    TextView quoteText = findViewById(R.id.api);
                    quoteText.setText("Błąd: " + e.getMessage());
                });
            }
        }).start();
    }
}