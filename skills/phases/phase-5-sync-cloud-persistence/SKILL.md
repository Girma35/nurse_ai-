---
name: nursy-phase-5-sync-cloud-persistence
description: Execute Nursy AI Phase 5 sync and cloud persistence work, including pending-sync fields on all 6 entities, WorkManager HealthSyncWorker with 3 retries and exponential backoff, SyncApiClient with OkHttp and Dispatchers.IO, RecordMapper to DynamoDB single-table key pattern, and sync-state marking after confirmed upload.
---

# Nursy Phase 5 — Sync & Cloud Persistence

## Objective

Sync offline mobile records to cloud storage when connectivity returns, without weakening local-first behavior. Uses WorkManager for background sync with retries and DynamoDB single-table design for cloud storage.

## What Was Built

### Pending-Sync Infrastructure

All 6 Room entities have:
- `syncState: String = "queued"` (default) / `"synced"`
- `pending*()` DAO methods: `SELECT * WHERE syncState != 'synced'`
- `mark*Synced(ids)` DAO methods: `UPDATE SET syncState = 'synced' WHERE id IN (:ids)`

### HealthSyncWorker

- Extends `CoroutineWorker` from WorkManager
- Collects pending records from all 6 entity types
- Maps to `SyncPayload` via `RecordMapper`
- Uploads via `SyncApiClient.batchUpsert()`
- On success: calls `markRecordsSynced()` to update Room
- **Retries**: 3 attempts with exponential backoff (30s initial)
- **Request method**: `requestSync()` companion — enqueues unique work with `ExistingWorkPolicy.REPLACE`

### SyncApiClient

- OkHttp-based HTTP client with `withContext(Dispatchers.IO)` for non-blocking IO
- `upsertRecord()` — single record POST to `/v1/sync/upsert`
- `batchUpsert()` — batch POST to `/v1/sync/batch`
- `fetchRecords()` — GET from `/v1/sync/records/{userId}`
- 30-second connect/read/write timeouts

### RecordMapper

6 mapper methods converting Room entities to `SyncPayload` with DynamoDB key pattern:

```
PK = "USER#<userId>"
SK = "<entityType>#<timestamp>#<entityId>"
```

Each creates a typed inner payload (e.g., `CheckInSyncPayload`) serialized as JSON in the `payload` field. Envelope includes `deviceId = "mobile-android"` and `syncVersion = 1` for conflict resolution.

### DynamoDB Table (`infra/dynamodb/nursy-ai-table.json`)

CloudFormation template with:
- Table: `nursy-ai-health-records`, PAY_PER_REQUEST billing
- PK (HASH): `USER#<userId>`, SK (RANGE): `<entity>#<timestamp>#<id>`
- GSI1: entity type + updatedAt for scoped queries
- Point-in-time recovery enabled

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/sync/HealthSyncWorker.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/SyncApiClient.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`
- `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — pending + markSynced queries
- `infra/dynamodb/nursy-ai-table.json`
- `infra/README.md`

## Guardrails

- Mark records synced only after confirmed cloud persistence.
- Never discard newer local data silently — conflict metadata enables server-side resolution.
- Keep cloud failures non-destructive — local records are never deleted.
- Keep web dashboard reads separate from mobile local source-of-truth behavior.

## Exit Criteria

- Local records sync when connectivity returns.
- Sync failures are retried up to 3 times.
- Dashboard can read synced records from the cloud layer.
- Unsynced records are not re-uploaded after successful sync.

## Verify

- Test no-network, transient failure, success, and duplicate-upload paths.
- Confirm DynamoDB keys match `USER#<userId>` / `<entity>#<timestamp>#<id>` pattern.
- Confirm synced records remain queryable by user scope.
- Confirm `pending*()` returns empty list after `mark*Synced()`.
