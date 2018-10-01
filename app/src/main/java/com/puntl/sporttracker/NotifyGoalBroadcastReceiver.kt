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
import android.support.v4.app.NotificationCompat


class NotifyGoalBroadcastReceiver : BroadcastReceiver(), SensorEventListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorManager: SensorManager
    private lateinit var applicationContext : Context
    private var previousTotalSteps = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = context!!.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)
        sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        applicationContext = context
        previousTotalSteps = sharedPreferences.getInt("total_steps", 0)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val totalSteps = p0!!.values[0].toInt()
        val dailyZero = sharedPreferences.getInt("daily_zero", 0)
        val isGoalAlreadyReached = sharedPreferences.getBoolean("daily_goal_reached", false)
        val dailyGoal = sharedPreferences.getInt("daily_goal", 1000)

        if(!isGoalAlreadyReached && totalSteps + previousTotalSteps - dailyZero > dailyGoal) {
            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel("default",
                        "DEFAULT_CHANNEL",
                        NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = "SPORT_TRACKER_NOTIFICATIONS"
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(applicationContext, "default")
                    .setContentTitle("Congratulations!")
                    .setContentText("You have reached your daily steps goal ($dailyGoal steps).")
                    .setContentIntent(PendingIntent.getActivity(applicationContext, 0,
                            Intent(applicationContext, HomeActivity::class.java), 0))
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setSound(ringtone)
                    .build()

            notificationManager.notify(1, notification)
            sharedPreferences.edit().putBoolean("daily_goal_reached", true).apply()
        }

        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}