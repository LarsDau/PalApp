package com.example.palapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FCMThread extends Thread{
    @Override
    public void run() {
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            URL url = new URL("http://132.252.60.165/msefcm/api/fcm/notify");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"Text\":\"Demotext\",\"Token\":\"" + token + "\"}");
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String text = "";
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) sb.append(line + "\n");
            text = sb.toString();
            reader.close();
            Log.d("Server", text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
