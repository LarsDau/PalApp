package com.example.palapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        createMessage(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "Channel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("id", name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createMessage(RemoteMessage remoteMessage){
        Intent intent;
        if(ContactActivity.tabletMode){
            intent = new Intent(getApplicationContext(), ContactActivity.class);
            if(LoginActivity.preferences.getString("UserNotification", "").equals("") || LoginActivity.preferences.getString("PasswordNotification", "").equals("")){

            }else{
                intent.putExtra("Notification", true);
                intent.putExtra("recipient", remoteMessage.getData().get("sender"));
                intent.putExtra("Password", LoginActivity.preferences.getString("UserNotification", ""));
                intent.putExtra("sender", LoginActivity.preferences.getString("PasswordNotification", ""));
            }
        }else{
            intent = new Intent(getApplicationContext(), chatActivity.class);
            if(LoginActivity.preferences.getString("UserNotification", "").equals("") || LoginActivity.preferences.getString("PasswordNotification", "").equals("")){

            }else{
                intent.putExtra("Notification", true);
                intent.putExtra("recipient", remoteMessage.getData().get("sender"));
                intent.putExtra("Password", LoginActivity.preferences.getString("PasswordNotification", ""));
                intent.putExtra("sender", LoginActivity.preferences.getString("UserNotification", ""));
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "id")
                        .setContentTitle("Received Message")
                        .setContentText(remoteMessage.getData().get("text"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

//    private void sendNotification(String title, String messageBody) {
//        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//            //you can use your launcher Activity insted of SplashActivity, But if the Activity you used here is not launcher Activty than its not work when App is in background.
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////Add Any key-value to pass extras to intent
//        intent.putExtra("pushnotification", "yes");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////For Android Version Orio and greater than orio.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_LOW;
//            NotificationChannel mChannel = new NotificationChannel("Sesame", "Sesame", importance);
//            mChannel.setDescription(messageBody);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//            mNotifyManager.createNotificationChannel(mChannel);
//        }
////For Android Version lower than oreo.
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Seasame");
//        mBuilder.setContentTitle(title)
//                .setContentText(messageBody)
//                .setSmallIcon(R.mipmap.ic_launcher_sesame)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_sesame))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setColor(Color.parseColor("#FFD600"))
//                .setContentIntent(pendingIntent)
//                .setChannelId("Sesame")
//                .setPriority(NotificationCompat.PRIORITY_LOW);
//
//        mNotifyManager.notify(count, mBuilder.build());
//        count++;
//    }
}
