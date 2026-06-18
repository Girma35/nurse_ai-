---
name: nursy-daily-health-check-in
description: Build or modify Nursy AI mobile daily health check-in flows, mood, energy, sleep, stress, water intake, notes, date history, dashboard summaries, or local health score calculation. Use when working on daily health state capture.
---

# Nursy Daily Health Check-In

## Objective

Capture the user's daily health state locally and make it immediately useful in the mobile dashboard, timeline, local rules, and later sync/reporting workflows.

## Data Captured

- mood
- energy level from 1 to 10
- sleep hours
- sleep quality
- stress level
- water intake in ml
- notes
- local date

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/DailyCheckInEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DailyCheckInScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- Inspect `HealthDao.kt` check-in queries
- Inspect `DashboardScreen.kt` for score inputs

## Implementation Guidance

- Save to Room first. Sync is a follow-up side effect.
- Use local date consistently for one-check-in-per-day behavior.
- Keep notes optional and local-first.
- Feed `LocalRulesEngine` with recent check-ins for missed check-in, sleep, hydration, and stress rules.
- Avoid cloud calls from the check-in form.

## Expected Deliverables

- Check-in form that writes `DailyCheckInEntity`.
- DAO methods for latest check-in, date history, and pending sync.
- Dashboard and timeline updates from local state.

## Verify

- Confirm save works offline.
- Confirm dashboard updates immediately after save.
- Confirm timeline shows the check-in.
- Confirm the record remains queued for sync until backend upload succeeds.
