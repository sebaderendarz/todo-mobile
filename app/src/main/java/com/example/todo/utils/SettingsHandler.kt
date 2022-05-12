package com.example.todo.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SettingsHandler(context: Context) {
    private val tasksCategory = "tasksCategory"
    private val hideDoneTasks = "hideDoneTasks"
    private val showUrgentTasksFirst = "showUrgentTasksFirst"
    private val notificationTime = "notificationTime"
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    fun getTasksCategory(): String {
        return sharedPreferences.getString(tasksCategory, "All") ?: "All"
    }

    fun setTasksCategory(category: String) {
        with(sharedPreferences.edit()) {
            putString(tasksCategory, category)
            apply()
        }
    }

    fun getHideDoneTasks(): Boolean {
        return sharedPreferences.getBoolean(hideDoneTasks, true)
    }

    fun setHideDoneTasks(value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(hideDoneTasks, value)
            apply()
        }
    }

    fun getShowUrgentTasksFirst(): Boolean {
        return sharedPreferences.getBoolean(showUrgentTasksFirst, true)
    }

    fun setShowUrgentTasksFirst(value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(showUrgentTasksFirst, value)
            apply()
        }
    }

    fun getNotificationTime(): Int {
        return sharedPreferences.getInt(notificationTime, 5)
    }

    fun setNotificationTime(timeInMinutes: Int) {
        with(sharedPreferences.edit()) {
            putInt(notificationTime, timeInMinutes)
            apply()
        }
    }
}