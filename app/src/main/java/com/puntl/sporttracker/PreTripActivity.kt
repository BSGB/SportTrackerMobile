package com.puntl.sporttracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View

class PreTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_trip)
        supportActionBar?.title = getString(R.string.introduction_title)
    }

    fun onStartTripClick(view: View) {
        if (!checkPermissions()) {
            startLocationTrackerService()
        }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            true
        } else false
    }

    private fun startLocationTrackerService() {
        val activityType = intent.getSerializableExtra("activity_type")

        val serviceIntent = Intent(applicationContext, LocationTrackerService::class.java)
        serviceIntent.putExtra("activity_type", activityType)
        startService(serviceIntent)

        val tripIntent = Intent(applicationContext, BicycleTripActivity::class.java)
        tripIntent.putExtra("activity_type", activityType)
        startActivity(tripIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startLocationTrackerService()
                    } else {
                        checkPermissions()
                    }
                }
            }
        }
    }
}
