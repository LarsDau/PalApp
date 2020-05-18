package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String base = "http://palaver.se.paluno.uni-due.de";
    String register = "http://palaver.se.paluno.uni-due.de/api/user/register";
    String validate = "http://palaver.se.paluno.uni-due.de/api/user/register/api/user/validate";
    String change = "http://palaver.se.paluno.uni-due.de/api/user/password";

    EditText user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = findViewById(R.id.registerUser);
        password = findViewById(R.id.registerPassword);
    }

    public void registerClicked(View view){
        String inputUser = user.getText().toString();
        String inputPassword = password.getText().toString();

        if(inputUser.length() >= 5 && inputPassword.length() >= 5){
            HashMap<String, String> params = new HashMap<>();
            params.put("Username", inputUser);
            params.put("Password", inputPassword);
            doRequest(params);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Username und Passwort müssen mind. 5 Zeichen haben", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void doRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(register,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
                        toast.show();
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