package com.example.palapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ChatAsyncTask extends AsyncTask {


    private RecyclerView chat_verlauf;
    private JsonObject object;
    private ArrayList<NachrichtItem> altNachrichtenItems;
    private ArrayList<NachrichtItem> newNachrichtenItems;
    private chatAdapter chatAdapter;
    private Context context;
    private int size;
    private String name;

    public ChatAsyncTask(JsonObject object, ArrayList<NachrichtItem> altNachrichtenItems, Context context, chatAdapter chatAdapter, RecyclerView chat_verlauf, String name){
        this.object = object;
        this.altNachrichtenItems = altNachrichtenItems;
        this.chatAdapter = chatAdapter;
        this.chat_verlauf = chat_verlauf;
        this.context = context;
        this.name = name;
        size = 0;
        newNachrichtenItems = new ArrayList<NachrichtItem>();
    }

    public void sendMessage(JsonObject object, Context context){
        try {
            URL url = new URL(PalaverLinks.sendMessage);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((urlConnection.getOutputStream()));
            outputStreamWriter.write(object.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String text = "";
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
                System.out.println(line);
            }
            text = sb.toString();
            JSONObject message = new JSONObject(text);
            reader.close();

            System.out.println("MESSAGE SEND: " + text + " JSONObject: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadChat(){
        try {
            URL url = new URL(PalaverLinks.getConversation);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((urlConnection.getOutputStream()));
            outputStreamWriter.write(object.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String text = "";
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
                System.out.println(line);
            }
            text = sb.toString();
            JSONObject o = new JSONObject(text);
            JSONArray jsonArray = o.getJSONArray("Data");
            reader.close();

            System.out.println("LENGTH: " + jsonArray.length());
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
                }
                newNachrichtenItems.add(newNachricht);
                size = newNachrichtenItems.size();
                if (newNachrichtenItems.size() > altNachrichtenItems.size()) {
                    chatAdapter.updateItems(newNachrichtenItems);
                    size = newNachrichtenItems.size();
                }
                System.out.println(chatAdapter.getItemCount());
            }
            System.out.println("End of download");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if(name.equals("send")){
            sendMessage(object, context);
            downloadChat();
        }else{
            downloadChat();
        }
        return newNachrichtenItems;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        chatAdapter.notifyDataSetChanged();
        chat_verlauf.scrollToPosition(size);
    }
}
