package com.noreplypratap.juicesavvy.viewmodels

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noreplypratap.juicesavvy.models.AppUsageData
import com.noreplypratap.juicesavvy.util.formatMilliseconds
import com.noreplypratap.juicesavvy.util.logger

class AppListViewModel : ViewModel() {

    private val _data = MutableLiveData<List<AppUsageData>>()
    val data: LiveData<List<AppUsageData>> get() = _data

    private var appsData: MutableList<AppUsageData> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.Q)
    fun Context.getDataByApp() {
        appsData.clear()
        val usageStatsManager = getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = 0L
        val queryUsageStats: MutableList<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime
        )
        for (usageStats in queryUsageStats) {
            val totalUsageTime = usageStats.totalTimeInForeground
            if (totalUsageTime.toInt() > 1000) {
                val totalTimeInForeground = formatMilliseconds(totalUsageTime)
                val totalTimeVisible = formatMilliseconds(usageStats.totalTimeVisible)
                getAppDetails(usageStats.packageName,totalTimeVisible)
            }
        }
        _data.postValue(appsData)
    }

    private fun Context.getAppDetails(packageName: String, totalTimeVisible: String): String {
        val pm = packageManager
        try {
            val applicationInfo: ApplicationInfo = pm.getApplicationInfo(packageName, 0)
            val icon: Drawable = applicationContext.packageManager.getApplicationIcon(applicationInfo)
            val name: String = applicationContext.packageManager.getApplicationLabel(applicationInfo).toString()
            appsData.add(AppUsageData(name,totalTimeVisible,icon,packageName))
        } catch (e: Exception) {
            logger("Exception in getting App Name")
        }
        return packageName
    }

    fun Context.getInstalledAppsByUser(): List<ApplicationInfo> {
        val packageManager = packageManager
        val apps: MutableList<ApplicationInfo> = ArrayList()

        val packages = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        for (appInfo in packages) {
            appInfo.flags
            // Filter out system apps (installed by the system)
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                apps.add(appInfo)
            }
        }
        return apps
    }

}