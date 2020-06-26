package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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




public class chatActivity extends AppCompatActivity {

    private RecyclerView chat_verlauf;
    private LinearLayoutManager chatLayoutManager;
    private  ArrayList<Transporter> transporters ;
    private chatAdapter chatAdapter;
    private ArrayList<NachrichtItem> NachrichtItems;
    private chatAdapter.onItemClickListener listener;
    String sendMessage = "http://palaver.se.paluno.uni-due.de/api/message/send";
    String getAllMessages = "http://palaver.se.paluno.uni-due.de/api/message/get";
    EditText textMessage;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button = findViewById(R.id.share_Location);
        setOnClickListener();
        NachrichtItems = new ArrayList<>();
        downloadChat(NachrichtItems);
        setContentView(R.layout.activity_chat);
        chat_verlauf = findViewById(R.id.chat_verlauf);
        chat_verlauf.setHasFixedSize(true);
        chatLayoutManager = new LinearLayoutManager(this);
        chatAdapter = new chatAdapter(NachrichtItems, listener);
        chat_verlauf.setLayoutManager(chatLayoutManager);
        chat_verlauf.setAdapter(chatAdapter);
        chatLayoutManager.setStackFromEnd(true);
        textMessage = findViewById(R.id.toSendMessage);
        // for notification Service

         Thread t = new Thread(){
           @Override
         public void run(){
           while(!isInterrupted()){
             try{
            Thread.sleep(1000);
          runOnUiThread(new Runnable() {
            @Override
          public void run() {
           downloadChat(NachrichtItems);
        }
        });
        } catch (InterruptedException e) {
           e.printStackTrace();
             }
         }
         }
        };
        t.start();
    }

    ////AINAS
    private void setOnClickListener() {
        listener = new chatAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), maps_receiver.class);
                intent.putExtra("latitude" , messageLatitude(NachrichtItems.get(position).getMessage()));
                intent.putExtra("longitude" , messageLongitude(NachrichtItems.get(position).getMessage()));
                startActivity(intent);
            }
        };
    }

    /////AINAS
    ////////////Download Chat from the server at the beginning ///////////
    private void downloadChat(ArrayList<NachrichtItem> altNachrichtenItems) {
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        HashMap<String, String> paramsChatRefresh = new HashMap<>();
        paramsChatRefresh.put("Username", sender);
        paramsChatRefresh.put("Password", PasswordSender);
        paramsChatRefresh.put("Recipient", recipient);
        downloadChatRequest(paramsChatRefresh, altNachrichtenItems);
    }

    public void downloadChatRequest(HashMap<String, String> params, final ArrayList<NachrichtItem> altNachrichtenItems) {
        final ArrayList<NachrichtItem> newNachrichtenItems = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(getAllMessages,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                 JSONObject Data = jsonArray.getJSONObject(i);
                                 String sender = Data.getString("Sender");
                                 String message = Data.getString("Data");
                                 String DateTime = Data.getString("DateTime");
                                 // NachrichtItem newNachricht = new NachrichtItem(sender, message, DateTime);
                                 NachrichtItem newNachricht = null ;
                                 if(message.charAt(message.length()-1) == '0' ){
                                    newNachricht = new NachrichtItem(sender,message.substring(0,message.length()-1),DateTime,true);
                                  }
                                 else if (message.charAt(message.length()-1 )=='1'){
                                     newNachricht = new NachrichtItem(sender,message.substring(0,message.length()-1),DateTime,false);

                                 }else{
                                     // Ra Bin hier if you want to add DATA in Chat but tell me before you do anything so i can explain to you
                                 }
                                newNachrichtenItems.add(newNachricht);
                                if (newNachrichtenItems.size() > altNachrichtenItems.size()) {
                                    addLastMessage(newNachrichtenItems);
                                }
                                chatAdapter.notifyDataSetChanged();
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
                    }
                });
        queue.add(postRequest);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public  String messageLatitude(String message){
            String newMessage = message.substring(29);

            int space1 = newMessage.indexOf(' ');

            String Latitude = newMessage.substring(0,space1);

            return Latitude;
    }
    public String messageLongitude(String Message){
        String newMessage = Message.substring(29);


        int space1 = newMessage.indexOf(' ');

        int space2 = newMessage.indexOf(' ' , space1+1);
        String Longitude = newMessage.substring(space1+1,space2);
        return Longitude;

    }

    ///////Send Message when button send is clicked //////////////////////////////////////////////
    public void sendClicked(View view) {
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        String mime = "text/plain";
        String toSendMessage = textMessage.getText().toString() + "0";
        HashMap<String, String> paramsMessage = new HashMap<>();
        paramsMessage.put("Username", sender);
        paramsMessage.put("Password", PasswordSender);
        paramsMessage.put("Recipient", recipient);
        paramsMessage.put("Mimetype", mime);
        paramsMessage.put("Data", toSendMessage);
        sendMessageRequest(paramsMessage);
        textMessage.setText("");


    }

    public void sendMessageRequest(HashMap<String, String> params) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(sendMessage,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        queue.add(postRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////RefreshChat  Adds the last Message to the conversation /////////////////////////
    private void refreshChat(HashMap<String, String> params) {


        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest postRequest = new JsonObjectRequest(getAllMessages, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("Data");

                    JSONObject Data = jsonArray.getJSONObject(NachrichtItems.size());
                    String sender = Data.getString("Sender");
                    String message = Data.getString("Data");
                    String DateTime = Data.getString("DateTime");

                    // NachrichtItem newNachricht = new NachrichtItem(sender, message, DateTime);

                    // NachrichtItems.add(newNachricht);


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
                    }
                });
        queue.add(postRequest);
    }

    private void addLastMessage(ArrayList<NachrichtItem> NachrichtItems) {
        chatAdapter.updateItems(NachrichtItems);
        chatAdapter.notifyDataSetChanged();
        chat_verlauf.scrollToPosition(NachrichtItems.size());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void LocationButtonClicked(View view) {
        Intent intent = new Intent(this, Maps_Activity.class);
        intent.putExtra("Sender", getIntent().getStringExtra("sender"));
        intent.putExtra("password", getIntent().getStringExtra("Password"));
        intent.putExtra("Recipient", getIntent().getStringExtra("recipient"));
        startActivity(intent);
    }
}
