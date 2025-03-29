package com.example.chatter_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class firstfragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private CircleImageView profileImage;
    private Button editButton;
    private TextView usernameTextView, phonenumber;

    public firstfragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firstfragment, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        editButton = view.findViewById(R.id.editButton);
        usernameTextView = view.findViewById(R.id.userNameid);
        phonenumber = view.findViewById(R.id.userPhoneNumber);

        // Retrieve SharedPreferences data
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs1", getContext().MODE_PRIVATE);
        int value=sharedPreferences.getInt("value",0);
        String username = sharedPreferences.getString("USERNAME", "Haiderali"); // Default value if not found
        String phone = sharedPreferences.getString("PHONENUM", "8780929056"); // Default value if not found

        usernameTextView.setText(username);
        phonenumber.setText(phone);


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



        // Set onClickListener for the Edit Button to open gallery
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for storage permission before opening the gallery
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    openGallery();
                }
            }
        });

        return view;
    }

    // Open the gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of image selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Save the selected image URI to SharedPreferences
            if (selectedImageUri != null) {
                SharedPreferences preferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile_image_uri", selectedImageUri.toString());
                editor.apply();

                // Set the selected image URI into the profile image view
                profileImage.setImageURI(selectedImageUri);
            }
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(); // Permission granted, open the gallery
            } else {
                Log.d("firstfragment", "Permission denied for storage.");
            }
        }
    }
}
