package com.puntl.sporttracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sharedPreferences: SharedPreferences
    private var sensorManager: SensorManager? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.signOutMenuItem -> {
                ParseUser.logOut()
                val mainIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(mainIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //greeting action bar
        supportActionBar?.title = getString(R.string.welcome, ParseUser.getCurrentUser().username)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sharedPreferences = applicationContext.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)

        //check if app runs for the first time
        val isAlarmSet = sharedPreferences.getBoolean("steps_alarm", false)

        //if so, register alarm for daily steps reset && change 'flag'
        if (!isAlarmSet) {
            ResetStepsCompanion.setResetStepsAlarm(this)
            sharedPreferences.edit().putBoolean("steps_alarm", true).apply()
        }
    }

    //on resume sensors config
    override fun onResume() {
        super.onResume()
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "No step counter sensor found.", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    //on pause unregister sensor listener
    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val totalSteps = event!!.values[0].toInt()
        var dailyZero = sharedPreferences.getInt("daily_zero", -1)

        //executes once on first app run - sets total steps as daily zero
        if (dailyZero < 0) sharedPreferences.edit().putInt("daily_zero", totalSteps).apply()

        dailyZero = sharedPreferences.getInt("daily_zero", -1)

        /*checks if daily steps are negative and prevents from displaying it to the user,
        this kind of situation can occur when user reboots its phone and OS
        hasn't run steps reset method yet - if so, message about loading is displayed*/
        stepsTextView.text = if (totalSteps - dailyZero < 0) {
            "loading..."
        } else {
            (totalSteps - dailyZero).toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {

    }
}
