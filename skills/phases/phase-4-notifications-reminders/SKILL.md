---
name: nursy-phase-4-notifications-reminders
description: Execute Nursy AI Phase 4 notifications and reminders work, including Android notification permission flow, 3 notification channels, AlarmManager-based medication reminders, daily check-in reminders, BroadcastReceiver declarations, and dose event tracking.
---

# Nursy Phase 4 — Notifications & Reminders

## Objective

Make the app proactive with local reminders for medication and daily check-ins. Uses Android `AlarmManager` for scheduling and `NotificationManager` for display. All behavior works offline.

## What Was Built

### Notification Channels (`NotificationHelper.kt`)

3 channels created on first launch:

| Channel | Importance | Purpose |
|---|---|---|
| `medication_reminders` | HIGH | "Time to take [name] ([dose])" |
| `checkin_reminders` | DEFAULT | "How are you feeling today?" |
| `general_reminders` | LOW | General health insights |

### Permissions

Manifest permissions:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

Runtime check via `NotificationHelper.hasPermission()` — gracefully handles denial.

### Reminder Scheduler (`ReminderScheduler.kt`)

- **Medication reminders**: `scheduleMedicationReminder()` creates daily repeating alarm at specified hour/minute. Uses `medicationId.hashCode() + hourOfDay` as unique notification ID. Cancel via `cancelMedicationReminders()` which iterates through all 24 hours.
- **Check-in reminder**: `scheduleCheckInReminder()` creates daily alarm at 10:00 AM. Cancel via `cancelCheckInReminder()`.
- **BroadcastReceivers**: `MedicationReminderReceiver` and `CheckInReminderReceiver` declared in AndroidManifest.xml.

### Dose Event Integration

The `NursyViewModel.scheduleDoseEventsForMedication()` creates `MedicationDoseEventEntity` records for each scheduled time. The `markDoseAsTaken()` method updates a dose event when the user confirms taking it.

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/notification/NotificationHelper.kt`
- `apps/mobile/app/src/main/java/com/nursyai/notification/ReminderScheduler.kt` (includes `MedicationReminderReceiver` and `CheckInReminderReceiver`)
- `apps/mobile/app/src/main/AndroidManifest.xml` (permissions + receiver declarations)
- `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `scheduleDoseEventsForMedication()`
- `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/MedicationDoseEventEntity.kt`

## Guardrails

- Keep reminders local and functional without cloud sync.
- Avoid notification text that sounds diagnostic ("reminder" not "alert").
- Do not schedule reminders for inactive medications (checked before scheduling).
- Treat permission denial as a UI state, not an app failure (local tracking continues).

## Exit Criteria

- Medication and check-in reminders work locally via AlarmManager.
- Medication adherence is calculated from dose events.
- Missed dose state appears in the mobile dashboard.
- Permissions are handled gracefully on all Android versions.

## Verify

- Confirm schedule, update, and cancel paths for medication reminders.
- Confirm adherence changes after taken and missed dose events.
- Confirm the mobile dashboard displays missed dose state.
- Confirm permission-denied state shows no crashes.
