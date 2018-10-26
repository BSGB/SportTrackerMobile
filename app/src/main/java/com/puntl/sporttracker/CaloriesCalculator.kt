package com.puntl.sporttracker

import java.text.SimpleDateFormat
import java.util.*

private const val MIFFLIN_WOMAN_BMR = -161
private const val MIFFLIN_MAN_BMR = 5
private const val MIFFLIN_WEIGHT = 10
private const val MIFFLIN_HEIGHT = 6.25
private const val MIFFLIN_AGE = 5

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
            HomeActivity.ActivityType.RUN -> getRunMet(averageSpeed)
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

    private fun getRunMet(averageSpeed: Float): Double {
        return when {
            averageSpeed < 6.4368 -> 4.5
            averageSpeed in 6.4368..7.88508 -> 6.0
            averageSpeed in 7.88509..8.351748 -> 8.3
            averageSpeed in 8.351749..9.49428 -> 9.0
            averageSpeed in 9.49429..10.62072 -> 9.8
            averageSpeed in 10.62073..11.10348 -> 10.5
            averageSpeed in 11.10349..11.90808 -> 11.0
            averageSpeed in 11.90808..12.71268 -> 11.5
            averageSpeed in 12.71269..13.6782 -> 11.8
            averageSpeed in 13.6783..14.32188 -> 12.3
            averageSpeed in 14.32189..15.93108 -> 12.8
            averageSpeed in 15.93109..17.54028 -> 14.5
            averageSpeed in 17.54029..19.14948 -> 16.0
            averageSpeed in 19.14949..20.75868 -> 19.0
            averageSpeed in 20.75869..22.36788 -> 19.8
            averageSpeed > 22.36789 -> 23.0
            else -> 0.0
        }
    }
}