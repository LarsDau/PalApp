package com.example.palapp;

public class NachrichtItem {
    String sender ;
    String message ;
    String dateTime ;
    int clickable ;
    public NachrichtItem(String sender , String message , String dateTime ){
        this.sender = sender;
        this.message = message;
        this.dateTime = dateTime;


    }
    public String getSender() { return sender;}
    public String getMessage() {
        return message;
    }
    public String getDateTime() {
        return dateTime;
    }
    public int getClickable(){return clickable;}
}
