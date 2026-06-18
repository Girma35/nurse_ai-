---
name: nursy-sync-engine
description: Build or modify the Nursy AI sync engine, WorkManager sync worker, Room-to-cloud upload, DynamoDB mapping, conflict metadata, retries, pending sync queues, or sync status reporting. Use when connecting offline data to cloud.
---

# Nursy Sync Engine

## Objective

Sync local Room records to the cloud when connectivity is available while preserving the mobile database as the offline source of truth. Uses WorkManager for background sync with exponential backoff retries.

## Architecture

```
HealthSyncWorker.doWork()
├── Collect pending records from Room (6 entity types)
│   ├── pendingCheckIns()
│   ├── pendingSymptoms()
│   ├── pendingMedications()
│   ├── pendingProfiles()
│   ├── pendingEmergencyContacts()
│   └── pendingDoseEvents()
├── Map to SyncPayload via RecordMapper
│   ├── RecordMapper.checkInToPayload()
│   ├── RecordMapper.symptomToPayload()
│   ├── RecordMapper.medicationToPayload()
│   ├── RecordMapper.profileToPayload()
│   ├── RecordMapper.emergencyContactToPayload()
│   └── RecordMapper.doseEventToPayload()
├── SyncApiClient.batchUpsert(payloads)
│   └── POST /sync/batch (OkHttp + Dispatchers.IO)
└── On success: markRecordsSynced()
    └── DAO: mark*Synced(ids) → syncState = 'synced'
```

## Key Files

| File | Path | Purpose |
|---|---|---|
| `HealthSyncWorker.kt` | `apps/mobile/app/src/main/java/com/nursyai/sync/` | WorkManager worker, retry logic, sync orchestration |
| `SyncApiClient.kt` | `apps/mobile/app/src/main/java/com/nursyai/sync/` | OkHttp HTTP client with `Dispatchers.IO`, serialization, batch upsert |
| `RecordMapper.kt` | `apps/mobile/app/src/main/java/com/nursyai/sync/` | Maps Room entities → `SyncPayload` for DynamoDB |
| `HealthDao.kt` | `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/` | Pending and sync-marking queries |

## DynamoDB Key Pattern

```
PK = "USER#<userId>"
SK = "<entityType>#<timestamp>#<entityId>"
GSI1PK = "<entityType>"
GSI1SK = "<updatedAt>"
```

## SyncPayload Model

```kotlin
data class SyncPayload(
    val PK: String,             // "USER#<userId>"
    val SK: String,             // "symptom#1718000000000#uuid"
    val entityType: String,     // "checkIn" | "symptom" | "medication" | "profile" | "emergencyContact" | "doseEvent"
    val userId: String,
    val entityId: String,
    val payload: String,        // JSON-serialized entity data
    val updatedAt: Long,
    val deviceId: String = "mobile-android",
    val syncVersion: Int = 1
)
```

## Retry Behavior

- **Max retries**: 3 attempts
- **Backoff**: Exponential (`BackoffPolicy.EXPONENTIAL`, 30 seconds initial)
- **Failure handling**: Transient failures retry up to 3 times. Permanent failures return `Result.failure()`. Local data is never discarded.
- **No-network**: WorkManager handles network constraint internally via `NetworkType.CONNECTED`.

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/HealthSyncWorker.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/SyncApiClient.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`
- Inspect DAO unsynced queries and entity sync metadata in `HealthDao.kt`
- Read `infra/README.md` and `infra/dynamodb/nursy-ai-table.json`
- Read component skills for Offline Storage and Cloud Backend

## Implementation Guidance

- **WorkManager**: Uses `OneTimeWorkRequestBuilder` with `ExistingWorkPolicy.REPLACE` via `requestSync()` companion method.
- **Record mapping**: Each entity type has a dedicated mapper method that creates a typed payload (`CheckInSyncPayload`, `SymptomSyncPayload`, etc.) and wraps it in the `SyncPayload` envelope.
- **Confidence boundary**: Records are marked `syncState = 'synced'` **only after** the API confirms success with `response.success == true`.
- **Conflict metadata**: Each payload includes `updatedAt`, `deviceId`, and `syncVersion` for conflict resolution on the server side.
- **IO threading**: All OkHttp calls are wrapped in `withContext(Dispatchers.IO)` to avoid blocking the WorkManager dispatcher.
- **Batch**: Uses `batchUpsert()` to upload all pending records in a single HTTP request.

## Expected Deliverables

- `HealthSyncWorker` with full sync orchestration, retries, and sync marking.
- `SyncApiClient` with `upsertRecord()`, `batchUpsert()`, `fetchRecords()`, and `SyncPayload.createSyncPayload()`.
- `RecordMapper` with 6 mapper methods (one per entity type).
- `requestSync()` companion method for external triggering.

## Verify

- Test success, failure, retry, and no-network paths.
- Confirm synced records are not re-uploaded (sync state marked after success).
- Confirm failed sync attempts leave local records intact.
- Confirm DynamoDB keys match the infrastructure pattern (`USER#<userId>` / `<entity>#<timestamp>#<id>`).
