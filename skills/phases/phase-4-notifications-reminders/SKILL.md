---
name: nursy-phase-4-notifications-reminders
description: Execute Nursy AI Phase 4 notifications and reminders work, including Android notification permission flow, medication reminders, daily check-in reminders, missed dose tracking, reminder scheduling, and adherence updates.
---

# Nursy Phase 4 Notifications Reminders

## Objective

Make the app proactive with local reminders for medication and daily check-ins.

## Start Here

- Read `component` Phase 4.
- Inspect medication entities and any dose event model.
- Inspect Android app dependencies in `apps/mobile/app/build.gradle.kts`.
- Use the component skills for Notifications and Medication Management.

## Work Sequence

1. Add notification permission handling for supported Android versions.
2. Create a reminder scheduler abstraction.
3. Schedule medication reminders from local medication schedules.
4. Schedule daily check-in reminders.
5. Add missed dose tracking and adherence score updates.
6. Cancel or update reminders when source data changes.

## Guardrails

- Keep reminders local and functional without cloud sync.
- Avoid notification text that sounds diagnostic.
- Do not schedule reminders for inactive medications.
- Treat permission denial as a UI state, not an app failure.

## Exit Criteria

- Medication and check-in reminders work locally.
- Medication adherence is calculated from dose events.
- Missed dose state appears in the dashboard.

## Verify

- Confirm schedule, update, and cancel paths.
- Confirm adherence changes after taken and missed dose events.
- Confirm dashboard displays missed dose state.
