package com.puntl.sporttracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationTrackerService : Service() {

    companion object {
        private const val MIN_TIME = 3000L
        private const val MIN_DISTANCE = 10F
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private val locations = ArrayList<Location>()

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
        locationManager.removeUpdates(locationListener)
    }
}