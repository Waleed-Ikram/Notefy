package com.example.notefy;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;

    public User(String uid, String name, String email,String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    public String getUid() {
        return uid;
    }

    // Setter for UID
    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter for Name
    public String getName() {
        return name;
    }

    // Setter for Name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for Email
    public String getEmail() {
        return email;
    }

    // Setter for Email
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }

    // Setter for Email
    public void setPhone(String p) {
        this.phone = p;
    }


}
