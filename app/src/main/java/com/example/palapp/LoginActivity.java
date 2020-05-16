package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    Button registerBtn;
    EditText userET;
    EditText passwordET;
    String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        loginBtn = findViewById(R.id.buttonLogin);
        registerBtn = findViewById(R.id.buttonRegister);
        userET = findViewById(R.id.editUser);
        passwordET = findViewById(R.id.editPassword);

        urlString = "http://palaver.se.paluno.uni-due.de";
        
    }

    public void loginClicked(View view){
        System.out.println("Login clicked");
    }

    public void registerClicked(View view){
        System.out.println("Register clicked");
    }
}
