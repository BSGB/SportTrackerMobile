package com.puntl.sporttracker

import android.app.Application
import com.parse.Parse
import com.parse.ParseACL

class StarterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.enableLocalDatastore(this)

        Parse.initialize(Parse.Configuration.Builder(applicationContext)
                .applicationId("20525b79db671747c49de6e730eb03cdf2dc63e5")
                .clientKey("55f31e91fb2cd60bd1fb819c3b7737e5b061e4ee")
                .server("http://18.130.193.120:80/parse/")
                .build())

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}