package com.shin.hfapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WaterReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "WaterReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a notification
        createNotificationChannel(context);
        sendNotification(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Water Reminder Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void sendNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class); // Change to your activity
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("WaterReminderReceiver", "Sending notification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_local_drink_24)
                .setContentTitle("Water Reminder")
                .setContentText("Time to drink water!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
            Log.d("WaterReminderReceiver", "Notification sent");
        } else {
            Log.e("WaterReminderReceiver", "NotificationManager is null");
        }
    }

}
