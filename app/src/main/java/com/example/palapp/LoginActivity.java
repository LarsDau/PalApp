package com.example.palapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn, registerBtn;
    EditText userET, passwordET;
    CheckBox remBox;
    ProgressBar progressBar;
    SharedPreferences preferences;

    public static Boolean fromLogout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.buttonLogin);
        registerBtn = findViewById(R.id.buttonAdd);
        userET = findViewById(R.id.loginUser);
        passwordET = findViewById(R.id.loginPassword);
        remBox = findViewById(R.id.rememberBox);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);

        remBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("rem", true);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putBoolean("rem", false);
                    editor.apply();
                }
            }
        });

        if(savedInstanceState == null){//not null if device rotates, prevents automatic login after every orientationchange/ null at start
            checkRemember();
        }
    }

    public void checkRemember(){
        Boolean rem = preferences.getBoolean("rem", false);
        //Wenn remember ausgewählt
        if(rem && !fromLogout){
            progressBar.setVisibility(View.VISIBLE);
            remBox.setChecked(true);

            String usernameData = preferences.getString("usernameData", "");
            String passwordData = preferences.getString("passwordData", "");

            userET.setText(usernameData);
            passwordET.setText(passwordData);

            loginWithRemember();
        } else if(rem && fromLogout){
            progressBar.setVisibility(View.GONE);
            remBox.setChecked(true);

            String usernameData = preferences.getString("usernameData", "");
            String passwordData = preferences.getString("passwordData", "");

            userET.setText(usernameData);
            passwordET.setText(passwordData);

        } else if(!rem && !fromLogout){
            progressBar.setVisibility(View.GONE);
            remBox.setChecked(false);

            userET.setText("");
            passwordET.setText("");
        } else if(!rem && fromLogout){
            progressBar.setVisibility(View.GONE);
            remBox.setChecked(false);

            userET.setText("");
            passwordET.setText("");
        }
    }

    public void loginWithRemember(){//remember login
        if(userET.getText().length() >= 5 && passwordET.getText().length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", userET.getText().toString());
            params.put("Password", passwordET.getText().toString());
            doLoginRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void startNextActivity(){//start ContactActivity
        Intent intent = new Intent(this , ContactActivity.class);
        intent.putExtra("Sender", preferences.getString("usernameData", ""));
        intent.putExtra("Username", preferences.getString("usernameData", ""));
        intent.putExtra("Password", preferences.getString("passwordData", ""));
        startActivity(intent);
    }

    public void loginClicked(View view){
        String user = userET.getText().toString();
        String password = passwordET.getText().toString();

        if(user.length() >= 5 && password.length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", user);
            params.put("Password", password);

            SharedPreferences.Editor editor= preferences.edit();
            editor.putString("usernameData", userET.getText().toString());
            editor.putString("passwordData", passwordET.getText().toString());
            editor.apply();

            doLoginRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void doLoginRequest(final HashMap<String, String> params){
        System.out.println("Doing request");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.validateUser,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        try {
                            if(response.getString("MsgType").equals("1")){
                                System.out.println("Login successfull");
                                runFCM(params);
                                startNextActivity();
                            }else{
                                System.out.println("Wrong Data for Login");
                            }
                            System.out.println("Request finished");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                        System.out.println("Error: " + error);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void goToRegisterClicked(View view){
        Intent intent = new Intent(this , RegisterActivity.class);
        startActivity(intent);
    }

    public void runFCM(HashMap<String, String> params){
        FCMRunnable fcmRunnable = new FCMRunnable(params);
        Thread thread = new Thread(fcmRunnable);
        thread.start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {//Save Data for Orientationchange
        super.onSaveInstanceState(outState);
    }
}
