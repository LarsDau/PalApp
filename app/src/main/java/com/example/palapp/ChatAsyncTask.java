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

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject Data = jsonArray.getJSONObject(i);
                String sender = Data.getString("Sender");
                String message = Data.getString("Data");
                String DateTime = Data.getString("DateTime");
                // NachrichtItem newNachricht = new NachrichtItem(sender, message, DateTime);
                NachrichtItem newNachricht = null ;
                newNachricht = new NachrichtItem(sender,message,DateTime,checkClickable(message));
                System.out.println("MESSAGE: " + newNachricht.getMessage() + "CLICKABLE: "+ newNachricht.getClickable());
                newNachricht.setMessage(trimMessage(newNachricht.getMessage()));
                newNachrichtenItems.add(newNachricht);
                size = newNachrichtenItems.size();
                if (newNachrichtenItems.size() > altNachrichtenItems.size()) {
                    chatAdapter.updateItems(newNachrichtenItems);
                    size = newNachrichtenItems.size();
                }
            }
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
            result = message.substring(0, message.length()-1);
        }
        return result;
    }
}
