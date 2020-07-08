package com.example.palapp;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class FCMRunnable implements Runnable {

    private HashMap<String, String> object;

    public FCMRunnable(HashMap<String, String> object){
        this.object = object;
    }

    @Override
    public void run() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String user = object.get("Username");
        String password = object.get("Password");
        String output = "{\"Username\" : \"" +user+ "\", \"Password\" :\""+password+ "\", \"PushToken\" :\"" + token + "\" }";
        try {
            URL url = new URL(PalaverLinks.pushtoken);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((urlConnection.getOutputStream()));
            outputStreamWriter.write(output);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String text = "";
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            text = sb.toString();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
