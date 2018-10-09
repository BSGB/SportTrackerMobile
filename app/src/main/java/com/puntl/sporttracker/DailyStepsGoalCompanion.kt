package com.puntl.sporttracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class DailyStepsGoalCompanion {
    companion object {
        private var alarmMgr : AlarmManager? = null

        fun setDailyStepsGoalAlarm(applicationContext : Context) {
            alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmReceiverIntent = Intent(applicationContext, NotifyGoalBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmReceiverIntent, 0)

            alarmMgr?.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    60*60*1000,
                    pendingIntent
            )
        }

        fun isAlarmSet(applicationContext: Context): Boolean {
            return (PendingIntent.getBroadcast(applicationContext, 0,
                    Intent(applicationContext, NotifyGoalBroadcastReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE) != null)
        }
    }
}