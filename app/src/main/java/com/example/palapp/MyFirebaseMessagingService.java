package com.example.palapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FCM","OnMessageReceived launch");
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id")
                .setContentTitle("push notification")
                .setContentText(remoteMessage.getData().get("text"))

                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            String name="Channel";
            String description="Description";
            int importance =NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel= new NotificationChannel("id", name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
