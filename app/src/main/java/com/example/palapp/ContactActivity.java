package com.example.palapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView contactList;
    private ContactAdapter adapterContactList;
    private RecyclerView.LayoutManager managerContactList;

    private ArrayList<ContactItem> contactItemArrayList;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactItemArrayList = new ArrayList<>();
        contactList = findViewById(R.id.contactlist);
        contactList.setHasFixedSize(true);
        managerContactList = new LinearLayoutManager(this);
        adapterContactList = new ContactAdapter(contactItemArrayList);
        new ItemTouchHelper(helper).attachToRecyclerView(contactList);
        contactList.setLayoutManager(managerContactList);
        contactList.setAdapter(adapterContactList);

        updateList(contactItemArrayList);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(contactItemArrayList);
                adapterContactList.notifyDataSetChanged();
            }
        });
    }

    ItemTouchHelper.SimpleCallback helper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            String deletedUser = contactItemArrayList.get(viewHolder.getAdapterPosition()).getmText1();
            deleteContact(deletedUser);
            contactItemArrayList.remove(viewHolder.getAdapterPosition());
            adapterContactList.notifyDataSetChanged();
        }
    };

    public void updateList(ArrayList<ContactItem> list){
        String getContacts = "http://palaver.se.paluno.uni-due.de/api/friends/get";
        HashMap<String, String> params = new HashMap<>();
        params.put("Username", getIntent().getStringExtra("Username"));
        params.put("Password", getIntent().getStringExtra("Password"));

        adapterContactList.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(getContacts,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        System.out.println("Response: " + response);

                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                            for(int i = 0; i < jsonArray.length(); i++){
                                String data = jsonArray.getString(i);
                                System.out.println("JSONString: " + data);
                                ContactItem c = new ContactItem(data);
                                contactItemArrayList.add(c);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterContactList.notifyDataSetChanged();
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

    public void goToAddContact(View view){
        Intent intent = new Intent(this , AddContactActivity.class);
        intent.putExtra("Username", getIntent().getStringExtra("Username"));
        intent.putExtra("Password", getIntent().getStringExtra("Password"));
        startActivity(intent);
    }

    public void deleteContact(String user){
        String addContact = "http://palaver.se.paluno.uni-due.de/api/friends/remove";

        HashMap<String, String> params = new HashMap<>();
        params.put("Username", getIntent().getStringExtra("Username"));
        params.put("Password", getIntent().getStringExtra("Password"));
        params.put("Friend", user);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(addContact,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
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

    public void logoutClicked(View view){
        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
        LoginActivity.fromLogout = true;
    }
}
