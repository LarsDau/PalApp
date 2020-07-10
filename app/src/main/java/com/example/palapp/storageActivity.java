package com.example.palapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class storageActivity extends AppCompatActivity {
    private Button choose_file;
    private Button send_image ;
    private final int PICK_IMAGE_REQUEST = 1 ;
    ImageView imageView;
    String sendMessage = "http://palaver.se.paluno.uni-due.de/api/message/send";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        choose_file = findViewById(R.id.choose_file);
        send_image = findViewById(R.id.send_image);
        imageView = findViewById(R.id.Image_send);
        choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openFileChooser();
            }
        });
        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_image_Clicked(v , imageView);
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent ,PICK_IMAGE_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
             imageView.setImageURI(data.getData());
            };
        }
    public void send_image_Clicked(View view , ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        byte[] bb = bos.toByteArray();
        String imageStr = Base64.encodeToString(bb, 0);






        HashMap<String, String> paramsMessage = new HashMap<>();
        paramsMessage.put("Username", getIntent().getStringExtra("Sender"));
        paramsMessage.put("Password", getIntent().getStringExtra("password"));
        paramsMessage.put("Recipient", getIntent().getStringExtra("Recipient"));
        paramsMessage.put("Mimetype", "text/plain");
        paramsMessage.put("Data", imageStr);

        sendMessageRequest(paramsMessage);

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


}