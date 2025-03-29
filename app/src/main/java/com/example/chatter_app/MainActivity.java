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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private TextView sign;
    private EditText user, pass;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views
        login = findViewById(R.id.loginbtn);
        sign = findViewById(R.id.signintext);
        user = findViewById(R.id.email_edit_text); // EditText for email
        pass = findViewById(R.id.password_edit_text); // EditText for password

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Navigate to sign-up activity when 'sign' text is clicked
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, sign_in.class);
                startActivity(intent);
            }
        });

        // Handle login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernamelogin = user.getText().toString().trim();
                String passwordlogin = pass.getText().toString().trim();

                // Check if both fields are filled
                if (usernamelogin.isEmpty() || passwordlogin.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in the details", Toast.LENGTH_SHORT).show();
                } else {
                    login.setBackgroundColor(getResources().getColor(R.color.new_btn));

                    // Perform login
                    auth.signInWithEmailAndPassword(usernamelogin, passwordlogin)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();

                                        // Fetch user details from Firebase (name, phone)
                                        fetchUserDetails();

                                        // Store login state in SharedPreferences
                                        SharedPreferences preferences = getSharedPreferences("MyPrefs2", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("isLoggedIn", true); // Set login status to true
                                        editor.putBoolean("isNewUser", false); // Set user status to false (they are no longer a new user)
                                        editor.apply();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void fetchUserDetails() {
        // Get current user ID
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user node in Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

        // Fetch the user details (username and phone number)
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phoneNumber").getValue(String.class);

                    // Store user details in SharedPreferences
                    SharedPreferences obj2 = getSharedPreferences("MyPrefs1", MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = obj2.edit();
                    editor2.putString("username", username);
                    editor2.putString("phonenumber", phone);
                    editor2.putInt("value", 1);  // You can set other relevant values
                    editor2.apply();

                    SharedPreferences logoutPrefs = getSharedPreferences("LogoutPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor logoutEditor = logoutPrefs.edit();
                    logoutEditor.putBoolean("isLoggedOut", false); // Set logout flag
                    logoutEditor.apply();

                    // Navigate to the next screen (bottom navigation activity)
                    Intent intent = new Intent(MainActivity.this, bottom_navigation.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error fetching user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
