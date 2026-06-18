---
name: nursy-notifications
description: Build or modify Nursy AI notifications, medication reminders, daily check-in reminders, notification permission flow, notification channels, or reminder scheduling with AlarmManager. Use when making the app proactive.
---

# Nursy Notifications

## Objective

Schedule local reminders for important health actions while respecting Android permission and lifecycle behavior. Uses `AlarmManager` for repeating alarms and `NotificationManager` for display.

## Architecture

```
ReminderScheduler.scheduleMedicationReminder()
  → AlarmManager.setRepeating(RTC_WAKEUP, ...)
    → MedicationReminderReceiver.onReceive()
      → NotificationHelper.showMedicationReminder()

ReminderScheduler.scheduleCheckInReminder()
  → AlarmManager.setRepeating(RTC_WAKEUP, ...)
    → CheckInReminderReceiver.onReceive()
      → NotificationHelper.showCheckInReminder()
```

## Notification Channels (`NotificationHelper.kt`)

3 channels created on app startup:

| Channel ID | Name | Importance | Description |
|---|---|---|---|
| `medication_reminders` | Medication Reminders | HIGH | Reminders for scheduled medication doses |
| `checkin_reminders` | Check-In Reminders | DEFAULT | Daily reminders to complete health check-in |
| `general_reminders` | Nursy AI Updates | LOW | General health insights and updates |

## Permissions

- **Android 13+ (Tiramisu)**: `POST_NOTIFICATIONS` permission must be requested at runtime.
- **Manifest**: `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />`
- **Alarm permissions** (Android 12+): `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `WAKE_LOCK`
- **Permission handling**: `NotificationHelper.hasPermission()` checks SDK version. If denied, local tracking continues without notifications — no app crash.

## Broadcast Receivers

Declared in `AndroidManifest.xml`:

```xml
<receiver android:name=".notification.MedicationReminderReceiver" android:exported="false" />
<receiver android:name=".notification.CheckInReminderReceiver" android:exported="false" />
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/notification/NotificationHelper.kt` — channels, permission check, notification display
- Inspect `apps/mobile/app/src/main/java/com/nursyai/notification/ReminderScheduler.kt` — `scheduleMedicationReminder()`, `cancelMedicationReminders()`, `scheduleCheckInReminder()`, `cancelCheckInReminder()`, `MedicationReminderReceiver`, `CheckInReminderReceiver`
- Inspect `apps/mobile/app/build.gradle.kts` for notification and WorkManager dependencies
- Inspect `apps/mobile/app/src/main/AndroidManifest.xml` for permission and receiver declarations
- Inspect medication and check-in data models before scheduling reminders

## Implementation Guidance

- **Local only**: Schedule reminders from local Room data, not cloud state.
- **Alarm IDs**: Medication reminder uses `medicationId.hashCode() + hourOfDay` as the notification ID and `PendingIntent` request code. This allows per-dose cancelation.
- **Daily repeating**: Both medication and check-in reminders use `AlarmManager.setRepeating(INTERVAL_DAY)`. If the scheduled time has passed today, they schedule for tomorrow.
- **Cancel on deactivate**: `cancelMedicationReminders()` iterates through 0-23 hours and cancels all pending intents for that medication.
- **Notification text**: Medication shows "Time to take [name] ([dose])". Check-in shows "How are you feeling today?"
- **No diagnosis language**: Avoid diagnostic-sounding text in notifications.

## Expected Deliverables

- `NotificationHelper.kt` — channel creation, permission check, `showMedicationReminder()`, `showCheckInReminder()`.
- `ReminderScheduler.kt` — schedule/cancel for medications and check-ins, with BroadcastReceiver classes.
- `MedicationReminderReceiver` and `CheckInReminderReceiver` (registered in manifest).
- Updated `AndroidManifest.xml` with notification and alarm permissions + receiver declarations.

## Verify

- Confirm reminders can be scheduled, updated, and canceled.
- Confirm disabled medications no longer notify (reminder cancelation on deactivate).
- Confirm permission-denied state does not break local tracking (graceful handling).
- Confirm notification channels are created (check in app settings).
