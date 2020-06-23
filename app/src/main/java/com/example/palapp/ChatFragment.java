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

    String sendMessage ="http://palaver.se.paluno.uni-due.de/api/message/send";
    String getAllMessages ="http://palaver.se.paluno.uni-due.de/api/message/get";
    EditText textMessage ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_chat, container, false);

        NachrichtItems = new ArrayList<>();

        chat_verlauf = myView.findViewById(R.id.chat_verlauff);
        chat_verlauf.setHasFixedSize(true);
        chatLayoutManager = new LinearLayoutManager(getActivity());

        chatAdapter = new chatAdapter(NachrichtItems);
        chat_verlauf.setLayoutManager(chatLayoutManager);
        chat_verlauf.setAdapter(chatAdapter);

        chatLayoutManager.setStackFromEnd(true);

        textMessage = myView.findViewById(R.id.toSendMessage);

        downloadChat();
        return myView;
    }

    public void deleteChat(){
        for(int i = 0; i < NachrichtItems.size(); i++){
            NachrichtItems.remove(i);
        }
        downloadChat();
    }

    public void downloadChat() {
        HashMap<String,String> paramsChatRefresh = new HashMap<>();
        paramsChatRefresh.put("Username" , sender);
        paramsChatRefresh.put("Password" , PasswordSender);
        paramsChatRefresh.put("Recipient" , recipient);
        downloadChatRequest(paramsChatRefresh);
    }

    public void downloadChatRequest(HashMap<String, String> params){
        System.out.println(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
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
                        Toast toast = Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        queue.add(postRequest);

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        JsonObjectRequest postRequest = new JsonObjectRequest(sendMessage,
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
