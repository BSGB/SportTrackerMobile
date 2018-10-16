package com.puntl.sporttracker

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, GeneralPreferenceFragment())
                .commit()
    }

    class GeneralPreferenceFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        private lateinit var dailyStepsGoalKey : String

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            dailyStepsGoalKey = getString(R.string.daily_steps_goal_key)

            val dailyStepsGoalPref = findPreference(dailyStepsGoalKey)
            dailyStepsGoalPref.summary = preferenceManager.sharedPreferences.getString(dailyStepsGoalKey, "1000")
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            when (key) {
                dailyStepsGoalKey -> {
                    val dailyStepsGoalPref = findPreference(key)
                    val dailyStepsGoal = sharedPreferences!!.getString(key, "1000")
                    if (dailyStepsGoal.isBlank() || dailyStepsGoal.isEmpty() || dailyStepsGoal.startsWith("0"))
                        sharedPreferences.edit().putString(key, "1").apply()

                    dailyStepsGoalPref.summary = sharedPreferences.getString(key, "")
                }
            }
        }
    }
}
