package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    EditText name;
    EditText vorname;
    EditText username ;
    EditText password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        register = findViewById(R.id.registerId);
        name = findViewById(R.id.Name_id);
        vorname = findViewById(R.id.Vorname_id);
        username = findViewById(R.id.Username_id);
        password = findViewById(R.id.password_id);



    }



    public void register ( View view){




    }

}
