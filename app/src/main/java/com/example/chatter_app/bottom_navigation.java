package com.example.chatter_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottom_navigation extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @SuppressLint({"NonConstantResourceId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Set homefragment as the default fragment

        loadFragment(new com.example.chatter_app.homefragment());


        // Set a listener for item selection in BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {

            // Check which item was clicked
            if (item.getItemId() == R.id.chats_id) {
                // Start the new activity for "Chats"
                return loadFragment(new com.example.chatter_app.homefragment());
            } else if (item.getItemId() == R.id.profile_id) {
                // Replace fragment with Profile fragment
                return loadFragment(new firstfragment());
            } else if (item.getItemId() == R.id.setting_id) {
                // Replace fragment with Settings fragment
                return loadFragment(new secondfragment());
            }

            return false;
        });
    }

    // Method to load fragments dynamically
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            // Start a FragmentTransaction to replace the current fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, fragment); // 'frame' is the container ID for your fragments
            transaction.addToBackStack(null); // Optional, if you want to add the fragment to backstack
            transaction.commit();
            return true;
        }
        return false;
    }
}
