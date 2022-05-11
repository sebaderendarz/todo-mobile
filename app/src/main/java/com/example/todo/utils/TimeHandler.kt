package com.example.todo.utils

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class TimeHandler {

    @SuppressLint("NewApi")
    fun generateEpochForCurrentTime(): Long {
        return LocalDateTime.now(ZoneId.of("CET")).toEpochSecond(ZoneOffset.UTC)
    }

    fun generateEpochFromTimeString(time: String): Long{
        return Timestamp.valueOf(time).time / 1000
    }

    fun generateTimeStringFromTimeValues(year: Int = 0, month: Int = 0, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0): String {
        val monthWithLeadingZero = if (month < 10) {
            "0$month"
        } else {
            month.toString()
        }
        val dayWithLeadingZero = if (day < 10) {
            "0$day"
        } else {
            day.toString()
        }
        val hourWithLeadingZero = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        val minuteWithLeadingZero = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        val secondWithLeadingZero = if (second < 10) {
            "0$second"
        } else {
            second.toString()
        }
        return "$year-$monthWithLeadingZero-$dayWithLeadingZero $hourWithLeadingZero:$minuteWithLeadingZero:$secondWithLeadingZero"
    }

    fun generateEpochFromTimeValues(
        year: Int = 0,
        month: Int = 0,
        day: Int = 0,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0
    ): Long {
        val timeString = generateTimeStringFromTimeValues(year, month, day, hour, minute, second)
        return Timestamp.valueOf(timeString).time / 1000
    }

    fun generateTimeStringFromEpoch(epoch: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = Date(epoch * 1000)
        return sdf.format(date)
    }
}
