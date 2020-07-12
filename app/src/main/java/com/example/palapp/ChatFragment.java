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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean running;
    private Thread t;
    private Runnable r;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_chat, container, false);
        button = myView.findViewById(R.id.share_Location);
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

        swipeRefreshLayout = myView.findViewById(R.id.swiperefreshChat);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadChat(NachrichtItems);
                swipeRefreshLayout.setRefreshing(false);
                chatAdapter.notifyDataSetChanged();
            }
        });
        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        System.out.println("OnResume: Running?: " + running);
        r = new Runnable() {
            @Override
            public void run() {
                downloadChat(NachrichtItems);
            }
        };
        if(getActivity() != null && r != null){
            t = new Thread(){
                @Override
                public void run(){
                    while(running){
                        System.out.println("Contact: " + recipient + " Activity: " + getActivity() + " Runnable: " + r);
                        try{
                            t.sleep(2000);
                            if(running){
                                getActivity().runOnUiThread(r);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
        System.out.println("OnPause: Running?: " + running);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        running = false;
        System.out.println("OnDestroyView: Running?: " + running);
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

//    public void updateChatButton(View view){
//        downloadChat(NachrichtItems);
//    }

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
