package com.example.chatter_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class status_fragment extends Fragment {

    private TextView usernameTextView;
    private CircleImageView profileImage;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_status_fragment, container, false);

        // Find the TextView where you want to display the username
        usernameTextView = rootView.findViewById(R.id.statususer_id);
        profileImage=rootView.findViewById(R.id.statusprofileImageid);

        // Retrieve and set saved profile image if exists
        SharedPreferences logoutPrefs = getActivity().getSharedPreferences("LogoutPrefs", Context.MODE_PRIVATE);
        boolean isLoggedOut = logoutPrefs.getBoolean("isLoggedOut", false);

        if (isLoggedOut) {
            // If the user is logged out, set default avatar
            profileImage.setImageResource(R.drawable.dummy_profile);
        } else {
            // Retrieve and set saved profile image if exists
            SharedPreferences preferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
            String savedImageUriString = preferences.getString("profile_image_uri", null);

            if (savedImageUriString != null) {
                profileImage.setImageURI(Uri.parse(savedImageUriString));
            }
        }






        // Retrieve SharedPreferences data
        if (getActivity() != null) {

            SharedPreferences obj2 = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
            String username = obj2.getString("USERNAME", "Haiderali"); // Retrieves stored username
            usernameTextView.setText(username);

        }

        return rootView;
    }
}
