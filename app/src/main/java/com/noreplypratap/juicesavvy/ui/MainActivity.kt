package com.noreplypratap.juicesavvy.ui

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.databinding.ActivityMainBinding
import com.noreplypratap.juicesavvy.models.AppUsageData
import com.noreplypratap.juicesavvy.permission.RequestPermission
import com.noreplypratap.juicesavvy.util.formatMilliseconds
import com.noreplypratap.juicesavvy.util.logger


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var appsData: MutableList<AppUsageData> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        RequestPermission.requestPermissions(this)

        getDataNow()

        binding.floatingActionButton.setOnClickListener {
            getDataNow()
        }

    }

    private fun getDataNow() {
        if (hasBatteryUsagePermission()) {
            getDataByApp()
        } else {
            requestBatteryUsagePermission()
        }
    }

    private fun getAppDetails(packageName: String, totalTimeVisible: String): String {
        val pm = packageManager
        try {
            val applicationInfo: ApplicationInfo = pm.getApplicationInfo(packageName, 0)
            val icon: Drawable = applicationContext.packageManager.getApplicationIcon(applicationInfo)
            val name: String = applicationContext.packageManager.getApplicationLabel(applicationInfo).toString()
            appsData.add(AppUsageData(name,totalTimeVisible,icon))

            showDataToUser()
        } catch (e: Exception) {
            logger("Exception in getting App Name")
        }
        return packageName
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDataToUser() {
        val appListAdapter = AppListAdapter(appsData)
        binding.rvList.apply {
            adapter = appListAdapter
        }
        appListAdapter.notifyDataSetChanged()
    }

    private fun getDataByApp() {
        appsData.clear()
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = 0L
        val queryUsageStats: MutableList<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime
        )
        for (usageStats in queryUsageStats) {
            val totalUsageTime = usageStats.totalTimeInForeground
            if (totalUsageTime.toInt() > 1000) {
                val totalTimeInForeground = formatMilliseconds(usageStats.totalTimeInForeground)
                val totalTimeVisible = formatMilliseconds(usageStats.totalTimeVisible)
                getAppDetails(usageStats.packageName,totalTimeVisible)
            }
        }
    }

    private fun getInstalledApps(): List<ApplicationInfo> {
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
    private fun requestBatteryUsagePermission() {
        Toast.makeText(
            this,
            "Battery Usage permission required. Please enable it in settings.",
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
    private fun hasBatteryUsagePermission(): Boolean {
        val appOpsManager =
            getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

}