package com.puntl.sporttracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.RingtoneManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat

private const val NOTIFICATION_CHANNEL_ID = "default"
private const val NOTIFICATION_CHANNEL_NAME = "DEFAULT_CHANNEL"
private const val NOTIFICATION_CHANNEL_DESC = "SPORT_TRACKER_NOTIFICATIONS"

class NotifyGoalBroadcastReceiver : BroadcastReceiver(), SensorEventListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorManager: SensorManager
    private lateinit var applicationContext: Context

    private lateinit var dailyTotalStepsKey: String
    private lateinit var dailyZeroStepsKey: String
    private lateinit var dailyStepsGoalKey: String
    private lateinit var dailyGoalReachedKey: String

    private var previousTotalSteps = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        applicationContext = context!!
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        dailyTotalStepsKey = context.getString(R.string.daily_total_steps_key)
        dailyZeroStepsKey = context.getString(R.string.daily_zero_steps_key)
        dailyStepsGoalKey = context.getString(R.string.daily_steps_goal_key)
        dailyGoalReachedKey = context.getString(R.string.daily_goal_reached_key)

        previousTotalSteps = sharedPreferences.getInt(dailyTotalStepsKey, 0)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val totalSteps = p0!!.values[0].toInt()
        val dailyZero = sharedPreferences.getInt(dailyZeroStepsKey, 0)
        val isGoalAlreadyReached = sharedPreferences.getBoolean(dailyGoalReachedKey, false)
        val dailyGoal = sharedPreferences.getString(dailyStepsGoalKey, DAILY_STEPS_GOAL_DEFAULT)

        if (!isGoalAlreadyReached && totalSteps + previousTotalSteps - dailyZero > dailyGoal.toInt()) {
            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = NOTIFICATION_CHANNEL_DESC
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(applicationContext.getString(R.string.content_title))
                    .setContentText(applicationContext.getString(R.string.content_message, dailyGoal))
                    .setContentIntent(PendingIntent.getActivity(applicationContext, 0,
                            Intent(applicationContext, HomeActivity::class.java), 0))
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setSound(ringtone)
                    .build()

            notificationManager.notify(1, notification)
            sharedPreferences.edit().putBoolean(dailyGoalReachedKey, true).apply()
        }

        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}