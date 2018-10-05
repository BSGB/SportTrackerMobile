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

    companion object {
        private const val TAG = "HomeActivity"
        //calories per step for average person (72kg, average height)
        private const val CALORIES_PER_STEP = 0.04F
    }

    private var sensorManager: SensorManager? = null
    private var previousTotalSteps = 0
    private lateinit var sharedPreferences: SharedPreferences

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
            R.id.settingsMenuItem -> {
                val settingsIntent = Intent(applicationContext, AppSettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /*user clicks on daily steps goal notification - it is necessary to check if user is
        signed in and if isn't -> send back to MainActivity*/
        if(ParseUser.getCurrentUser() == null) {
            val mainIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainIntent)
        }

        //greeting action bar
        supportActionBar?.title = getString(R.string.welcome, ParseUser.getCurrentUser().username)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sharedPreferences = applicationContext.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)

        previousTotalSteps = sharedPreferences.getInt("total_steps", 0)

        //set alarms if not set
        if(!ResetStepsCompanion.isAlarmSet(applicationContext)) ResetStepsCompanion.setResetStepsAlarm(applicationContext)
        if(!DailyStepsGoalCompanion.isAlarmSet(applicationContext)) DailyStepsGoalCompanion.setDailyStepsGoalAlarm(applicationContext)
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

    //do not let user go back to sign in/sign up screen without signing out
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val totalSteps = event!!.values[0].toInt()
        var dailyZero = sharedPreferences.getInt("daily_zero", -1)

        //executes once on first app run - sets total steps as daily zero
        dailyZero = if(dailyZero < 0) {
            sharedPreferences.edit().putInt("daily_zero", totalSteps).apply()
            totalSteps
        } else {
            dailyZero
        }

        //calculate daily steps
        val calculatedSteps = totalSteps + previousTotalSteps - dailyZero

        //show daily steps
        stepsTextView.text = calculatedSteps.toString()

        //calculate burned calories
        val burnedCalories = calculatedSteps * CALORIES_PER_STEP

        //show burned calories
        caloriesTextView.text = getString(R.string.calories, "%.2f".format(burnedCalories))
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {

    }
}
