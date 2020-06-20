package com.example.palapp;

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


    String validate = "http://palaver.se.paluno.uni-due.de/api/user/validate";
    Boolean success = false;
    public static Boolean fromLogout = false;
    public static Boolean remember = false;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

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
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                }else{
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });
        checkRemember();
    }

    public void checkRemember(){
        //Wenn remember ausgewählt
        if(remember == true && !fromLogout){
            progressBar.setVisibility(View.VISIBLE);
            remBox.setChecked(true);

            String usernameData = preferences.getString("usernameData", "");
            String passwordData = preferences.getString("passwordData", "");

            userET.setText(usernameData);
            passwordET.setText(passwordData);

            if(usernameData.length() >= 5 && passwordData.length() >= 5){
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", usernameData);
                params.put("Password", passwordData);
                if(!fromLogout){
                    doLoginRequest(params);
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_SHORT);
                toast.show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        startNextActivity();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if(remember == false && !fromLogout){
            progressBar.setVisibility(View.GONE);
            remBox.setChecked(false);

            if(userET.getText().length() >= 5 && passwordET.getText().length() >= 5){
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", userET.getText().toString());
                params.put("Password", passwordET.getText().toString());
                if(!fromLogout){
                    doLoginRequest(params);
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void startNextActivity(){
        if(success){
            Intent intent = new Intent(this , ContactActivity.class);
            intent.putExtra("Username", preferences.getString("usernameData", ""));
            intent.putExtra("Password", preferences.getString("passwordData", ""));
            startActivity(intent);
        }
    }

    public void loginClicked(View view){
        String user = userET.getText().toString();
        String password = passwordET.getText().toString();

        if(user.length() >= 5 && password.length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", user);
            params.put("Password", password);

            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor= preferences.edit();
            editor.putString("usernameData", userET.getText().toString());
            editor.putString("passwordData", passwordET.getText().toString());
            editor.apply();

            doLoginRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_SHORT);
            toast.show();
        }
        startNextActivity();
    }

    public void doLoginRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(validate,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        try {
                            if(response.getString("MsgType").equals("1")){
                                success = true;
                                System.out.println("Response Code: " + response.getString("MsgType"));
                            }else{
                                success = false;
                                System.out.println("Response Code: " + response.getString("MsgType"));
                            }
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
}
