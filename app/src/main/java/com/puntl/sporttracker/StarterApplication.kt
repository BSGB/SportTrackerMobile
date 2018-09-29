package com.puntl.sporttracker

import android.app.Application
import com.parse.Parse
import com.parse.ParseACL

class StarterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.enableLocalDatastore(this)

        Parse.initialize(Parse.Configuration.Builder(applicationContext)
                .applicationId("YOUR_APP_KEY")
                .clientKey("YOUR_CLIENT_KEY")
                .server("YOUR_SERVER_URL")
                .build())

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}