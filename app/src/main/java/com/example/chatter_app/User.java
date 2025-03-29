package com.example.chatter_app;


public class User {
    private String username;
    private String phone;
    private String gender;
    private String uid;


    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String username, String phone, String gender ,String uid) {
        this.username = username;
        this.phone = phone;
        this.gender = gender;
        this.uid=uid;


    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }
    public void  setUid(String uid) {
        this.uid=uid;
    }
}
