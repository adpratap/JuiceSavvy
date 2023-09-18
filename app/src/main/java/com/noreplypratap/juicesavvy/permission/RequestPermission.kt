package com.noreplypratap.juicesavvy.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.noreplypratap.juicesavvy.util.Constants

object RequestPermission {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permissionsToRequest = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE)
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), Constants.PERMISSION_REQUEST_CODE)
            }
        }
    }
}