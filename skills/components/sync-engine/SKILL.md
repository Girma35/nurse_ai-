---
name: nursy-sync-engine
description: Build or modify the Nursy AI sync engine, WorkManager sync worker, Room-to-cloud upload, DynamoDB mapping, conflict metadata, retries, pending sync queues, or sync status reporting. Use when connecting offline data to cloud.
---

# Nursy Sync Engine

## Objective

Sync local Room records to the cloud when connectivity is available while
preserving the mobile database as the offline source of truth.

## Start Here

- Inspect `HealthSyncWorker.kt`.
- Inspect DAO unsynced queries and entity sync metadata.
- Read `infra/README.md` and `infra/dynamodb/nursy-ai-table.json`.
- Read `component` sections for Sync Engine and Offline Storage.

## Implementation Guidance

- Use WorkManager for background sync.
- Upload pending local changes and mark them synced only after confirmed success.
- Include conflict metadata such as `updatedAt`, `deviceId`, and `syncVersion`.
- Map records into the DynamoDB key pattern: `PK` as user id scope and `SK` as
  entity plus timestamp plus id.
- Retry transient failures and expose sync status to the dashboard.
- Keep conflict handling conservative: never discard newer local data silently.

## Expected Deliverables

- Sync queue or pending-sync implementation.
- Record mapper from Room entities to cloud payloads.
- WorkManager retry behavior.
- Sync status model.
- Backend API contract or client integration.

## Verify

- Test success, failure, retry, and no-network paths.
- Confirm synced records are not re-uploaded unnecessarily.
- Confirm failed sync attempts leave local records intact.
