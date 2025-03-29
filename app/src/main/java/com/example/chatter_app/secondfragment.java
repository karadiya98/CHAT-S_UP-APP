package com.example.chatter_app;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class secondfragment extends Fragment {

    public Button logout;
    private TextView usernameTextView,phonenumber;

    private CircleImageView profileImage;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public secondfragment() {
        // Required empty public constructor
    }

    public static secondfragment newInstance(String param1, String param2) {
        secondfragment fragment = new secondfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_secondfragment, container, false);
        logout = view.findViewById(R.id.logOutButton);




        // Find the TextView or any other UI element where you want to display the username
        usernameTextView = view.findViewById(R.id.usernameid);
        // Retrieve SharedPreferences data
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs1", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Haiderali"); // Default value if not found
        // Set the username in the TextView
        usernameTextView.setText(username);
        profileImage=view.findViewById(R.id.settingprofileimageid);

        // Retrieve and set saved profile image if exists
        SharedPreferences logoutPrefs = getActivity().getSharedPreferences("LogoutPrefs", MODE_PRIVATE);
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


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog to confirm logout
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Confirm", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {

                                // Change button background color on logout confirmation
                                logout.setBackgroundColor(getResources().getColor(R.color.new_logout));
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs2", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", false); // Set to false on logout
                                editor.putBoolean("isNewUser", true); // Reset newUser flag after logout
                                editor.apply();

                               // for image is null
                                SharedPreferences logoutPrefs = getActivity().getSharedPreferences("LogoutPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor logoutEditor = logoutPrefs.edit();
                                logoutEditor.putBoolean("isLoggedOut", true); // Set logout flag
                                logoutEditor.apply();


                                SharedPreferences preferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor imageEditor = preferences.edit();
                                imageEditor.remove("profile_image_uri");  // Remove stored image URI
                                imageEditor.apply();

                                // Redirect to MainActivity (login screen) after logout
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                logout.setBackgroundColor(getResources().getColor(R.color.logout));

                            }
                        })
                        .show();
            }
        });

        return view;
    }





}
