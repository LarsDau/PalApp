package com.example.palapp;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class LoginPreferences {
    public LoginPreferences(){

    }

    public static boolean saveUsername(String user, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("Username", user);
        prefsEditor.apply();
        return true;
    }

    public static String getUsername(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Username", null);
    }

    public static String getPassword(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Password", null);
    }

    public static boolean savePassword(String password, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("Password", password);
        prefsEditor.apply();
        return true;
    }

}
//https://github.com/delaroy/SqliteLogin/blob/sharedpreferences/app/src/main/java/com/delaroystudios/sqlitelogin/utils/PreferenceUtils.java
//
//        https://www.youtube.com/watch?v=HjXlFkBWJCY