package com.example.palapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
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

    private AddContactFragment addContactFragment;
    private ChatFragment chatFragment;

    public static boolean tabletMode;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private TextView whichUser;
    private String rec;
    private String password;
    private String sender;
    private RecyclerView contactList;
    private ContactAdapter adapterContactList;
    private RecyclerView.LayoutManager managerContactList;
    private ContactAdapter.onItemClickListener listener;
    private boolean fromNotification;

    private ArrayList<ContactItem> contactItemArrayList;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);
        setOnClickListener();

        fragmentManager = getFragmentManager();

        if(findViewById(R.id.contact2) != null){
            tabletMode = true;
        }else{
            tabletMode = false;
        }


        if(getIntent().hasExtra("Notification")){
            sender = getIntent().getStringExtra("sender");
            rec = getIntent().getStringExtra("recipient");
            password = getIntent().getStringExtra("Password");
            fromNotification = true;
            chatFragment = new ChatFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerForChatAndAddContact, chatFragment);
            fragmentTransaction.commit();
            chatFragment.setSender(sender);
            chatFragment.setPasswordSender(password);
            chatFragment.setRecipient(rec);
            rec = getIntent().getStringExtra("recipient");
        }else{
            fromNotification = false;
            sender = getIntent().getStringExtra("Username");
            password = getIntent().getStringExtra("Password");
        }

        whichUser = findViewById(R.id.whichUser);
        whichUser.setText(sender);

        contactItemArrayList = new ArrayList<>();
        contactList = findViewById(R.id.contactlist);
        contactList.setHasFixedSize(true);
        managerContactList = new LinearLayoutManager(this);
        adapterContactList = new ContactAdapter(contactItemArrayList , listener);
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

    private void setOnClickListener() {
        listener = new ContactAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(tabletMode == false){
                    Intent intent = new Intent(getApplicationContext() , chatActivity.class);
                    intent.putExtra("recipient" , contactItemArrayList.get(position).getmText1() );
                    intent.putExtra("sender" , sender);
                    intent.putExtra("Password" , password);
                    System.out.println("this is what im looking for " +  whichUser.toString());

                    startActivity(intent);
                }else{
                    chatFragment = new ChatFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerForChatAndAddContact, chatFragment);
                    fragmentTransaction.commit();
                    if(getIntent().hasExtra("Notification") && fromNotification){
                        chatFragment.setSender(sender);
                        chatFragment.setPasswordSender(password);
                        rec = getIntent().getStringExtra("recipient");
                        chatFragment.setRecipient(rec);
                        fromNotification = false;
                    }else{
                        chatFragment.setSender(String.valueOf(whichUser.getText()));
                        chatFragment.setPasswordSender(password);
                        chatFragment.setRecipient(contactItemArrayList.get(position).getmText1());
                        rec = contactItemArrayList.get(position).getmText1();
                    }
                }
            }
        };
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
        final HashMap<String, String> params = new HashMap<>();
        if(getIntent().hasExtra("Notification")){
            params.put("Username", sender);
            params.put("Password", password);
        }else{
            params.put("Username", sender);
            params.put("Password", password);
        }

        adapterContactList.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.getContacts,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("MsgType").equals("1")){
                                Toast toast = Toast.makeText(getApplicationContext(), "Your contactlist", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println("Error: " + error);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void deleteContact(final String user){
        final HashMap<String, String> params = new HashMap<>();
        if(getIntent().hasExtra("Notification")){
            params.put("Username", getIntent().getStringExtra("sender"));
            params.put("Password", getIntent().getStringExtra("Password"));
        }else{
            params.put("Username", getIntent().getStringExtra("Username"));
            params.put("Password", getIntent().getStringExtra("Password"));
        }
        params.put("Friend", user);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.deleteContact,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("MsgType").equals("1")){
                                Toast toast = Toast.makeText(getApplicationContext(), params.get("Friend") + " is no longer your friend", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    public void goToAddContact(View view){
        if(tabletMode){
            addContactFragment = new AddContactFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerForChatAndAddContact, addContactFragment);
            fragmentTransaction.commit();
            addContactFragment.setUser(sender);
            addContactFragment.setPassword(password);
        }else{
            Intent intent = new Intent(this , AddContactActivity.class);
            intent.putExtra("Username", sender);
            intent.putExtra("Password", password);
            startActivity(intent);
        }
    }

    public void addContactClickedFragment(View view){
        addContactFragment.addContactClickedFragment(view);
    }

    public void LocationButtonClickedFragment(View view) {
        if(tabletMode){
            Intent intent = new Intent(this, Maps_Activity.class);
            intent.putExtra("Sender", sender);
            intent.putExtra("password", password);
            intent.putExtra("Recipient", rec);
            startActivity(intent);
        }
        System.out.println("CLICKED");
    }

    public void updateChatFragment(View view){
        chatFragment.updateChatButton(view);
    }

    public void sendClickedFragment(View view){
        chatFragment.sendClickedFragment(view);
    }


    public void sendFileClicked(View view) {
        Intent intent = new Intent(this, storageActivity.class);
        intent.putExtra("Sender", sender);
        intent.putExtra("password", password);
        intent.putExtra("Recipient", rec);
        System.out.println(sender + password + rec);
        startActivity(intent);
    }
}