package com.example.todo.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todo.utils.SettingsHandler


class NotificationsHandler(private var context: Context, private val settings: SettingsHandler) {
    private val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerDefaultNotificationsChannel() {
        val name = "Due Date Channel"
        val description = "Notify That the Task's Deadline is Looming"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleNotification(notificationId: Int, title: String, time: Long) {
        val intent = Intent(context, Notification::class.java)
        val minutesBefore = settings.getNotificationTime()
        val message = "Deadline is looming! $minutesBefore minutes remaining"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(notificationIdExtra, notificationId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val notificationTime = (time - minutesBefore * 60) * 1000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationTime,
            pendingIntent
        )
    }

    fun cancelNotification(notificationId: Int) {
        val intent = Intent(context, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}