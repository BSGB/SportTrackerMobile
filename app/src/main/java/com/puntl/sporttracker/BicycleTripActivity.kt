package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
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

class BicycleTripActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val BOUNDS_OFFSET = 100
    }

    private lateinit var mMap: GoogleMap
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var startTime = 0L
    private val locations = mutableListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicycle_trip)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()

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
                intent!!.extras.getParcelableArray("locations").forEach { locations.add(it as Location) }
                updateMap()
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("location_update"))
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
        mMap.setOnMapLoadedCallback {
            if (locations.isNotEmpty()) updateMap()
        }
    }

    private fun startTimer() {
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                val millis = System.currentTimeMillis() - startTime

                var seconds = millis / 1000
                var minutes = seconds / 60
                val hours = minutes /60

                seconds %= 60
                minutes %= 60

                val secondsString = if(seconds < 10) "0$seconds" else "$seconds"
                val minutesString = if(minutes < 10) "0$minutes" else "$minutes"
                val hoursString = if(hours < 10) "0$hours" else "$hours"

                tripTimeTextView.text = getString(R.string.trip_time, hoursString, minutesString, secondsString)

                handler.postDelayed(this, 1000L)
            }
        }
        runnable.run()
    }

    private fun updateMap() {
        mMap.clear()

        val latLngList = mutableListOf<LatLng>()
        locations.forEach { latLngList.add(LatLng(it.latitude, it.longitude)) }

        mMap.addPolyline(PolylineOptions()
                .clickable(false)
                .addAll(latLngList))

        val builder = LatLngBounds.builder()
        latLngList.forEach { builder.include(it) }
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_OFFSET)
        mMap.addMarker(MarkerOptions().position(latLngList[0]).title("Start"))
        mMap.moveCamera(cameraUpdate)
    }
}
