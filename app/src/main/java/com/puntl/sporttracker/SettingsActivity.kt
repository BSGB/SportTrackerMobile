package com.puntl.sporttracker

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

const val USER_HEIGHT_DEFAULT = "175"
const val USER_WEIGHT_DEFAULT = "70"
const val USER_GENDER_DEFAULT = "male"
const val USER_BIRTH_DATE_DEFAULT = "1980-01-01"
const val DAILY_STEPS_GOAL_DEFAULT = "1000"
const val BICYCLE_TRACKER_INTERVAL_DEFAULT = "10000"
const val RUN_TRACKER_INTERVAL_DEFAULT = "10000"

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, GeneralPreferenceFragment())
                .commit()
    }

    class GeneralPreferenceFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        private lateinit var dailyStepsGoalKey: String
        private lateinit var userHeightKey: String
        private lateinit var userWeightKey: String
        private lateinit var userGenderKey: String
        private lateinit var userBirthDateKey: String
        private lateinit var bicycleTrackerTimeKey: String
        private lateinit var runTrackerTimeKey: String

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            dailyStepsGoalKey = getString(R.string.daily_steps_goal_key)
            userHeightKey = getString(R.string.user_height_key)
            userWeightKey = getString(R.string.user_weight_key)
            userGenderKey = getString(R.string.user_gender_key)
            userBirthDateKey = getString(R.string.user_birth_date_key)
            bicycleTrackerTimeKey = getString(R.string.bicycle_tracker_time_key)
            runTrackerTimeKey = getString(R.string.run_tracker_time_key)

            val dailyStepsGoalPref = findPreference(dailyStepsGoalKey)
            dailyStepsGoalPref.summary = preferenceManager.sharedPreferences.getString(dailyStepsGoalKey, DAILY_STEPS_GOAL_DEFAULT)

            val userHeightPref = findPreference(userHeightKey)
            userHeightPref.summary = preferenceManager.sharedPreferences.getString(userHeightKey, USER_HEIGHT_DEFAULT)

            val userWeightPref = findPreference(userWeightKey)
            userWeightPref.summary = preferenceManager.sharedPreferences.getString(userWeightKey, USER_WEIGHT_DEFAULT)

            val userGenderPref = findPreference(userGenderKey)
            userGenderPref.summary = preferenceManager.sharedPreferences.getString(userGenderKey, USER_GENDER_DEFAULT)

            val userBirthDatePref = findPreference(userBirthDateKey)
            userBirthDatePref.summary = preferenceManager.sharedPreferences.getString(userBirthDateKey, USER_BIRTH_DATE_DEFAULT)

            val bicycleTrackerTimePref = findPreference(bicycleTrackerTimeKey)
            val bicycleTime = preferenceManager.sharedPreferences.getString(bicycleTrackerTimeKey, BICYCLE_TRACKER_INTERVAL_DEFAULT).toInt() / MILLIS_IN_SECOND
            bicycleTrackerTimePref.summary = bicycleTime.toString()

            val runTrackerTimePref = findPreference(runTrackerTimeKey)
            val runTime = preferenceManager.sharedPreferences.getString(runTrackerTimeKey, RUN_TRACKER_INTERVAL_DEFAULT).toInt() / MILLIS_IN_SECOND
            runTrackerTimePref.summary = runTime.toString()
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
                    val dailyStepsGoal = sharedPreferences!!.getString(key, DAILY_STEPS_GOAL_DEFAULT)
                    if (dailyStepsGoal.isBlank() || dailyStepsGoal.isEmpty() || dailyStepsGoal.startsWith("0"))
                        sharedPreferences.edit().putString(key, "1").apply()

                    dailyStepsGoalPref.summary = sharedPreferences.getString(key, "")
                }

                userHeightKey, userWeightKey -> {
                    val default = when (key) {
                        userHeightKey -> USER_HEIGHT_DEFAULT
                        userWeightKey -> USER_WEIGHT_DEFAULT
                        else -> null
                    }
                    val userPreference = findPreference(key)
                    val userCharacteristic = sharedPreferences!!.getString(key, default)
                    if (userCharacteristic.isBlank() || userCharacteristic.isEmpty() || userCharacteristic.toInt() < 1)
                        sharedPreferences.edit().putString(key, default).apply()

                    userPreference.summary = sharedPreferences.getString(key, "")
                }

                userGenderKey, userBirthDateKey -> {
                    val userPreference = findPreference(key)
                    userPreference.summary = sharedPreferences!!.getString(key, "")
                }

                bicycleTrackerTimeKey, runTrackerTimeKey -> {
                    val userPreference = findPreference(key)
                    val value = sharedPreferences!!.getString(key, "").toInt()
                    userPreference.summary = (value / MILLIS_IN_SECOND).toString()
                }
            }
        }
    }
}
