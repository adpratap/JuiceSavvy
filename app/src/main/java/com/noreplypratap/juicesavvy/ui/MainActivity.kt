package com.noreplypratap.juicesavvy.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.databinding.ActivityMainBinding
import com.noreplypratap.juicesavvy.permission.RequestPermission


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        RequestPermission.requestPermissions(this)
        if (!hasBatteryUsagePermission()) {
            requestBatteryUsagePermission()
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