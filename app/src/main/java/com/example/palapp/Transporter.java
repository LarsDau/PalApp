package com.example.palapp;

public class Transporter {

   private String url;
    double longitude;
    double Latitude;
    public Transporter(String url , double Latitude,double longitude){
        this.url = url ;
        this.Latitude = Latitude;
        this.longitude = longitude;
    }
    public String geturl() {
        return url;
    }
    public String seturl() {
        return url;
    }



    public double getlongitude() {
        return longitude;
    }
    public double setlonitude(double longitude) {
        return this.longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double setlatitude(double Latitude) {

        return this.Latitude;
    }
}
