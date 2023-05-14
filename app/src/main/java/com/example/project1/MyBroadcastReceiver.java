package com.example.project1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract notification title and content from Intent
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        builder = null;

        // Create the NotificationManager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel (necessary for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(channel);


            // Build the notification
            builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.main)  // Make sure this resource exists in your project
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
        } else {
            builder = new NotificationCompat.Builder(context);
        }



        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}
