package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class AddContactActivity extends AppCompatActivity {

    EditText contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contactName = findViewById(R.id.contactName);
    }

    public void addContactClicked(View view){
        addContact();
    }

    public void addContact(){
        String name = contactName.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("Username", getIntent().getStringExtra("Username"));
        params.put("Password", getIntent().getStringExtra("Password"));
        params.put("Friend", name);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.addContact,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        try {
                            if(response.getString("MsgType").equals("1")){
                                System.out.println("Response Code: " + response.getString("MsgType"));
                            }else{
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


}