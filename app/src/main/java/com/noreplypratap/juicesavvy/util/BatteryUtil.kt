package com.noreplypratap.juicesavvy.util

import android.content.Context
import android.os.BatteryManager

fun Context.getBatteryUsageInfo(): Int {
    val batteryManager =
        getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}
