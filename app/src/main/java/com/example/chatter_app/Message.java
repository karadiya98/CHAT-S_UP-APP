package com.example.chatter_app;

public class Message {
    private String message;


    // Constructor
    public Message(String message, long timestamp) {
        this.message = message;
       // this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
