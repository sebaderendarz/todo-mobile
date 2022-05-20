package com.example.todo.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todo.R
import com.example.todo.activities.UpdateTaskActivity


const val channelId = "dueDateChannel"
const val messageExtra = "messageExtra"
const val notificationIdExtra = "notificationIdExtra"
const val titleExtra = "titleExtra"


class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val resultIntent = Intent(context, UpdateTaskActivity::class.java)
        resultIntent.putExtra("taskId", intent.getIntExtra(notificationIdExtra, 1))
        val resultPendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .build()
        val notificationId = intent.getIntExtra(notificationIdExtra, 0)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

}