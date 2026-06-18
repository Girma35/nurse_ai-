---
name: nursy-offline-storage
description: Build or modify Nursy AI offline storage, Room database schema, 6 entities, DAOs, migrations, pending sync fields, local source-of-truth behavior, or unsynced record queries. Use when working on local persistence.
---

# Nursy Offline Storage

## Objective

Make Room DB the reliable local source of truth for all core mobile health data. The database has 6 entities, a comprehensive DAO, and a singleton connection pattern.

## Database Schema (`NursyDatabase.kt`)

```
NursyDatabase (version 2)
├── daily_check_ins
├── symptoms
├── medications
├── profiles
├── emergency_contacts
└── medication_dose_events
```

All entities include these sync metadata fields:
- `id: String` — stable UUID
- `userId: String` — user scope for queries
- `createdAt: Long` — epoch millis when record was created
- `updatedAt: Long` — epoch millis when record was last modified
- `syncState: String` — `"queued"` (pending sync) | `"synced"` (confirmed cloud)

### Singleton Pattern

```kotlin
companion object {
    @Volatile
    private var INSTANCE: NursyDatabase? = null

    fun getInstance(context: Context): NursyDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context, NursyDatabase::class.java, "nursy_ai_database")
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
```

## DAO Methods (`HealthDao.kt`)

### Entity-Specific CRUD

| Entity | Observe (Flow) | Get (suspend) | Upsert | Other |
|---|---|---|---|---|
| Check-Ins | `observeCheckIns`, `observeLatestCheckIn` | `getCheckInByDate`, `getAllCheckIns` | `upsertCheckIn` | `checkInCount` |
| Symptoms | `observeSymptoms`, `observeActiveSymptoms` | `getAllSymptoms`, `getActiveSymptoms`, `getSymptomById` | `upsertSymptom` | `resolveSymptom` |
| Medications | `observeActiveMedications`, `observeAllMedications` | `getAllMedications`, `getMedicationById` | `upsertMedication` | `deactivateMedication` |
| Dose Events | `observeDoseEvents`, `observeDoseEventsForMedication` | `getDoseEventsForMedication`, `getTakenDoseEvents`, `getMissedDoseEvents` | `upsertDoseEvent` | `markDoseAsTaken`, `markDoseAsMissed` |
| Profile | `observeProfile` | `getProfile` | `upsertProfile` | — |
| Emergency Contacts | `observeEmergencyContacts` | `getEmergencyContacts` | `upsertEmergencyContact` | `deleteEmergencyContact` |

### Sync Methods

Each entity has:
- `pending*()` — `SELECT * WHERE syncState != 'synced'`
- `mark*Synced(ids: List<String>)` — `UPDATE SET syncState = 'synced' WHERE id IN (:ids)`
- `pending*Count()` — `SELECT COUNT(*) WHERE syncState = 'queued'`

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/NursyDatabase.kt` — database class with companion singleton
- Inspect all entity files in `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — all DAO methods
- Read phase 1 skill and offline storage component sections
- Check sync requirements before changing timestamps or pending sync fields

## Implementation Guidance

- **Source of truth**: All user-entered health data is stored locally first. Network availability never affects local save success.
- **IDs**: Stable UUIDs generated client-side. Required for sync idempotency.
- **Migrations**: `fallbackToDestructiveMigration()` is used during development. Production should use proper `Migration` objects.
- **Sync marking**: Records are marked `synced` only after confirmed cloud persistence via `HealthSyncWorker`.
- **Flow-based**: Most DAO queries return `Flow<List<T>>` for reactive UI binding. Suspending variants (`suspend fun`) are provided for one-shot reads.

## Expected Deliverables

- 6 Room entities with complete field definitions.
- `HealthDao` with observe, get, upsert, pending, sync-marking methods.
- `NursyDatabase` with singleton pattern and version management.
- Unsynced record query support for all syncable entities.

## Verify

- Compile the Android app after schema changes (`./gradlew assembleDebug`).
- Confirm records save and reload offline with pending sync metadata intact.
- Confirm `pending*()` queries return only unsynced records.
- Confirm `mark*Synced()` updates sync state correctly.
