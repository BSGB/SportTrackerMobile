package com.puntl.sporttracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.parse.ParseUser

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.title = getString(R.string.welcome, ParseUser.getCurrentUser().username)
    }
}
