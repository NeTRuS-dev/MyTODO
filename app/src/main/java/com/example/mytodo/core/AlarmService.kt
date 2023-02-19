package com.example.mytodo.core

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.mytodo.R
import com.example.mytodo.core.extensions.nextPositiveInt
import com.example.mytodo.core.models.Note
import com.example.mytodo.presentation.ARG_ID
import com.example.mytodo.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject


class AlarmService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        createNotificationChannel(context)
    }

    fun showNotification(
        notificationId: Int,
        title: String?,
        message: String?,
        pendingIntent: PendingIntent?
    ) {
        val channelId = context.getString(R.string.channel_id)

        var builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (pendingIntent != null) {
            builder = builder.setContentIntent(pendingIntent)
        }

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show()
                return
            }
            notify(notificationId, builder.build())
        }
    }

    fun createPendingIntentToFragment(fragmentId: Int, bundle: Bundle?): PendingIntent {
        return NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_navigation)
            .setDestination(fragmentId)
            .setArguments(bundle)
            .createPendingIntent()
    }

    fun createAlarm(note: Note, executeAt: Long, requestCode: Int? = null): Int {
        var alarmCode = requestCode
        if (alarmCode == null) {
            alarmCode = Random().nextPositiveInt()
        }
        val pendingIntent = buildAlarmIntent(alarmCode, note.id)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            executeAt,
            pendingIntent
        )

        return alarmCode

    }

    fun cancelAlarm(note: Note, alarmCode: Int) {
        val pendingIntent = buildAlarmIntent(alarmCode, note.id)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(pendingIntent)
    }

    private fun buildAlarmIntent(alarmCode: Int, noteId: Int): PendingIntent {
        val intent = Intent(context, NotificationsReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(ARG_ID, noteId)
        return PendingIntent.getBroadcast(
            context,
            alarmCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.channel_name)
            val channelId = context.getString(R.string.channel_id)
            val channelDescription = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}