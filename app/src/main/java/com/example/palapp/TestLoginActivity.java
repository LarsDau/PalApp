package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestLoginActivity extends AppCompatActivity {

    private TextView textView;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        textView = findViewById(R.id.textView);
        textView.setText("Login erfolgreich");

        logout = findViewById(R.id.logoutButton);
    }

    public void logoutClicked(View view){
        Intent intent= new Intent(TestLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        TestLoginActivity.this.finish();
        LoginActivity.fromLogout = true;
    }
}
