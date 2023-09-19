package com.noreplypratap.juicesavvy.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.notification.buildNotification
import com.noreplypratap.juicesavvy.receiver.EventReceiver
import com.noreplypratap.juicesavvy.util.Constants.ACTION
import com.noreplypratap.juicesavvy.util.Constants.NOTIFICATION_ID
import com.noreplypratap.juicesavvy.util.Constants.TAG
import com.noreplypratap.juicesavvy.util.formatMilliseconds
import com.noreplypratap.juicesavvy.util.getBatteryUsageInfo
import com.noreplypratap.juicesavvy.util.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val eventReceiver = EventReceiver()

private var screenOnTime: Long = 0L
private var screenOffTime: Long = 0L

private var timeStampOn: Long = System.currentTimeMillis()
private var timeStampOff: Long = System.currentTimeMillis()

private var isScreenOn: Boolean = true


private var lastBatterPercentage: Int = -1
private var currentBatterPercentage: Int = -1
private var lastBatterPercentageTime: Long = 0L
private var currentBatterPercentageTime: Long = 0L

class TrackerService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val handler = Handler()
    private val updateIntervalMillis = 5000L // Update every 5 seconds

    private lateinit var notificationManager: NotificationManager
    private lateinit var powerManager: PowerManager

    override fun onCreate() {
        super.onCreate()

        if (lastBatterPercentage < 0){
            lastBatterPercentage = getBatteryUsageInfo()
            lastBatterPercentageTime = System.currentTimeMillis()
        }

        powerManager = getSystemService(
            Context.POWER_SERVICE
        ) as PowerManager

        notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SHUTDOWN)
        filter.addAction(Intent.ACTION_DREAMING_STARTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(eventReceiver, filter)
        scope.launch {
            val notification = buildAndScheduleNotification()
            withContext(Dispatchers.Main) {
                startForeground(NOTIFICATION_ID, notification)
            }
        }
        handler.postDelayed(updateNotificationTask, updateIntervalMillis)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent?.hasExtra(ACTION) == true) {
            when (intent.getStringExtra(ACTION)) {

                Intent.ACTION_SCREEN_ON -> {
                    logger("ACTION_SCREEN_ON")
                    if (!isScreenOn){
                        onScreenAction()
                    }
                }

                Intent.ACTION_SCREEN_OFF -> if (!isInteractive()) {
                    if (isScreenOn){
                        offScreenAction()
                    }
                    logger("ACTION_SCREEN_OFF")

                }

                //Intent.ACTION_USER_PRESENT -> {}
                //Intent.ACTION_BOOT_COMPLETED -> {}
                //Intent.ACTION_SHUTDOWN -> {}
                //Intent.ACTION_DREAMING_STARTED -> {}
                Intent.ACTION_BATTERY_CHANGED -> {
                    logger("ACTION_BATTERY_CHANGED ... $currentBatterPercentage.")
                    currentBatterPercentageTime = System.currentTimeMillis()
                    currentBatterPercentage = getBatteryUsageInfo()
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(eventReceiver)
        handler.removeCallbacks(updateNotificationTask)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun updateNotification() {
        scope.launch {
            val notification = buildAndScheduleNotification()
            withContext(Dispatchers.Main) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun Context.buildAndScheduleNotification(): Notification {
        return buildNotification(
            R.drawable.emoji_objects,
            getBatteryUsageInfo(),
            formatMilliseconds(screenOnTime),
            formatMilliseconds(screenOffTime)
        )
    }

    private fun cancelNotificationUpdate() {
        handler.removeCallbacks(updateNotificationTask)
    }

    private val updateNotificationTask = object : Runnable {
        override fun run() {
            if (isInteractive()){
                Log.d(TAG, "updateNotificationTask.........")
                val now = System.currentTimeMillis()
                screenOnTime += (now - timeStampOn)
                timeStampOn = now
                updateNotification()
            }
            handler.postDelayed(this, updateIntervalMillis)
        }
    }

    private fun offScreenAction() {
        logger("ACTION_SCREEN_OFF..........")
        timeStampOff = System.currentTimeMillis()
        screenOnTime += (timeStampOff - timeStampOn)
        isScreenOn = false
    }

    private fun onScreenAction() {
        logger("ACTION_SCREEN_ON..........")
        timeStampOn = System.currentTimeMillis()
        screenOffTime += (timeStampOn - timeStampOff)
        updateNotification()
        isScreenOn = true
    }

    private fun isInteractive(): Boolean {
        return powerManager.isInteractive
    }


}

