---
name: nursy-health-dashboard
description: Build or modify the Nursy AI mobile health dashboard, health score ring, today's summary, active symptoms, medication status, local insights, or sync status display. Use when working on the main Android summary view.
---

# Nursy Health Dashboard

## Objective

Show a compact, useful summary of the user's current health state from local Room data first. Synced or cloud-generated report records can appear when available, but the dashboard must remain useful offline.

## Mobile Dashboard (`DashboardScreen.kt`)

```text
LazyColumn
├─ Header: Nursy AI + Today
├─ HealthScoreCard: score ring + label + insight
├─ QuickMetricRow: Sleep, Water, Energy
├─ Local insights list
├─ Active symptoms list
├─ Active medications list
├─ Sync status
└─ EmptyDashboardCard when no data exists
```

## Health Score Calculation

Keep the score deterministic and local:

```kotlin
val sleepScore = min(sleepHours / 8.0, 1.0) * 20
val energyScore = (energyLevel / 10.0) * 20
val stressScore = max(0, (5 - stressLevel) / 5.0) * 15
val hydrationScore = min(waterIntakeMl / 2000.0, 1.0) * 15
val symptomPenalty = symptoms.sumOf { it.severity * 3 }
val totalScore = clamp(sleepScore + energyScore + stressScore + hydrationScore - symptomPenalty, 0, 100)
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DashboardScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt`

## Implementation Guidance

- Compute dashboard state from Room through `NursyViewModel`.
- Collect `StateFlow` values with Compose lifecycle-aware patterns when available.
- Keep first-run empty state useful and direct.
- Show local `HealthInsight` values from `LocalRulesEngine`.
- Surface sync state without blocking local interaction.
- Cloud reports are optional cached records, never required for the score or core summary.

## Expected Deliverables

- `DashboardScreen.kt` with score ring, metrics, insights, symptoms, medications, sync status, and empty state.
- Deterministic local score behavior.
- Offline rendering with no backend dependency.

## Verify

- Confirm dashboard renders with no records, partial records, and populated local data.
- Confirm updates appear immediately after local saves.
- Confirm score and summaries are deterministic for the same local data.
- Run `cd apps/mobile && ./gradlew test` when changing rules or scoring behavior.
