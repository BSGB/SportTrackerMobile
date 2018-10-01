package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!ResetStepsCompanion.isAlarmSet(context!!.applicationContext)) ResetStepsCompanion.setResetStepsAlarm(context.applicationContext)
        if (!DailyStepsGoalCompanion.isAlarmSet(context.applicationContext)) DailyStepsGoalCompanion.setDailyStepsGoalAlarm(context.applicationContext)
    }
}