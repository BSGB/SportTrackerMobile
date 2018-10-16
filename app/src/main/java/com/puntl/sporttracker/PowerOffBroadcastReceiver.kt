package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.preference.PreferenceManager

class PowerOffBroadcastReceiver : BroadcastReceiver(), SensorEventListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorManager: SensorManager

    private lateinit var dailyTotalStepsKey : String

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!.applicationContext)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        dailyTotalStepsKey = context.getString(R.string.daily_total_steps_key)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val totalSteps = p0!!.values[0].toInt()

        //save current total steps + previous reboot steps
        val previousTotalSteps = sharedPreferences.getInt(dailyTotalStepsKey, 0)
        sharedPreferences.edit().putInt(dailyTotalStepsKey, previousTotalSteps + totalSteps).apply()

        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}