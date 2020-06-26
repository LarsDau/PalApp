package com.example.palapp;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ChatFragment extends Fragment {

    private String sender;
    private String PasswordSender;
    private String recipient;
    private String mime;
    private String toSendMessage;

    private RecyclerView chat_verlauf ;
    private LinearLayoutManager chatLayoutManager ;
    private chatAdapter chatAdapter;
    private ArrayList<NachrichtItem> NachrichtItems  ;
    private chatAdapter.onItemClickListener listener;
    EditText textMessage ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_chat, container, false);

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

        Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()){
                    try{
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
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
        return myView;
    }

    //listener für location button fehlt

    public void deleteChat(){
        for(int i = 0; i < NachrichtItems.size(); i++){
            NachrichtItems.remove(i);
        }
        downloadChat(NachrichtItems);
    }

    private void downloadChat(ArrayList<NachrichtItem> altNachrichtenItems) {
        HashMap<String, String> paramsChatRefresh = new HashMap<>();
        paramsChatRefresh.put("Username", sender);
        paramsChatRefresh.put("Password", PasswordSender);
        paramsChatRefresh.put("Recipient", recipient);
        downloadChatRequest(paramsChatRefresh, altNachrichtenItems);
    }

    public void downloadChatRequest(HashMap<String, String> params, final ArrayList<NachrichtItem> altNachrichtenItems) {
        final ArrayList<NachrichtItem> newNachrichtenItems = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.getConversation,
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
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
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
    public void sendClickedFragment(View view){
        mime = "text/plain";
        toSendMessage = textMessage.getText().toString();

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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.sendMessage,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {



                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        queue.add(postRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////RefreshChat  Adds the last Message to the conversation /////////////////////////
    private void refreshChat(HashMap<String, String> params) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.getConversation, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Data");

                    JSONObject Data = jsonArray.getJSONObject(NachrichtItems.size());
                    String sender = Data.getString("Sender");
                    String message = Data.getString("Data");
                    String DateTime = Data.getString("DateTime");

                    addLastMessage(NachrichtItems);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT);
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
