package com.noreplypratap.juicesavvy.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.noreplypratap.juicesavvy.service.TrackerService
import com.noreplypratap.juicesavvy.util.Constants.TAG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun calculateDuration(startTime: Date, endTime: Date): String {
    val durationMillis = endTime.time - startTime.time

    val seconds = (durationMillis / 1000) % 60
    val minutes = (durationMillis / (1000 * 60)) % 60
    val hours = (durationMillis / (1000 * 60 * 60)) % 24
    val days = (durationMillis / (1000 * 60 * 60 * 24))

    val durationStringBuilder = StringBuilder()

    if (days > 0) {
        durationStringBuilder.append("$days days")
    }

    if (hours > 0) {
        if (durationStringBuilder.isNotEmpty()) {
            durationStringBuilder.append(", ")
        }
        durationStringBuilder.append("$hours hours")
    }

    if (minutes > 0) {
        if (durationStringBuilder.isNotEmpty()) {
            durationStringBuilder.append(", ")
        }
        durationStringBuilder.append("$minutes minutes")
    }

    if (seconds > 0) {
        if (durationStringBuilder.isNotEmpty()) {
            durationStringBuilder.append(", ")
        }
        durationStringBuilder.append("$seconds seconds")
    }

    return durationStringBuilder.toString()
}

fun Context.startTrackerService(
    callback: ((intent: Intent) -> Any)? = null
) {
    val intent = Intent(this, TrackerService::class.java)
    callback?.let {
        callback(intent)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun logger(msg: String) {
    Log.d(TAG,msg)
}

fun Context.sendIntent(action: String) {
    startTrackerService { intent ->
        intent.apply {
            putExtra(Constants.ACTION, action)
            putExtra(Constants.TIMESTAMP, System.currentTimeMillis())
        }
    }
}

fun formatMilliseconds(milliseconds: Long): String {
    val seconds = (milliseconds / 1000).toInt()
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    val stringBuilder = StringBuilder()

    // Handle hours
    if (hours > 0) {
        stringBuilder.append("$hours ${if (hours == 1) "hr" else "hrs"} ")
    }

    // Handle minutes
    if (minutes > 0 || (hours == 0 && remainingSeconds == 0)) {
        stringBuilder.append("$minutes ${if (minutes == 1) "min" else "mins"} ")
    }

    // Handle seconds
    stringBuilder.append("$remainingSeconds ${if (remainingSeconds == 1) "sec" else "secs"}")

    return stringBuilder.toString()
}

fun toFormattedDateAndTime(dateAndTime: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dateAndTime)
}



