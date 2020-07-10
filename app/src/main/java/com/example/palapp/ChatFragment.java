package com.example.palapp;

import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView chat_verlauf;
    private LinearLayoutManager chatLayoutManager;
    private chatAdapter chatAdapter;

    private ArrayList<NachrichtItem> NachrichtItems;
    private chatAdapter.onItemClickListener listener;
    private EditText textMessage ;
    private Button button;

    private String sender, PasswordSender, recipient, mime, toSendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_chat, container, false);
        button = myView.findViewById(R.id.share_Location);
        setOnClickListener();
        NachrichtItems = new ArrayList<>();

        chat_verlauf = myView.findViewById(R.id.chat_verlauff);
        chat_verlauf.setHasFixedSize(true);
        chatLayoutManager = new LinearLayoutManager(getActivity());
        chatAdapter = new chatAdapter(NachrichtItems, listener);
        chat_verlauf.setLayoutManager(chatLayoutManager);
        chat_verlauf.setAdapter(chatAdapter);
        chatLayoutManager.setStackFromEnd(true);
        textMessage = myView.findViewById(R.id.toSendMessage);

        downloadChat(NachrichtItems);

//        Thread t = new Thread(){
//            @Override
//            public void run(){
//                while(!isInterrupted()){
//                    try{
//                        Thread.sleep(2000);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                downloadChat(NachrichtItems);
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t.start();
        return myView;
    }

    private void downloadChat(ArrayList<NachrichtItem> altNachrichtenItems) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("Username", sender);
            object.addProperty("Password", PasswordSender);
            object.addProperty("Recipient", recipient);

            ChatAsyncTask chatAsyncTask = new ChatAsyncTask(object, altNachrichtenItems ,getActivity().getApplicationContext() , chatAdapter, chat_verlauf, "download");
            NachrichtItems = (ArrayList<NachrichtItem>) chatAsyncTask.execute().get();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

    public void sendClickedFragment(View view){
        mime = "text/plain";
        toSendMessage = textMessage.getText().toString() + "0";

        textMessage.setText("");
        try {
            JsonObject object = new JsonObject();
            object.addProperty("Username", sender);
            object.addProperty("Password", PasswordSender);
            object.addProperty("Recipient", recipient);
            object.addProperty("Mimetype", mime);
            object.addProperty("Data", toSendMessage);

            ChatAsyncTask chatAsyncTask = new ChatAsyncTask(object, NachrichtItems ,getActivity().getApplicationContext(), chatAdapter, chat_verlauf, "send");
            chatAsyncTask.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        textMessage.setText("");
    }

//    private void addLastMessage(ArrayList<NachrichtItem> NachrichtItems) {
//        chatAdapter.updateItems(NachrichtItems);
//        chatAdapter.notifyDataSetChanged();
//        chat_verlauf.scrollToPosition(NachrichtItems.size());
//    }

    //Maps
    private void setOnClickListener() {
        listener = new chatAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity().getApplicationContext(), maps_receiver.class);
                intent.putExtra("latitude" , messageLatitude(NachrichtItems.get(position).getMessage()));
                intent.putExtra("longitude" , messageLongitude(NachrichtItems.get(position).getMessage()));
                startActivity(intent);
            }
        };
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setPasswordSender(String passwordSender) {
        PasswordSender = passwordSender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
