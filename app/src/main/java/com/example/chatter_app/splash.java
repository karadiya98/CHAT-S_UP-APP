package com.example.chatter_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(1100); // Wait for 1.2 seconds
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Check for login status
                    SharedPreferences preferences = getSharedPreferences("MyPrefs2", MODE_PRIVATE);
                    boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false); // Default to false if not set

                    // Check if the user is new or already logged in
                    boolean isNewUser = preferences.getBoolean("isNewUser", true); // Default to true if not set

                    if (isLoggedIn) {
                        // If the user is logged in, go directly to the bottom_navigation screen
                        Intent intent = new Intent(splash.this, bottom_navigation.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (isNewUser) {
                            // If the user is new, guide them to MainActivity for login/signup
                            Intent intent = new Intent(splash.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If the user is not logged in but not a new user, go to MainActivity for login/signup
                            Intent intent = new Intent(splash.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        };
        thread.start();
    }
}
