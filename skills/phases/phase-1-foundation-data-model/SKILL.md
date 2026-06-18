---
name: nursy-phase-1-foundation-data-model
description: Execute Nursy AI Phase 1 foundation and data model work, including Room entities, comprehensive DAO methods, sync metadata, NursyDatabase setup, and mobile sync payload mapping.
---

# Nursy Phase 1 — Foundation & Data Model

## Objective

Make the mobile data model solid before adding more screens. Prioritize local Room persistence, sync-ready metadata, and deterministic payload mapping.

## Room Entities

| Entity | Table | Key Fields | Sync Fields |
|---|---|---|---|
| `DailyCheckInEntity` | `daily_check_ins` | mood, energy, sleep, stress, water, notes, date | createdAt, updatedAt, syncState |
| `SymptomEntity` | `symptoms` | name, severity, startedAt, duration, notes, active | createdAt, updatedAt, syncState |
| `MedicationEntity` | `medications` | name, dose, frequency, schedule, counts, active | createdAt, updatedAt, syncState |
| `ProfileEntity` | `profiles` | name, DOB, gender, measurements, blood type, allergies, conditions | createdAt, updatedAt, syncState |
| `EmergencyContactEntity` | `emergency_contacts` | name, relationship, phone | createdAt, updatedAt, syncState |
| `MedicationDoseEventEntity` | `medication_dose_events` | medicationId, scheduledTime, takenAt, status | createdAt, updatedAt, syncState |

## Canonical Sync Fields

```kotlin
val id: String
val userId: String
val createdAt: Long
val updatedAt: Long
val syncState: String
```

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/*.kt`
- `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt`
- `apps/mobile/app/src/main/java/com/nursyai/data/local/NursyDatabase.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`

## Guardrails

- Keep Room DB as the offline source of truth.
- Do not block local record creation on cloud availability.
- Keep `pending*()` and `mark*Synced()` support for every syncable entity.
- Add proper migrations before production release.

## Exit Criteria

- Mobile can save and read all entity types locally.
- Unsynced local records can be queried.
- `RecordMapper` produces DynamoDB key patterns.
- Local data remains available offline.

## Verify

```bash
cd apps/mobile
./gradlew test
./gradlew assembleDebug
```
