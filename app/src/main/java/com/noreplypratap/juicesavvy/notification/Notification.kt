package com.noreplypratap.juicesavvy.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.ui.MainActivity
import com.noreplypratap.juicesavvy.util.Constants.CHANNEL_ID

@SuppressLint("ObsoleteSdkInt")
fun Context.buildNotification(
    icon: Int,
    batteryLevel: Int,
    screenOn: String,
    screenOff: String
): Notification {

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        Intent(applicationContext, MainActivity::class.java),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else 0
    )

    val bigTextStyle = Notification.BigTextStyle()
    bigTextStyle.setBigContentTitle("Screen On : $screenOn")
    bigTextStyle.bigText(
        "Screen Off : $screenOff\n" +
                "Battery Level : ~ $batteryLevel%"
    )

    val builder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        val noti = Notification.Builder(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            noti.setPriority(Notification.PRIORITY_MIN)
        }
        noti
    } else {
        createChannel()
        Notification.Builder(this, CHANNEL_ID)
    }.setOngoing(true)
        .setOnlyAlertOnce(true)
        .setSmallIcon(icon)
        .setContentTitle("$batteryLevel% ~ Screen On : $screenOn")
        .setStyle(bigTextStyle)
        .setContentIntent(pendingIntent)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
        builder.notification
    } else {
        builder.build()
    }
}

fun Context.createChannel() {
    val nm = getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager
    if (nm.getNotificationChannel(CHANNEL_ID) == null) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = getString(R.string.desc)
        channel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        channel.setSound(null, null)
        channel.setShowBadge(false)
        channel.enableLights(false)
        channel.enableVibration(false)
        nm.createNotificationChannel(channel)
    }
}
