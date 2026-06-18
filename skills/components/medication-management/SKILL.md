---
name: nursy-medication-management
description: Build or modify Nursy AI mobile medication management, medication records, dose events, adherence scoring, taken/missed tracking, reminder scheduling, local dashboard state, or sync mapping.
---

# Nursy Medication Management

## Objective

Help users track medications, scheduled doses, taken/missed events, adherence, and reminders while remaining fully usable offline.

## Data Captured

- medication name
- dose
- frequency
- scheduled times
- active state
- taken count
- missed count
- dose events with scheduled time, taken time, and status

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/MedicationEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/MedicationDoseEventEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/MedicationManagementScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/notification/ReminderScheduler.kt`
- Inspect `HealthDao.kt` medication and dose queries

## Implementation Guidance

- Save medication and dose state locally first.
- Use dose events for history rather than only mutating aggregate counts.
- Calculate adherence deterministically from local records.
- Reminder scheduling should tolerate app restarts and permission differences.
- Sync mapping should include enough metadata to recover dose history on another device.

## Expected Deliverables

- Medication CRUD.
- Dose event tracking.
- Taken/missed controls.
- Local adherence score.
- Reminder integration.
- Sync payload mapping through `RecordMapper`.

## Verify

- Confirm medication and dose events save offline.
- Confirm adherence updates after taken/missed events.
- Confirm reminders are scheduled/cancelled correctly.
- Confirm pending records are not marked synced before backend success.
