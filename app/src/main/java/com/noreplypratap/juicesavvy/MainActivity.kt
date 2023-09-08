package com.noreplypratap.juicesavvy

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noreplypratap.juicesavvy.databinding.ActivityMainBinding
import com.noreplypratap.juicesavvy.models.AppScreenUsage
import com.noreplypratap.juicesavvy.util.formatMilliseconds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var appsData: MutableList<AppScreenUsage> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val myApp = application as JuicesavvyApplication
        myApp.requestPermissions(this)

        getDataNow()

        binding.floatingActionButton.setOnClickListener {
            getDataNow()
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDataNow() {
        if (hasBatteryUsagePermission()) {
            getDataByApp()
            val appListAdapter = AppListAdapter(appsData)
            binding.rvList.apply {
                adapter = appListAdapter
            }
            appListAdapter.notifyDataSetChanged()
        } else {
            requestBatteryUsagePermission()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)

    private fun getDataByApp() {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = 0L
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime
        )

        for (usageStats in queryUsageStats) {
            val totalUsageTime = usageStats.totalTimeInForeground
            if (totalUsageTime.toInt() > (1000 * 60)) {

                val appName = getAppName(applicationContext, usageStats.packageName)
                val totalTimeInForeground = formatMilliseconds(usageStats.totalTimeInForeground)
                val totalTimeVisible = formatMilliseconds(usageStats.totalTimeVisible)

                appsData.add(AppScreenUsage(appName,totalTimeVisible))


                Log.d("qwertyuiop", "$appName")
                Log.d("qwertyuiop", "totalTimeInForeground : $totalTimeInForeground")
                Log.d("qwertyuiop", "totalTimeVisible : $totalTimeVisible")
                Log.d("qwertyuiop", ".........................")
            }
        }
    }

    private fun getAppName(context: Context, packageName: String): String {
        val pm: PackageManager = context.packageManager
        return try {
            val applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appName = pm.getApplicationLabel(applicationInfo).toString()
            appName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", "Package name not found: $packageName")
            // Handle the error, e.g., return a default value or show a message
            packageName
        }
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