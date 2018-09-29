package com.puntl.sporttracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class ResetStepsCompanion {
    companion object {
        private var alarmMgr: AlarmManager? = null

        fun setResetStepsAlarm(applicationContext: Context) {
            alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmReceiverIntent = Intent(applicationContext, ResetStepsBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmReceiverIntent, 0)

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }

            alarmMgr?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            )
        }

        fun isAlarmSet(applicationContext: Context): Boolean {
            return (PendingIntent.getBroadcast(applicationContext, 0,
                    Intent(applicationContext, ResetStepsBroadcastReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE) != null)
        }
    }
}