package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
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


    private RecyclerView chat_verlauf ;
    private LinearLayoutManager chatLayoutManager ;
    private chatAdapter chatAdapter;
    private ArrayList<NachrichtItem> NachrichtItems  ;

    String sendMessage ="http://palaver.se.paluno.uni-due.de/api/message/send";
    String getAllMessages ="http://palaver.se.paluno.uni-due.de/api/message/get";


    EditText textMessage ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        downloadChat();
        setContentView(R.layout.activity_chat);
        NachrichtItems = new ArrayList<>();

        chat_verlauf = findViewById(R.id.chat_verlauf);
        chat_verlauf.setHasFixedSize(true);
        chatLayoutManager = new LinearLayoutManager(this);


        chatAdapter = new chatAdapter(NachrichtItems);
        chat_verlauf.setLayoutManager(chatLayoutManager);
        chat_verlauf.setAdapter(chatAdapter);

        chatLayoutManager.setStackFromEnd(true);

        textMessage = findViewById(R.id.toSendMessage);


    }





   ////////////Download Chat from the server at the beginning ///////////
    private void downloadChat() {
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        HashMap<String,String> paramsChatRefresh = new HashMap<>();
        paramsChatRefresh.put("Username" , sender);
        paramsChatRefresh.put("Password" , PasswordSender);
        paramsChatRefresh.put("Recipient" , recipient);
        downloadChatRequest(paramsChatRefresh);
    }

    public void downloadChatRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(getAllMessages,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {



                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");
                            for(int i = 0 ; i<jsonArray.length();i++){
                                JSONObject Data =  jsonArray.getJSONObject(i);
                                String sender = Data.getString("Sender");
                                String message = Data.getString("Data");
                                String DateTime = Data.getString("DateTime");
                                NachrichtItem newNachricht = new NachrichtItem(sender,message,DateTime);


                                NachrichtItems.add(newNachricht);
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












    ///////Send Message when button send is clicked //////////////////////////////////////////////
    public void sendClicked(View view){
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        String mime = "text/plain";
        String toSendMessage = textMessage.getText().toString();


        HashMap<String,String> paramsMessage = new HashMap<>();
        paramsMessage.put("Username" , sender);
        paramsMessage.put("Password" , PasswordSender);
        paramsMessage.put("Recipient" , recipient);
        paramsMessage.put("Mimetype" , mime);
        paramsMessage.put("Data", toSendMessage);
        sendMessageRequest(paramsMessage);

        textMessage.setText("");
        refreshChat(paramsMessage);

    }

    public void sendMessageRequest(HashMap<String, String> params){
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

                    NachrichtItem newNacho = new NachrichtItem(sender, message, DateTime);

                    NachrichtItems.add(newNacho);

                   addLastMessage(NachrichtItems);

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
}