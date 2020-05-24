package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        if(preferences.getString("remember", "").equals("true")){
            goToLoginActivity();
        }else{
            Logoluncher logoluncher= new Logoluncher();
            logoluncher.start();
            goToLoginActivity();
        }
    }

    public void goToLoginActivity(){
        Intent intent= new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        SplashScreenActivity.this.finish();
    }

    private class Logoluncher extends Thread{
        public void run(){
            try{
                sleep(5000);
            }
            catch(InterruptedException e){
              e.printStackTrace();
            }

        }
    }


}
