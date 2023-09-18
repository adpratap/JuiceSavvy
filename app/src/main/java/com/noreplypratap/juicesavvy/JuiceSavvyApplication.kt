package com.noreplypratap.juicesavvy

import android.app.Application
import com.noreplypratap.juicesavvy.util.startTrackerService

class JuiceSavvyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startTrackerService()
    }
}
