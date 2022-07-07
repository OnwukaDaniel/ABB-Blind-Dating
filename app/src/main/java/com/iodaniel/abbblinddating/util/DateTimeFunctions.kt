package com.iodaniel.abbblinddating.util

import java.util.*

object DateTimeFunctions {
    fun getTime(input: String): Pair<String, String> {
        val instance = Calendar.getInstance()
        instance.timeInMillis = input.toLong()
        val hour = if(instance.get(Calendar.HOUR) == 0) 12 else instance.get(Calendar.HOUR)
        val min = if(instance.get(Calendar.MINUTE).toString().length == 1) "${0}${instance.get(Calendar.MINUTE)}" else instance.get(Calendar.MINUTE)
        val amPm = if (instance.get(Calendar.AM_PM) == 0) "AM" else "PM"
        return hour.toString() to "$min:$amPm"
    }

    fun getDay(input: String): String {
        val days: ArrayList<String> = arrayListOf("Today", "Yesterday")
        val months = arrayListOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val nowInstance = Calendar.getInstance().timeInMillis
        val instance = Calendar.getInstance()
        instance.timeInMillis = input.toLong()
        val elapsedTime = nowInstance - instance.timeInMillis
        val elapsedInstance = Calendar.getInstance()
        elapsedInstance.timeInMillis = elapsedTime
        val time = elapsedInstance.get(Calendar.HOUR_OF_DAY)
        return when {
            time <= 24 -> {
                days[0]
            }
            time in 25..47 -> {
                days[1]
            }
            else -> {
                val day = instance.get(Calendar.DAY_OF_MONTH)
                val month = instance.get(Calendar.MONTH)
                val year = instance.get(Calendar.YEAR)
                "$day-${months[month]}-$year"
            }
        }
    }
}