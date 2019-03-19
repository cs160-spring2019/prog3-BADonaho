package com.example.cs160_sp18.prog3;

import java.util.Date;

// custom class made for storing a message. you can update this class
public class Comment {

    public String text;
    public String username;
    public Date date;

    Comment(String text, String username, Date date) {
        this.text = text;
        this.username = username;
        this.date = date;
    }

    protected String dateString() {
        return date.toString();
    }
}

