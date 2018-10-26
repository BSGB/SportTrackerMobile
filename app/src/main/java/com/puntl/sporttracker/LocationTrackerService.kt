package com.puntl.sporttracker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings

private const val INTENT_EXTRA_LOCATIONS = "locations"

class LocationTrackerService : Service() {

    companion object {
        const val MIN_DISTANCE = 0F
        const val INTENT_ACTION = "location_update"
        val locations = ArrayList<Location>()
        var minTime = 0L
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var activityType: HomeActivity.ActivityType
    private lateinit var bicycleTrackerTimeKey: String
    private lateinit var runTrackerTimeKey: String

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activityType = intent?.getSerializableExtra("activity_type") as HomeActivity.ActivityType
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        bicycleTrackerTimeKey = getString(R.string.bicycle_tracker_time_key)
        runTrackerTimeKey = getString(R.string.run_tracker_time_key)

        minTime = when (activityType) {
            HomeActivity.ActivityType.BICYCLE -> {
                sharedPreferences.getString(bicycleTrackerTimeKey, BICYCLE_TRACKER_INTERVAL_DEFAULT).toLong()
            }

            HomeActivity.ActivityType.RUN -> {
                sharedPreferences.getString(runTrackerTimeKey, RUN_TRACKER_INTERVAL_DEFAULT).toLong()
            }
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                locations.add(location!!)
                val locationsArray = arrayOfNulls<Location>(locations.size)
                locations.toArray(locationsArray)

                val locationUpdateActivity = Intent(INTENT_ACTION)
                locationUpdateActivity.putExtra(INTENT_EXTRA_LOCATIONS, locationsArray)
                sendBroadcast(locationUpdateActivity)
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onProviderDisabled(p0: String?) {
                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(settingsIntent)
            }
        }

        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, MIN_DISTANCE, locationListener)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        locations.clear()
        locationManager.removeUpdates(locationListener)
    }
}