package com.puntl.sporttracker

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_bicycle_trip.*

const val BOUNDS_OFFSET = 100
const val SECONDS_IN_HOUR = 3600
const val METERS_IN_KILOMETER = 1000
const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val MILLIS_IN_SECOND = 1000
const val HOURS_IN_DAY = 24
const val HANDLER_DELAY = 1000L

class BicycleTripActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var userWeightKey: String
    private lateinit var userHeightKey: String
    private lateinit var userBirthDateKey: String
    private lateinit var userGenderKey: String
    private lateinit var caloriesCalculator: CaloriesCalculator
    private lateinit var sharedPreferences: SharedPreferences
    private val locations = mutableListOf<Location>()
    private val latLngList = mutableListOf<LatLng>()
    private val averageSpeedList = mutableListOf<Float>()
    private var startTime = 0L
    private var burnedCalories = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicycle_trip)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        supportActionBar?.hide()
        tripAvgSpeedTextView.text = getString(R.string.calculating)
        tripDistanceTextView.text = getString(R.string.calculating)
        burnedCaloriesTextView.text = getString(R.string.calculating)

        userWeightKey = getString(R.string.user_weight_key)
        userHeightKey = getString(R.string.user_height_key)
        userBirthDateKey = getString(R.string.user_birth_date_key)
        userGenderKey = getString(R.string.user_gender_key)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val userWeight = sharedPreferences.getString(userWeightKey, "70")
        val userHeight = sharedPreferences.getString(userHeightKey, "175")
        val userGender = sharedPreferences.getString(userGenderKey, "male")
        val userBirthDate = sharedPreferences.getString(userBirthDateKey, "1980-01-01")


        val activityType = HomeActivity.ActivityType.BICYCLE
        val userAge = CaloriesCalculator.getUserAge(userBirthDate)
        val userBMI = CaloriesCalculator.getUserBMI(userWeight.toDouble(), userHeight.toDouble(), userAge, userGender)
        caloriesCalculator = CaloriesCalculator(activityType, userBMI, LocationTrackerService.MIN_TIME / MILLIS_IN_SECOND)

        startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        locations.clear()
        locations.addAll(LocationTrackerService.locations)
        try {
            updateMap()
        } catch (e: Exception) {
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                locations.clear()
                intent!!.extras.getParcelableArray(LocationTrackerService.INTENT_EXTRA_LOCATIONS).forEach { locations.add(it as Location) }
                updateMap()
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter(LocationTrackerService.INTENT_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, LocationTrackerService::class.java))
        handler.removeCallbacks(runnable)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun startTimer() {
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                val millis = System.currentTimeMillis() - startTime

                var seconds = millis / MILLIS_IN_SECOND
                var minutes = seconds / SECONDS_IN_MINUTE
                val hours = minutes / MINUTES_IN_HOUR

                seconds %= SECONDS_IN_MINUTE
                minutes %= MINUTES_IN_HOUR

                val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
                val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
                val hoursString = if (hours < 10) "0$hours" else "$hours"

                tripTimeTextView.text = getString(R.string.trip_time, hoursString, minutesString, secondsString)

                handler.postDelayed(this, HANDLER_DELAY)
            }
        }
        runnable.run()
    }

    private fun updateMap() {
        mMap.clear()

        fillLatLngList()

        mMap.addPolyline(PolylineOptions()
                .clickable(false)
                .addAll(latLngList))

        val builder = LatLngBounds.builder()
        latLngList.forEach { builder.include(it) }
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_OFFSET)
        mMap.addMarker(MarkerOptions().position(latLngList[0]).title(getString(R.string.start_pin_title)))
        mMap.moveCamera(cameraUpdate)

        fillAverageSpeedList()

        val averageSpeedInKmH = getAverageSpeed() * SECONDS_IN_HOUR / METERS_IN_KILOMETER
        val tripDistanceInKm = getTripDistance() / METERS_IN_KILOMETER

        tripAvgSpeedTextView.text = getString(R.string.trip_average_speed, "%.2f".format(averageSpeedInKmH))
        tripDistanceTextView.text = getString(R.string.trip_distance, "%.3f".format(tripDistanceInKm))
        burnedCaloriesTextView.text = getString(R.string.trip_calories, "%.2f".format(burnedCalories))
    }

    private fun fillLatLngList() {
        latLngList.clear()
        locations.forEach { latLngList.add(LatLng(it.latitude, it.longitude)) }
    }

    private fun fillAverageSpeedList() {
        averageSpeedList.clear()
        for (i in 0 until locations.size - 1) {
            val distance = locations[i].distanceTo(locations[i + 1])
            val speed = distance / (LocationTrackerService.MIN_TIME / MILLIS_IN_SECOND)
            averageSpeedList.add(speed)
        }

        if (averageSpeedList.isNotEmpty()) {
            val averageSpeed = averageSpeedList[averageSpeedList.size - 1]
            burnedCalories += caloriesCalculator.getBurnedCalories(averageSpeed * SECONDS_IN_HOUR / METERS_IN_KILOMETER)
        }
    }

    private fun getAverageSpeed(): Float {
        return if (averageSpeedList.isNotEmpty()) {
            averageSpeedList.average().toFloat()
        } else 0.0F
    }

    private fun getTripDistance(): Float {
        var distance = 0.0F
        for (i in 0 until locations.size - 1) {
            distance += locations[i].distanceTo(locations[i + 1])
        }
        return distance
    }
}
