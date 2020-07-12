package com.example.palapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;




public class chatActivity extends AppCompatActivity{

    private RecyclerView chat_verlauf;
    private LinearLayoutManager chatLayoutManager;
    private ArrayList<Transporter> transporters ;
    private chatAdapter chatAdapter;
    private ArrayList<NachrichtItem> NachrichtItems;
    private chatAdapter.onItemClickListener listener;
    private String sendMessage = "http://palaver.se.paluno.uni-due.de/api/message/send";
    private String getAllMessages = "http://palaver.se.paluno.uni-due.de/api/message/get";
    private EditText textMessage;
    private Button button;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button = findViewById(R.id.share_Location);
        NachrichtItems = new ArrayList<>();
        setContentView(R.layout.activity_chat);
        chat_verlauf = findViewById(R.id.chat_verlauf);
        chat_verlauf.setHasFixedSize(true);
        chatLayoutManager = new LinearLayoutManager(this);
        chatAdapter = new chatAdapter(NachrichtItems, listener);
        chat_verlauf.setLayoutManager(chatLayoutManager);
        chat_verlauf.setAdapter(chatAdapter);
        chatLayoutManager.setStackFromEnd(true);


        textMessage = findViewById(R.id.toSendMessage);

        downloadChat(NachrichtItems);



       // Thread t = new Thread(){
        //    @Override
        // public void run(){
        //  while(!isInterrupted()){
        //       try{
        //           Thread.sleep(2000);
        //           new Thread(new Runnable() {
        //               @Override
        //               public void run() {
        //                   downloadChat(NachrichtItems);
        //               }
        //           });
        //       }catch (InterruptedException e) {
        //           e.printStackTrace();
        //       }
        //   }
        // }
        //};
        // t.start();




        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadChat(NachrichtItems);
            }
        });

    }


    @Override
    public void onRestart(){
        super.onRestart();
        downloadChat(NachrichtItems);
    }

    private void downloadChat(ArrayList<NachrichtItem> altNachrichtenItems){
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");

        try {
            JsonObject object = new JsonObject();
            object.addProperty("Username", sender);
            object.addProperty("Password", PasswordSender);
            object.addProperty("Recipient", recipient);

            ChatAsyncTask chatAsyncTask = new ChatAsyncTask(object, altNachrichtenItems ,getApplicationContext() , chatAdapter, chat_verlauf, "download");
            NachrichtItems = (ArrayList<NachrichtItem>) chatAsyncTask.execute().get();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

   // public  String messageLatitude(String message){
     //   String newMessage = message.substring(29);
   //    int space1 = newMessage.indexOf(' ');
    //    String Latitude = newMessage.substring(0,space1);
    //   return Latitude;
   //}
   // public String messageLongitude(String Message){
   //    String newMessage = Message.substring(29);
   //    int space1 = newMessage.indexOf(' ');
   //    int space2 = newMessage.indexOf(' ' , space1+1);
    //    String Longitude = newMessage.substring(space1+1,space2);
    //   return Longitude;
    //}

    public void sendClicked(View view) {
        String sender = getIntent().getStringExtra("sender");
        String PasswordSender = getIntent().getStringExtra("Password");
        String recipient = getIntent().getStringExtra("recipient");
        String mime = "text/plain";
        String toSendMessage = textMessage.getText().toString() + "0";

        try {
            JsonObject object = new JsonObject();
            object.addProperty("Username", sender);
            object.addProperty("Password", PasswordSender);
            object.addProperty("Recipient", recipient);
            object.addProperty("Mimetype", mime);
            object.addProperty("Data", toSendMessage);

            ChatAsyncTask chatAsyncTask = new ChatAsyncTask(object, NachrichtItems ,getApplicationContext(), chatAdapter, chat_verlauf, "send");
            chatAsyncTask.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        textMessage.setText("");
    }

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
}
