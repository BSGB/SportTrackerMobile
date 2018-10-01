package com.puntl.sporttracker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_app_settings.*

class AppSettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)

        sharedPreferences = this.getSharedPreferences("com.puntl.sporttracker", Context.MODE_PRIVATE)

        val dailyGoal = sharedPreferences.getInt("daily_goal", 1000)
        dailyGoalEditText.setText(dailyGoal.toString())

        dailyGoalEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!charSequence.isNullOrEmpty() && !charSequence.isNullOrBlank() && charSequence!!.contains(Regex("^[1-9][0-9]*"))) {
                    sharedPreferences.edit().putInt("daily_goal", charSequence!!.toString().toInt()).apply()
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                if(editable.isNullOrBlank() || editable.isNullOrEmpty() || editable.toString().startsWith("0")) {
                    dailyGoalEditText.setText(sharedPreferences.getInt("daily_goal", 1000).toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }
}
