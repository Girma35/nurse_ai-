---
name: nursy-medication-management
description: Build or modify Nursy AI medication management, dose, frequency, schedules, reminders, missed doses, dose events, medication status, or adherence scoring. Use when working on medication tracking.
---

# Nursy Medication Management

## Objective

Help users track medications, scheduled doses, missed doses, and adherence while
keeping all essential behavior available offline.

## Start Here

- Inspect `MedicationEntity.kt` and `HealthDao.kt`.
- Read `component` sections for Medication Management, Notifications, and Local
  AI Health Insights.
- Check Android notification and WorkManager dependencies before adding reminder
  behavior.

## Implementation Guidance

- Model medication name, dose, frequency, schedule, active state, and sync
  metadata.
- Track dose events separately from the medication definition when calculating
  adherence.
- Distinguish skipped, missed, and taken states if the UI needs them.
- Keep adherence scoring deterministic and testable.
- Integrate reminder scheduling through the notifications component rather than
  embedding alarm logic in the data model.

## Expected Deliverables

- Medication list and edit screens.
- Dose event model and DAO methods.
- Medication status summary for dashboard.
- Adherence score helper.
- Reminder scheduling integration point.

## Verify

- Save medication and dose events offline.
- Confirm adherence score changes after taken and missed events.
- Confirm inactive medications no longer schedule future reminders.
