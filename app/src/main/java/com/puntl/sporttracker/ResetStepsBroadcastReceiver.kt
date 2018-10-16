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

class ResetStepsBroadcastReceiver : BroadcastReceiver(), SensorEventListener{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorManager : SensorManager

    private lateinit var dailyZeroStepsKey : String
    private lateinit var dailyTotalStepsKey : String
    private lateinit var dailyGoalReachedKey : String

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        dailyZeroStepsKey = context.getString(R.string.daily_zero_steps_key)
        dailyTotalStepsKey = context.getString(R.string.daily_total_steps_key)
        dailyGoalReachedKey = context.getString(R.string.daily_goal_reached_key)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val totalSteps = p0!!.values[0].toInt()

        //set current total as new daily zero
        sharedPreferences.edit().putInt(dailyZeroStepsKey, totalSteps).apply()

        //set daily goal to false (unreached)
        sharedPreferences.edit().putBoolean(dailyGoalReachedKey, false).apply()

        //clear total steps (saved in order to keep real counter after reboot)
        sharedPreferences.edit().putInt(dailyTotalStepsKey, 0).apply()

        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}