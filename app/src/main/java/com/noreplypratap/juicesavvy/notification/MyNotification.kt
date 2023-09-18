package com.noreplypratap.juicesavvy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.receiver.EventReceiver
import com.noreplypratap.juicesavvy.ui.MainActivity
import com.noreplypratap.juicesavvy.util.Constants.CHANNEL_ID
import com.noreplypratap.juicesavvy.util.Constants.CHANNEL_NAME
import com.noreplypratap.juicesavvy.util.Constants.MARK_AS_READ_ACTION

object MyNotification {
    fun Context.createNotification(
        notificationId: Int,
        batteryLevel: Int,
        screenOn: String,
        screenOff: String,
        channelID: String = CHANNEL_ID,
        channelName: String = CHANNEL_NAME
    ) {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a Notification Channel (required for Android Oreo and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent for the notification click
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("Screen On : $screenOn")
        bigTextStyle.bigText("Screen Off : $screenOff\n" +
                "Battery Level : ~ $batteryLevel%")

        // Create an intent for the "Mark as Read" action
        val markAsReadIntent = Intent(this, EventReceiver::class.java)
        markAsReadIntent.action = MARK_AS_READ_ACTION
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.emoji_objects)
            .setContentTitle("$batteryLevel% ~ Screen On : $screenOn")
            .setStyle(bigTextStyle)
            .addAction(
                R.drawable.emoji_objects,
                "Mark as Read",
                markAsReadPendingIntent
            )
            .addAction(R.drawable.emoji_objects,
                "Save",
                markAsReadPendingIntent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification
        notificationManager.notify(notificationId, builder.build())
    }
}


