package com.noreplypratap.juicesavvy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noreplypratap.juicesavvy.util.sendIntent

class EventReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {
		intent?.action?.let {
			context.sendIntent(it)
		}
	}
}
