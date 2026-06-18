---
name: nursy-symptom-tracking
description: Build or modify Nursy AI mobile symptom tracking, symptom CRUD, severity, duration, notes, active/resolved state, symptom history, local rule inputs, or sync mapping.
---

# Nursy Symptom Tracking

## Objective

Track symptoms locally over time so the user can see patterns offline and the local rules engine can generate useful insights.

## Data Captured

- symptom name
- severity from 1 to 5
- start time
- duration hours when known
- notes
- active/resolved state

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/SymptomEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomTrackingScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomJournalScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- Inspect `HealthDao.kt` symptom queries

## Implementation Guidance

- Save symptoms to Room first.
- `active = true` means the symptom appears in dashboard and local rules.
- Resolving a symptom should preserve history.
- Normalize names only for comparisons; preserve user-facing labels where possible.
- Local rules should detect repeated symptoms and high severity without cloud access.

## Expected Deliverables

- Symptom entry and history UI.
- Active/resolved DAO behavior.
- Parser integration from AI symptom journal.
- Sync payload mapping through `RecordMapper`.

## Verify

- Confirm symptoms save offline.
- Confirm resolved symptoms leave active lists but stay in history.
- Confirm repeated/high-severity symptoms produce local insights.
- Confirm pending sync state remains until upload succeeds.
