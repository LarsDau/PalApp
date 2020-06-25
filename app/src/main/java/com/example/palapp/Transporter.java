package com.example.palapp;

public class Transporter {


    String longitude;
    String Latitude;
    public Transporter( String Latitude,String Longitude){
        this.Latitude = Latitude;
        this.longitude = Longitude;
    }
    public String getlongitude() {
        return longitude;
    }
    public String getLatitude() {
        return Latitude;
    }
}
