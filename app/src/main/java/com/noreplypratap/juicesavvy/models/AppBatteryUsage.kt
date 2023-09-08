package com.noreplypratap.juicesavvy.models

data class AppBatteryUsage(
    val packageName: String,
    val appLabel: String,
    val batteryUsagePercentage: Float
)

