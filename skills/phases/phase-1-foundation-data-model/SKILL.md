---
name: nursy-phase-1-foundation-data-model
description: Execute Nursy AI Phase 1 foundation and data model work, including 6 Room entities, comprehensive DAO with 40+ methods, 6 sync payload mappers, NursyDatabase singleton with version 2, and 17 shared TypeScript types.
---

# Nursy Phase 1 — Foundation & Data Model

## Objective

Make the product data model solid before adding many screens. Prioritize local persistence, shared contracts, and sync-ready metadata. All 6 entities support offline creation, pending-sync querying, and cloud upload via RecordMapper.

## What Was Built

### Room Entities (6)

| Entity | Table | Key Fields | Sync Fields |
|---|---|---|---|
| `DailyCheckInEntity` | `daily_check_ins` | mood, energyLevel, sleepHours, stressLevel, waterIntakeMl, notes, date | createdAt, updatedAt, syncState |
| `SymptomEntity` | `symptoms` | name, severity(1-5), startedAt, durationHours, notes, active | createdAt, updatedAt, syncState |
| `MedicationEntity` | `medications` | name, dose, frequency, scheduledTimesCsv, takenCount, missedCount, active | createdAt, updatedAt, syncState |
| `ProfileEntity` | `profiles` | fullName, dateOfBirth, gender, weightKg, heightCm, bloodType, allergies, chronicConditions | createdAt, updatedAt, syncState |
| `EmergencyContactEntity` | `emergency_contacts` | name, relationship, phoneNumber | createdAt, updatedAt, syncState |
| `MedicationDoseEventEntity` | `medication_dose_events` | medicationId, scheduledTime, takenAt, status | createdAt, updatedAt, syncState |

### DAO (`HealthDao.kt`) — 40+ methods

- **Per-entity**: observe (Flow), get (suspend), upsert, pending, markSynced, pendingCount
- **Cross-entity**: aggregated pending counts for sync status

### Database (`NursyDatabase.kt`)

- Version 2 with `fallbackToDestructiveMigration()`
- Singleton `getInstance(context)` pattern with `@Volatile` + `synchronized`

### Canonical Sync Fields

Every entity includes:
```kotlin
val id: String,           // stable UUID
val userId: String,       // user scope
val createdAt: Long,      // epoch millis
val updatedAt: Long,      // epoch millis
val syncState: String,    // "queued" | "synced"
```

### Shared TypeScript Models (`packages/shared/src/index.ts`)

17 types (DailyCheckIn, SymptomLog, Medication, MedicationDoseEvent, UserProfile, EmergencyContact, EmergencyHealthCard, HealthInsight, WeeklyReport, SyncRecord, SyncSummary, TimelineEvent, MoodLevel, SleepQuality, DoseStatus, SyncState, EntityType) and 6 helper functions.

### RecordMapper — 6 entity-to-payload mappers

Each creates typed inner payloads (`CheckInSyncPayload`, `SymptomSyncPayload`, etc.) wrapped in the DynamoDB `SyncPayload` envelope.

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/*.kt` (6 entities)
- `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt`
- `apps/mobile/app/src/main/java/com/nursyai/data/local/NursyDatabase.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`
- `packages/shared/src/index.ts`

## Guardrails

- Keep Room DB as the offline source of truth.
- Do not block local record creation on cloud availability.
- Avoid UI-heavy changes until the data model supports the core flows.
- Add proper `Migration` objects before production release (currently using `fallbackToDestructiveMigration`).

## Exit Criteria

- Mobile can save and read all 6 entity types locally.
- Shared TypeScript models describe the same records used by mobile and web.
- Unsynced local records can be queried via `pending*()` DAO methods.
- RecordMapper produces valid DynamoDB key patterns.

## Verify

- Run `./gradlew assembleDebug` to compile the mobile data layer.
- Run `npm run typecheck --workspace @nursy/shared` to validate shared models.
- Run `npm run typecheck:web` to confirm web imports resolve correctly.
- Confirm every syncable entity has `pending*()` and `mark*Synced()` query support.
