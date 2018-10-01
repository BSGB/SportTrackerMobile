package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class PowerOffBroadcastReceiver : BroadcastReceiver(), SensorEventListener {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var sensorManager: SensorManager

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = context!!.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)
        sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val totalSteps = p0!!.values[0].toInt()

        //save current total steps + previous reboot steps
        val previousTotalSteps = sharedPreferences.getInt("total_steps", 0)
        sharedPreferences.edit().putInt("total_steps", previousTotalSteps + totalSteps).apply()

        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}