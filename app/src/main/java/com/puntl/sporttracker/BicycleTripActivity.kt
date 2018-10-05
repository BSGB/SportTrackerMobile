package com.puntl.sporttracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class BicycleTripActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val BOUNDS_OFFSET = 100
    }

    private lateinit var mMap: GoogleMap
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val locations = mutableListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicycle_trip)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
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
    }

    override fun onBackPressed() {
        TODO("add dialog for user to confirm exit, exit with save or stay in activity")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(locations.isNotEmpty()) updateMap()
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
