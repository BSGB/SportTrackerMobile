package com.puntl.sporttracker

import java.text.SimpleDateFormat
import java.util.*

const val MIFFLIN_WOMAN_BMR = -161
const val MIFFLIN_MAN_BMR = 5
const val MIFFLIN_WEIGHT = 10
const val MIFFLIN_HEIGHT = 6.25
const val MIFFLIN_AGE = 5

class CaloriesCalculator(private val activityType: HomeActivity.ActivityType, private val userBMR: Double, private val time: Long) {

    companion object {
        fun getUserBMI(userWeight: Double, userHeight: Double,
                       userAge: Int, userGender: String): Double {

            val genderConst = when (userGender) {
                "male" -> MIFFLIN_MAN_BMR
                "female" -> MIFFLIN_WOMAN_BMR
                else -> MIFFLIN_MAN_BMR
            }

            return MIFFLIN_WEIGHT * userWeight + MIFFLIN_HEIGHT * userHeight - MIFFLIN_AGE * userAge + genderConst
        }

        fun getUserAge(userBirthDateString: String): Int {
            val currentDate = Calendar.getInstance()

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val userBirthDateFormat = formatter.parse(userBirthDateString)
            val userBirthDate = Calendar.getInstance()
            userBirthDate.time = userBirthDateFormat

            return currentDate.get(Calendar.YEAR) - userBirthDate.get(Calendar.YEAR)
        }
    }

    fun getBurnedCalories(averageSpeed: Float): Double {
        val met = getMet(averageSpeed)
        return (userBMR / HOURS_IN_DAY) * met * time / SECONDS_IN_HOUR
    }

    private fun getMet(averageSpeed: Float): Double {
        return when (activityType) {
            HomeActivity.ActivityType.BICYCLE -> getBicycleMet(averageSpeed)
        }
    }

    private fun getBicycleMet(averageSpeed: Float): Double {
        return when {
            averageSpeed < 16.092 -> 4.0
            averageSpeed in 16.092..19.14948 -> 6.8
            averageSpeed in 19.14949..22.36788 -> 8.0
            averageSpeed in 22.36789..25.58628 -> 10.0
            averageSpeed in 25.58629..32.183 -> 12.0
            averageSpeed > 32.183 -> 15.8
            else -> 0.0
        }
    }
}