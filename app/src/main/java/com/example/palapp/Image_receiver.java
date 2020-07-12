package com.example.palapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
public class Image_receiver extends AppCompatActivity {
    Button downloadBtn ;
    ImageView imageView ;
    private final int REQUEST_WRITE_PERMISSION = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_receiver);
          imageView = findViewById(R.id.ImageView_receiver);
          downloadBtn = findViewById(R.id.save_image);
          String Message = getIntent().getStringExtra("Message");
          decodeImage(Message);
          downloadBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                          requestPermissions(new String[] {
                                  android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                      }
                          imageView.setDrawingCacheEnabled(true);
                          Bitmap b = imageView.getDrawingCache();
                          MediaStore.Images.Media.insertImage(getContentResolver(),b ,"title", "Palapp");
                          Toast toast = Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT);
                          toast.show();
              }
          });
            }
            private void decodeImage(String message){
                byte[] decodedString = Base64.decode(message.getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
    }
}