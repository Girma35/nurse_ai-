package com.nursyai.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nursyai.R

object NotificationHelper {
    private const val CHANNEL_ID_MEDICATIONS = "medication_reminders"
    private const val CHANNEL_ID_CHECKINS = "checkin_reminders"
    private const val CHANNEL_ID_GENERAL = "general_reminders"

    fun createChannels(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        listOf(
            NotificationChannel(
                CHANNEL_ID_MEDICATIONS,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled medication doses"
            },
            NotificationChannel(
                CHANNEL_ID_CHECKINS,
                "Check-In Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to complete your health check-in"
            },
            NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Nursy AI Updates",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General health insights and updates"
            }
        ).forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMedicationReminder(
        context: Context,
        medicationName: String,
        dose: String,
        notificationId: Int
    ) {
        if (!hasPermission(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MEDICATIONS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take $medicationName ($dose)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showCheckInReminder(context: Context) {
        if (!hasPermission(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CHECKINS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Check-In")
            .setContentText("How are you feeling today? Tap to log your check-in.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}
