---
name: nursy-daily-health-check-in
description: Build or modify Nursy AI daily health check-in flows, mood, energy, sleep, stress, water intake, notes, date history, or dashboard summaries. Use when working on daily health state capture.
---

# Nursy Daily Health Check-In

## Objective

Capture the user's daily health state locally and make it immediately useful in
the mobile dashboard and later sync/reporting workflows.

## Start Here

- Inspect `DailyCheckInEntity.kt` and `HealthDao.kt`.
- Inspect `packages/shared/src/index.ts` for health score helpers before adding
  duplicate scoring logic.
- Read `component` Phase 1 and Phase 2 before changing schema or UI flows.

## Implementation Guidance

- Store mood, energy from 1 to 10, sleep hours, sleep quality, stress, water
  intake, notes, and check-in date.
- Use local persistence as the source of truth.
- Keep one clear check-in per date unless the product flow explicitly supports
  multiple entries.
- Normalize values at the form boundary so scoring and insights receive stable
  input.
- Mark records as pending sync when created or edited.
- Surface today's check-in in the dashboard without requiring cloud data.

## Expected Deliverables

- Check-in form screen.
- DAO create, update, latest, by-date, history, and unsynced queries.
- Dashboard summary mapping.
- Health score input mapping.

## Verify

- Save a check-in offline and reload it from Room.
- Confirm out-of-range mood, energy, sleep, stress, and water values are handled.
- Confirm dashboard updates from local state.
