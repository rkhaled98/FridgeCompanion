package com.fridgecompanion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.fridgecompanion.ui.history.HistoryFragment;

/**
 * Class: FridgeNotifications
 * Notes: Class to handle notifications for when food is about to expire, food is low, etc.
 */
public class FridgeNotifications {

    public static NotificationManager notificationManager;
    public static final String CHANNEL_ID = "notification channel";
    public static final int MSG_INVENTORY_LOW = 1;
    public static final int MSG_EXPIRING_SOON = 2;
    public static final int NOTIFY_ID = 101;

    /**
     * Method: showNotification
     * Notes: Method to display notification to user based on the msg type specified
     */
    public static void showNotification(Context context, int msg_type, Food foodItem, String fridgeName) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, HistoryFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notificationBuilder.setContentTitle("Fridge Companion");
        if(MSG_EXPIRING_SOON == msg_type) {
            notificationBuilder.setContentText(foodItem.getFoodName()
                    + " is expiring soon in fridge: " + fridgeName);
        }
        else {
            notificationBuilder.setContentText("NO MESSAGE TYPE SPECIFIED");
        }
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        if(Build.VERSION.SDK_INT > 26) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFY_ID, notification);
    }

    /**
     * Method: cancelNotification
     * Notes: Method to destroy the notification manager
     */
    public static void cancelNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFY_ID);
        }
    }

    /**
     * Method: areNotificationsOn
     * Notes: Helper method to determine whether the user wants to receive notifications or not
     */
    public static boolean areNotificationsOn(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("key_notification_on_off", true)) {
            return false;
        }
        return true;
    }
}
