package com.puntl.sporttracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_home.*

private const val CALORIES_PER_STEP = 0.04F

class HomeActivity : AppCompatActivity(), SensorEventListener {

    enum class ActivityType {
        BICYCLE,
        RUN
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dailyTotalStepsKey: String
    private lateinit var dailyZeroStepsKey: String

    private var sensorManager: SensorManager? = null
    private var previousTotalSteps = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.signOutMenuItem -> {
                ParseUser.logOut()
                val mainIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(mainIntent)
                true
            }
            R.id.settingsMenuItem -> {
                val settingsIntent = Intent(applicationContext, SettingsActivity::class.java)
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
//        if(ParseUser.getCurrentUser() == null) {
//            val mainIntent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(mainIntent)
//        }

        //greeting action bar
//        supportActionBar?.title = getString(R.string.welcome, ParseUser.getCurrentUser().username)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        dailyTotalStepsKey = getString(R.string.daily_total_steps_key)
        dailyZeroStepsKey = getString(R.string.daily_zero_steps_key)

        previousTotalSteps = sharedPreferences.getInt(dailyTotalStepsKey, 0)

        //set alarms if not set
        if (!ResetStepsCompanion.isAlarmSet(applicationContext)) ResetStepsCompanion.setResetStepsAlarm(applicationContext)
        if (!DailyStepsGoalCompanion.isAlarmSet(applicationContext)) DailyStepsGoalCompanion.setDailyStepsGoalAlarm(applicationContext)

        bicycleLinearLayout.setOnClickListener {
            val preTripIntent = Intent(applicationContext, PreTripActivity::class.java)
            preTripIntent.putExtra("activity_type", ActivityType.BICYCLE)
            startActivity(preTripIntent)
        }

        runLinearLayout.setOnClickListener {
            val preTripIntent = Intent(applicationContext, PreTripActivity::class.java)
            preTripIntent.putExtra("activity_type", ActivityType.RUN)
            startActivity(preTripIntent)
        }
    }

    //on resume sensors config
    override fun onResume() {
        super.onResume()
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, getString(R.string.no_step_sensor_message), Toast.LENGTH_SHORT).show()
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
        var dailyZero = sharedPreferences.getInt(dailyZeroStepsKey, -1)

        //executes once on first app run - sets total steps as daily zero
        dailyZero = if (dailyZero < 0) {
            sharedPreferences.edit().putInt(dailyZeroStepsKey, totalSteps).apply()
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
