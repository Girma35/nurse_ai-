---
name: nursy-notifications
description: Build or modify Nursy AI notifications, medication reminders, daily check-in reminders, hydration reminders, appointment alerts, notification permission flow, or reminder scheduling. Use when making the app proactive.
---

# Nursy Notifications

## Objective

Schedule local reminders for important health actions while respecting Android
permission and lifecycle behavior.

## Start Here

- Inspect `apps/mobile/app/build.gradle.kts` for notification, WorkManager, or
  alarm dependencies.
- Inspect medication and check-in data models before scheduling reminders.
- Read `component` sections for Notifications and Medication Management.

## Implementation Guidance

- Handle notification permission explicitly on supported Android versions.
- Schedule reminders from local data, not cloud state.
- Support medication reminders and daily check-in reminders first.
- Keep hydration and appointment alerts behind clear data models.
- Cancel or update reminders when source records are changed or disabled.
- Keep notification text concise and non-diagnostic.

## Expected Deliverables

- Permission request flow.
- Reminder scheduler abstraction.
- Medication reminder integration.
- Daily check-in reminder integration.
- Cancellation/update behavior.

## Verify

- Confirm reminders can be scheduled, updated, and canceled.
- Confirm disabled medications no longer notify.
- Confirm permission-denied state does not break local tracking.
