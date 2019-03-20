package com.example.cs160_sp18.prog3;

import java.util.Date;

// custom class made for storing a message. you can update this class
public class Comment {

    public String text;
    public String username;
    public String timestamp;

    Comment(String text, String username, String timestamp) {
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
    }
}

