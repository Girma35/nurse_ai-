package com.nursyai.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nursyai.data.local.entity.MedicationEntity
import java.util.Calendar

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medication_name") ?: return
        val dose = intent.getStringExtra("medication_dose") ?: return
        val notificationId = intent.getIntExtra("notification_id", 0)

        NotificationHelper.showMedicationReminder(context, medicationName, dose, notificationId)
    }
}

class CheckInReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showCheckInReminder(context)
    }
}

object ReminderScheduler {
    private const val CHECK_IN_REQUEST_CODE = 2001

    fun scheduleMedicationReminder(
        context: Context,
        medication: MedicationEntity,
        hourOfDay: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            putExtra("medication_name", medication.name)
            putExtra("medication_dose", medication.dose)
            putExtra("notification_id", medication.id.hashCode() + hourOfDay)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medication.id.hashCode() + hourOfDay,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelMedicationReminders(context: Context, medicationId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Cancel all pending reminders for this medication (up to 24 hours worth)
        for (hour in 0..23) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                medicationId.hashCode() + hour,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    fun scheduleCheckInReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CheckInReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            CHECK_IN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for 10:00 AM daily
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelCheckInReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CheckInReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            CHECK_IN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
