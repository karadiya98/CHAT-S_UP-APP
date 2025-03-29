package com.example.chatter_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_fragment extends Fragment {

    private LinearLayout ordersContainer;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference databaseReference;
    private CircleImageView profileImage;

    public chat_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);

        // Get reference to the container where profile items will be added
        ordersContainer = view.findViewById(R.id.ordersContainer);
        profileImage = view.findViewById(R.id.chatprofileimageid);

        // Initialize Firebase Database reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabaseReference = firebaseDatabase.getReference("users");

        // Retrieve the user data from Firebase
        retrieveUsersFromDatabase();

        // Return the view to be displayed by the fragment
        return view;
    }

    private void retrieveUsersFromDatabase() {
        // Set a listener to fetch data from Firebase Database
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear any existing items in the container
                ordersContainer.removeAllViews();

                // Get the current user UID
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Loop through the users and add each one to the layout
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String uid = snapshot.getKey();

                        // Pass the user's UID along with other details to the addProfileItem method
                        addProfileItem(user.getUsername(), "Last message text here", "Yesterday", uid, currentUserUid);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(getContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProfileItem(String name, String lastMessage, String date, String uid, String currentUserUid) {
        // Inflate the profile item layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View profileView = inflater.inflate(R.layout.activity_chat_design, ordersContainer, false);

        // Set dynamic values for the profile item
        TextView userName = profileView.findViewById(R.id.userName);
        TextView lastMessageView = profileView.findViewById(R.id.lastMessage);
        TextView dateView = profileView.findViewById(R.id.dateText);

        userName.setText(name);
        lastMessageView.setText(lastMessage);
        dateView.setText(date);

        // If the uid matches the current user's UID, call the addimage method to set the profile image
        if (uid.equals(currentUserUid)) {
            addimage(profileView);
            userName.setText(name+" [YOU] ");
            profileView.setBackgroundResource(R.drawable.own_profile);


        }

        // Set the click listener for profile item
        profileView.setOnClickListener(v -> {

            profileView.setBackgroundResource(R.drawable.clickable_profile);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference reference = databaseReference.child("users");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop through all the users in the database
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the username and uid from the database
                        String username = snapshot.child("username").getValue(String.class);
                        String uid = snapshot.getKey();  // This is the UID of the user



                        // Check if the name from the database matches the clicked name
                        if (username != null && username.equals(name)) {
                            Intent intent = new Intent(getActivity(), TALKING.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("NAME", name);
                            startActivity(intent);

                            // set original backgroun
                            if (uid.equals(currentUserUid)) {
                                profileView.setBackgroundResource(R.drawable.own_profile);
                                break;
                            }

                            profileView.setBackgroundResource(R.drawable.chat_design_layout);

                            break;
                        }


                    }

                             }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that occur during the database query
                    Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Add the profile view to the container
        ordersContainer.addView(profileView);
    }

    // Method to set the profile image for the current user
    private void addimage(View profileView) {
        if (getActivity() == null) {
            return; // Prevent crash if the fragment is not attached
        }

        // Retrieve the saved profile image URI from SharedPreferences
        SharedPreferences logoutPrefs = getActivity().getSharedPreferences("LogoutPrefs", Context.MODE_PRIVATE);
        boolean isLoggedOut = logoutPrefs.getBoolean("isLoggedOut", false);

        // Find the ImageView
        CircleImageView imageView = profileView.findViewById(R.id.chatprofileimageid);

        if (isLoggedOut) {
            // If the user is logged out, set the default avatar
            imageView.setImageResource(R.drawable.dummy_profile);
        } else {
            // Retrieve saved profile image
            SharedPreferences preferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String savedImageUriString = preferences.getString("profile_image_uri", null);

            if (savedImageUriString != null) {
                Uri savedImageUri = Uri.parse(savedImageUriString);
                imageView.setImageURI(savedImageUri);
            } else {
                // If no saved image, set the default avatar
                imageView.setImageResource(R.drawable.dummy_profile);
            }
        }
    }




}
