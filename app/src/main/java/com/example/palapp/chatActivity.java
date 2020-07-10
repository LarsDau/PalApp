package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
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

    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button = findViewById(R.id.share_Location);
        setContentView(R.layout.activity_chat);
        chat_verlauf = findViewById(R.id.chat_verlauf);
         NachrichtItems = downloadChat1();

        chatAdapter = new chatAdapter(NachrichtItems, listener);

        chatLayoutManager = new LinearLayoutManager(this);
        chat_verlauf.setAdapter(chatAdapter);
        chat_verlauf.setHasFixedSize(false);

        chat_verlauf.setLayoutManager(chatLayoutManager);
        //  setOnClickListener();


        chatLayoutManager.setStackFromEnd(true);
        textMessage = findViewById(R.id.toSendMessage);




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
    // private void setOnClickListener() {
        //  listener = new chatAdapter.onItemClickListener() {
            //   @Override
            //   public void onItemClick(View view, int position) {

                //intent.putExtra("latitude" , messageLatitude(NachrichtItems.get(position).getMessage()));
               // intent.putExtra("longitude" , messageLongitude(NachrichtItems.get(position).getMessage()));
           //     intent.putExtra("Message" , NachrichtItems.get(position).getMessage());
         //      startActivity(intent);
                //    }
                //};
                // }



    //Ainas //////////////////////////////////

    private ArrayList<NachrichtItem> insertNewItems(ArrayList<NachrichtItem> oldNachrichtenItems){
        ArrayList<NachrichtItem> newNachrichtenItems = new ArrayList<>();
        newNachrichtenItems = downloadChat1();
        int difference = newNachrichtenItems.size() - oldNachrichtenItems.size();

        if(difference > 0){
            for(int i = oldNachrichtenItems.size() ; i<newNachrichtenItems.size() ; i++){
                oldNachrichtenItems.add(new NachrichtItem(newNachrichtenItems.get(i).getSender(),newNachrichtenItems.get(i).getMessage(),newNachrichtenItems.get(i).getDateTime(),checkClickable(newNachrichtenItems.get(i).getMessage())));
            }
        }
          return oldNachrichtenItems;
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

                                 NachrichtItem newNachricht   = new NachrichtItem(sender,message,DateTime , checkClickable(message));



                                    newNachricht.setMessage(trimMessage(newNachricht.getMessage()));

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
    private ArrayList<NachrichtItem>  downloadChat1() {
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        HashMap<String, String> paramsChatRefresh = new HashMap<>();
        paramsChatRefresh.put("Username", sender);
        paramsChatRefresh.put("Password", PasswordSender);
        paramsChatRefresh.put("Recipient", recipient);
      return   downloadChatRequest1(paramsChatRefresh);
    }

    public ArrayList<NachrichtItem>  downloadChatRequest1(HashMap<String, String> params) {
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

                                NachrichtItem newNachricht   = new NachrichtItem(sender,message,DateTime ,checkClickable(message));

                                     newNachricht.setMessage(trimMessage(newNachricht.getMessage()));



                                newNachrichtenItems.add(newNachricht);

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
        return newNachrichtenItems;
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
    public void sendFileClicked(View view) {
        Intent intent = new Intent(this, storageActivity.class);
        intent.putExtra("Sender", getIntent().getStringExtra("sender"));
        intent.putExtra("password", getIntent().getStringExtra("Password"));
        intent.putExtra("Recipient", getIntent().getStringExtra("recipient"));
        startActivity(intent);
    }





    private int checkClickable(String message) {
        if (message.charAt(message.length() -1 ) == '0') {
            return 0;
        }
        else if (message.charAt(message.length() -1 ) == '1'){
            return 1 ;
        }
        return 2 ;
    }


    private String trimMessage(String message){
        String result = null ;
        if ((message != null) && (message.length() > 0)) {
             result = message.substring(0, message.length() - 1);
        }
        return result;
    }


}
