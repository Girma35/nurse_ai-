---
name: nursy-daily-health-check-in
description: Build or modify Nursy AI daily health check-in flows, mood, energy, sleep, stress, water intake, notes, date history, dashboard summaries, or health score calculation. Use when working on daily health state capture.
---

# Nursy Daily Health Check-In

## Objective

Capture the user's daily health state locally and make it immediately useful in the mobile dashboard and later sync/reporting workflows. The check-in form collects mood, energy, sleep, stress, water intake, and notes.

## Data Model

### DailyCheckInEntity (Room entity, table `daily_check_ins`)

```kotlin
@Entity(tableName = "daily_check_ins")
data class DailyCheckInEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val date: String,           // "yyyy-MM-dd"
    val mood: String,           // "low" | "down" | "steady" | "good" | "great"
    val energyLevel: Int,       // 1-10
    val sleepHours: Double,
    val sleepQuality: String,   // "poor" | "fair" | "good" | "excellent"
    val stressLevel: Int,       // 1-10
    val waterIntakeMl: Int,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### Shared TypeScript type

```typescript
export type DailyCheckIn = { id, userId, date, mood: MoodLevel, energyLevel, sleepHours, sleepQuality: SleepQuality, stressLevel, waterIntakeMl, notes? };
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/DailyCheckInEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — `observeCheckIns()`, `observeLatestCheckIn()`, `getCheckInByDate()`, `getAllCheckIns()`, `upsertCheckIn()`, `pendingCheckIns()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DailyCheckInScreen.kt` — form with mood dropdown, energy slider, sleep input, sleep quality dropdown, stress slider, water intake, notes
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `saveCheckIn()` method that upserts to Room
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DashboardScreen.kt` — `HealthScoreCard` that computes score from check-in data
- Inspect `packages/shared/src/index.ts` — `calculateHealthScore()` and `calculateHealthScoreFromFields()` helpers
- Read `component` Phase 1 and Phase 2 before changing schema or UI flows

## Implementation Guidance

- **One per date**: The ViewModel's `saveCheckIn()` queries `getCheckInByDate()`. If an existing check-in exists for today, it reuses the same ID (upsert). Otherwise, a new UUID is generated.
- **Form pre-fill**: The screen pre-fills from today's existing check-in using a `latestCheckIn` comparison by `date == today`.
- **Value normalization**: Mood is selected from `["low", "down", "steady", "good", "great"]`. Sleep quality from `["poor", "fair", "good", "excellent"]`. Energy and stress are 1-10 sliders.
- **Health score**: The `HealthScoreCard` in `DashboardScreen.kt` calculates a local score using sleep, energy, stress, hydration, and symptom penalty. The `packages/shared` package also exports `calculateHealthScore()` and `calculateHealthScoreFromFields()` for use in the web dashboard.
- **Sync**: All check-ins are marked `syncState = "queued"` on creation. The `HealthSyncWorker` uploads `pendingCheckIns()` and then `markCheckInsSynced()`.

## Expected Deliverables

- `DailyCheckInEntity` with all fields.
- DAO: `observeCheckIns`, `observeLatestCheckIn`, `getCheckInByDate`, `getAllCheckIns`, `upsertCheckIn`, `pendingCheckIns`, `markCheckInsSynced`, `pendingCheckInCount`.
- `DailyCheckInScreen.kt` — Compose form with mood dropdown, sliders, text inputs.
- `NursyViewModel.saveCheckIn()` — creates/updates check-in in Room.
- Shared `DailyCheckIn` type and `calculateHealthScore*()` helpers.

## Verify

- Save a check-in offline and reload it from Room (restart app or re-navigate).
- Confirm out-of-range values are handled: sleep hours ≤ 0, stress over 10, negative water intake.
- Confirm dashboard updates from local state immediately after save.
- Confirm today's existing check-in pre-fills the form correctly.
