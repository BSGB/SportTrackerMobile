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
        private lateinit var userHeightKey : String
        private lateinit var userWeightKey : String
        private lateinit var userGenderKey : String
        private lateinit var userBirthDateKey : String

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            dailyStepsGoalKey = getString(R.string.daily_steps_goal_key)
            userHeightKey = getString(R.string.user_height_key)
            userWeightKey = getString(R.string.user_weight_key)
            userGenderKey = getString(R.string.user_gender_key)
            userBirthDateKey = getString(R.string.user_birth_date_key)

            val dailyStepsGoalPref = findPreference(dailyStepsGoalKey)
            dailyStepsGoalPref.summary = preferenceManager.sharedPreferences.getString(dailyStepsGoalKey, "1000")

            val userHeightPref = findPreference(userHeightKey)
            userHeightPref.summary = preferenceManager.sharedPreferences.getString(userHeightKey, "175")

            val userWeightPref = findPreference(userWeightKey)
            userWeightPref.summary = preferenceManager.sharedPreferences.getString(userWeightKey, "70")

            val userGenderPref = findPreference(userGenderKey)
            userGenderPref.summary = preferenceManager.sharedPreferences.getString(userGenderKey, "male")

            val userBirthDatePref = findPreference(userBirthDateKey)
            userBirthDatePref.summary = preferenceManager.sharedPreferences.getString(userBirthDateKey, "1980-01-01")
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

                userHeightKey -> {
                    val userHeightPref = findPreference(key)
                    val userHeight = sharedPreferences!!.getString(key, "175")
                    if(userHeight.isBlank() || userHeight.isEmpty() || userHeight.toInt() < 1)
                        sharedPreferences.edit().putString(key, "175").apply()

                    userHeightPref.summary = sharedPreferences.getString(key, "")
                }

                userWeightKey -> {
                    val userWeightPref = findPreference(key)
                    val userWeight = sharedPreferences!!.getString(key, "70")
                    if(userWeight.isBlank() || userWeight.isEmpty() || userWeight.toInt() < 1)
                        sharedPreferences.edit().putString(key, "70").apply()

                    userWeightPref.summary = sharedPreferences.getString(key, "")
                }

                userGenderKey -> {
                    val userGenderPref = findPreference(key)
                    userGenderPref.summary = sharedPreferences!!.getString(key, "")
                }

                userBirthDateKey -> {
                    val userBirthDatePref = findPreference(key)
                    userBirthDatePref.summary = sharedPreferences!!.getString(key, "")
                }
            }
        }
    }
}
