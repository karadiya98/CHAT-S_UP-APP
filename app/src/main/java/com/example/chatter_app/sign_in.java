package com.example.chatter_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_in extends AppCompatActivity {

    public Button sign;
    public TextView login;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, usernameEditText, phoneEditText, genderEditText;
    private FirebaseAuth auth;
    private String userId;

    public int signinvariable=0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize the input fields
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        genderEditText = findViewById(R.id.Gender_edit_text);

        sign = findViewById(R.id.signinbtn);
        login = findViewById(R.id.logintext);

        // Login Text Click Listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign_in.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Sign In Button Click Listener
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinvariable=1;
                // Validate user input
                if (isValidInput()) {
                    // If input is valid, attempt to create the user with Firebase Authentication
                    auth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                            .addOnCompleteListener(sign_in.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Successfully created the user
                                        userId = auth.getCurrentUser().getUid();
                                        Toast.makeText(getApplicationContext(), "Successfully signed up! Now provide additional details.", Toast.LENGTH_SHORT).show();

                                        // Get the user details
                                        String username = usernameEditText.getText().toString().trim();
                                        String phone = phoneEditText.getText().toString().trim();
                                        String gender = genderEditText.getText().toString().trim();


                                        SharedPreferences obj2 = getSharedPreferences("MyPrefs1", MODE_PRIVATE);
                                        SharedPreferences.Editor editor2 = obj2.edit();
                                        editor2.putString("USERNAME", username); // FIXED: Use variable instead of hardcoded string
                                        editor2.putString("PHONENUM", phone);
                                        editor2.putInt("value", 2);
                                        editor2.apply();


                                        // Create a User object
                                        User newUser = new User(username, phone, gender,"null");

                                        // Get a reference to the Firebase Realtime Database
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                        // Store the user data under the authenticated user's UID
                                        databaseReference.child("users").child(userId).setValue(newUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Successfully stored the user details in the database





                                                            Intent intent = new Intent(sign_in.this, bottom_navigation.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(sign_in.this, "Failed to save user details.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Handle failure
                                        String errorMessage = "Failed to sign up";
                                        if (task.getException() != null) {
                                            errorMessage = task.getException().getMessage();
                                        }
                                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                SharedPreferences preferences = getSharedPreferences("MyPrefs2", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true); // Set login status to true
                editor.putBoolean("isNewUser", false); // Set user status to false (they are no longer a new user)
                editor.apply();

                // for image is null
                SharedPreferences logoutPrefs = getSharedPreferences("LogoutPrefs", MODE_PRIVATE);
                SharedPreferences.Editor logoutEditor = logoutPrefs.edit();
                logoutEditor.putBoolean("isLoggedOut", false); // Set logout flag
                logoutEditor.apply();
            }
        });
    }

    // Validate the input fields
    private boolean isValidInput() {
        // Get the text from input fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        // Validate email
        if (email.isEmpty()) {
            Toast.makeText(sign_in.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate password
        if (password.isEmpty()) {
            Toast.makeText(sign_in.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            Toast.makeText(sign_in.this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(sign_in.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(sign_in.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate phone number
        if (phone.isEmpty()) {
            Toast.makeText(sign_in.this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate gender
        if (gender.isEmpty()) {
            Toast.makeText(sign_in.this, "Please enter your gender.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
