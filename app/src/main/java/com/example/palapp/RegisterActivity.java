package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = findViewById(R.id.contactName);
        password = findViewById(R.id.registerPassword);
    }

    public void registerClicked(View view){
        String inputUser = user.getText().toString();
        String inputPassword = password.getText().toString();

        if(inputUser.length() >= 5 && inputPassword.length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", inputUser);
            params.put("Password", inputPassword);
            doRegisterRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username and password must have at least 5 characters", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void doRegisterRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.registerUser,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("MsgType").equals("1")){
                                Toast toast = Toast.makeText(getApplicationContext(), "User is registered now", Toast.LENGTH_LONG);
                                toast.show();
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_LONG);
                                toast.show();
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
                    }
                }
        );
        queue.add(postRequest);
    }
}