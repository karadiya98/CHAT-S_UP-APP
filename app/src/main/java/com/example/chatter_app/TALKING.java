package com.example.chatter_app;

import static com.example.chatter_app.R.layout.activity_talking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TALKING extends AppCompatActivity {

    private LinearLayout container;
    private EditText messageInput;
    private Button sendButton;
    private MaterialToolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String receiverUid, profileHeading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_talking);

        // Set the status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.status));

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        receiverUid = getIntent().getStringExtra("uid");
        profileHeading = getIntent().getStringExtra("NAME");

        toolbar = findViewById(R.id.toolbar_heading);
        setSupportActionBar(toolbar);
        toolbar.setTitle("<--  "+profileHeading);

        // Initialize views
        container = findViewById(R.id.container);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String typedMessage = messageInput.getText().toString();

            if (!typedMessage.isEmpty()) {
                sendMessageToFirebase(typedMessage);
                messageInput.setText("");
            } else {
                Toast.makeText(TALKING.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        showMessagesFromFirebase();
    }

    private void sendMessageToFirebase(String message) {
        String senderUid = mAuth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis(); // Get current timestamp

        DatabaseReference senderRef = databaseReference.child("users")
                .child(senderUid)
                .child("CHAT")
                .child(receiverUid)
                .child("send")
                .child(String.valueOf(timestamp)); // Store under timestamp

        DatabaseReference receiverRef = databaseReference.child("users")
                .child(receiverUid)
                .child("CHAT")
                .child(senderUid)
                .child("received")
                .child(String.valueOf(timestamp)); // Store under timestamp

        Message chatMessage = new Message(message, timestamp); // Add timestamp to message object
        senderRef.setValue(chatMessage);
        receiverRef.setValue(chatMessage);

        addSendingMessage(message, String.valueOf(timestamp));  // Update UI with the message
    }


    private void showMessagesFromFirebase() {
        String currentUid = mAuth.getCurrentUser().getUid();

        DatabaseReference sendRef = databaseReference.child("users").child(currentUid).child("CHAT").child(receiverUid).child("send");
        DatabaseReference receivedRef = databaseReference.child("users").child(currentUid).child("CHAT").child(receiverUid).child("received");

        // Handling Sent Messages
        sendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String message = messageSnapshot.child("message").getValue(String.class);
                        if (message != null) {

                            View sendingView = addSendingMessage(message, messageSnapshot.getKey());
                            TextView sendingText = sendingView.findViewById(R.id.sending);
                            ImageView deleteIcon = sendingView.findViewById(R.id.delete_sended);

                            if (message.equals("Deleted from Me")) {
                                sendingText.setTextColor(Color.GRAY);
                                sendingText.setTextSize(14);
                                sendingText.setTypeface(null, Typeface.ITALIC);
                                deleteIcon.setVisibility(View.GONE);
                            } else if (message.equals("Deleted from Everyone")) {
                                sendingText.setTextColor(Color.RED);
                                sendingText.setTextSize(14);
                                sendingText.setTypeface(null, Typeface.ITALIC);
                                deleteIcon.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch sent messages", databaseError.toException());
            }
        });

        // Handling Received Messages
        receivedRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    String message = dataSnapshot.child("message").getValue(String.class);
                    if (message != null) {
                        View receivedView = addReceivedMessage(message, dataSnapshot.getKey());
                        TextView receivedText = receivedView.findViewById(R.id.display);

                        if (message.equals("Deleted from Me")) {
                            receivedText.setTextColor(Color.GRAY);
                            receivedText.setTextSize(14);
                            receivedText.setTypeface(null, Typeface.ITALIC);
                        } else if (message.equals("Deleted from Everyone")) {
                            receivedText.setTextColor(Color.RED);
                            receivedText.setTextSize(14);
                            receivedText.setTypeface(null, Typeface.ITALIC);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle edits to messages (optional)
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle message deletions (optional)
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Not needed in most chat applications
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch received messages", databaseError.toException());
            }
        });

    }


    private View addSendingMessage(String message, String messageKey) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View sendingView = inflater.inflate(R.layout.activity_sending, container, false);

        TextView sendingText = sendingView.findViewById(R.id.sending);
        sendingText.setText(message);

        ImageView del_sended = sendingView.findViewById(R.id.delete_sended);
        del_sended.setOnClickListener(v -> showDeleteDialog(sendingView, messageKey));

        container.addView(sendingView);
        return sendingView;
    }

    private View addReceivedMessage(String message, String messageKey) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View retrieveView = inflater.inflate(R.layout.activity_retrieve, container, false);

        TextView retrieveText = retrieveView.findViewById(R.id.display);
        retrieveText.setText(message);

        container.addView(retrieveView);
        return retrieveView;
    }

    private void showDeleteDialog(View messageView, String messageKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Message")
                .setMessage("Choose to delete from everyone or just me.")
                .setPositiveButton("Delete from Everyone", (dialog, which) -> {
                    updateMessageInFirebase(messageKey, "Deleted from Everyone", true);
                    updateUIAfterDelete(messageView, "Deleted from Everyone", Color.RED);
                })
                .setNegativeButton("Delete from Me", (dialog, which) -> {
                    updateMessageInFirebase(messageKey, "Deleted from Me", false);
                    updateUIAfterDelete(messageView, "Deleted from Me", Color.GRAY);
                })
                .show();
    }

    private void updateMessageInFirebase(String messageKey, String newMessage, boolean deleteForEveryone) {
        String currentUid = mAuth.getCurrentUser().getUid();
        DatabaseReference sendRef = databaseReference.child("users").child(currentUid).child("CHAT").child(receiverUid).child("send").child(messageKey);

        sendRef.child("message").setValue(newMessage);

        if (deleteForEveryone) {
            DatabaseReference receivedRef = databaseReference.child("users").child(receiverUid).child("CHAT").child(currentUid).child("received").child(messageKey);
            receivedRef.child("message").setValue(newMessage);
        }
    }

    private void updateUIAfterDelete(View messageView, String newText, int textColor) {
        TextView textView;

        if (messageView.findViewById(R.id.sending) != null) {
            textView = messageView.findViewById(R.id.sending);
        } else {
            textView = messageView.findViewById(R.id.display);
        }

        textView.setText(newText);
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.ITALIC);
        textView.setTextColor(textColor);

        View deleteIcon = messageView.findViewById(R.id.delete_sended);
        if (deleteIcon != null) {
            deleteIcon.setVisibility(View.GONE);
        }
    }


    // making custom toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chating_menu, menu); // Reference to your menu XML
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1 :
                int actionSearch = R.id.action_search;// Correct ID as defined in the menu XML
// Handle the search image click action
                return true;

            case 2:
                int actionMore = R.id.action_more;
                // Correct ID as defined in the menu XML
                // Handle the more image click action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}