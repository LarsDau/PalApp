package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn, registerBtn;
    EditText userET, passwordET;

    String validate = "http://palaver.se.paluno.uni-due.de/api/user/validate";
    String next = "0";
    Boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        loginBtn = findViewById(R.id.buttonLogin);
        registerBtn = findViewById(R.id.buttonRegister);
        userET = findViewById(R.id.loginUser);
        passwordET = findViewById(R.id.loginPassword);
    }

    public void goToRegisterClicked(View view){
        Intent intent = new Intent(this , RegisterActivity.class);
        startActivity(intent);
    }

    public void loginClicked(View view){
        //TODO
        //zum Testen

        String user = userET.getText().toString();
        String password = passwordET.getText().toString();
        if(user.length() >= 5 && password.length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", user);
            params.put("Password", password);
            doLoginRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_LONG);
            toast.show();
        }

        if(success){
            Intent intent = new Intent(this , TestLoginActivity.class);
            startActivity(intent);
        }
    }

    public void doLoginRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(validate,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
                        toast.show();

                        String msg = "";

                        try {
                            if(response.getString("MsgType").equals("1")){
                                success = true;
                            }else{
                                success = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println("Error: " + error);
                    }
                }
        );
        queue.add(postRequest);
    }
}
