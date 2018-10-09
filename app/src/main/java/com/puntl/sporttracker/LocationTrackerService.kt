package com.puntl.sporttracker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings

class LocationTrackerService : Service() {

    companion object {
        private const val MIN_TIME = 30000L
        private const val MIN_DISTANCE = 0F
        val locations = ArrayList<Location>()
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                locations.add(location!!)
                val locationsArray = arrayOfNulls<Location>(locations.size)
                locations.toArray(locationsArray)

                val locationUpdateActivity = Intent("location_update")
                locationUpdateActivity.putExtra("locations", locationsArray)
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        locations.clear()
        locationManager.removeUpdates(locationListener)
    }
}