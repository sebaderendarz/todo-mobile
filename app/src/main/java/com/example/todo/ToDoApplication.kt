package com.example.todo

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todo.notifications.NotificationsHandler
import com.example.todo.utils.SettingsHandler

class ToDoApplication: Application() {
    lateinit var settings: SettingsHandler
    private lateinit var notificationsHandler: NotificationsHandler

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        settings = SettingsHandler(this)
        notificationsHandler = NotificationsHandler(this, settings)
        notificationsHandler.registerDefaultNotificationsChannel()
    }

    fun scheduleNotification(notificationId: Int, title: String, time: Long){
        notificationsHandler.scheduleNotification(notificationId, title, time)
    }

    fun cancelNotification(notificationId: Int){
        notificationsHandler.cancelNotification(notificationId)
    }
}