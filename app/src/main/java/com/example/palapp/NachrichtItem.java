package com.example.palapp;

public class NachrichtItem {
    String sender;
    String message;
    String dateTime;
    boolean clickable = false ;

    public NachrichtItem(String sender, String message, String dateTime , boolean clickable) {
        this.sender = sender;
        this.message = message;
        this.dateTime = dateTime;
        this.clickable = clickable;

    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
}