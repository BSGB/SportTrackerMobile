package com.puntl.sporttracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*

class BootBroadcastReceiver : BroadcastReceiver() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = context!!.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("daily_zero", 0).apply()
        ResetStepsCompanion.setResetStepsAlarm(context)
    }
}