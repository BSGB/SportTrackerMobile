package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class BootBroadcastReceiver : BroadcastReceiver() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = context!!.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("daily_zero", 0).apply()
        if (!ResetStepsCompanion.isAlarmSet(context.applicationContext)) ResetStepsCompanion.setResetStepsAlarm(context.applicationContext)
    }
}