package com.bigneardranch.to_do.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import android.Manifest
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.bigneardranch.to_do.R
import com.bigneardranch.to_do.data.database.TaskModel
import com.bigneardranch.to_do.ui.MainActivity

//Todo: Create Notification Group
const val CHANNEL_ID = "current_task_notification"

class CurrentTaskNotification(private val context: Context) {
    companion object {
        fun createNotificationChannel(context: Context) {
            val name = context.getString(R.string.currentTaskNotificationChannelName)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        fun notifyCurrentTask(context: Context, task: TaskModel) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(task.title)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Todo: Add Request permission

            } else {
                NotificationManagerCompat.from(context).notify(task.id, notification)
            }
        }

        fun cancelCurrentTask(context: Context, task: TaskModel) {
            NotificationManagerCompat.from(context).cancel(task.id)
        }
    }


}