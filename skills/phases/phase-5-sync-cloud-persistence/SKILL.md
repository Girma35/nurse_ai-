---
name: nursy-phase-5-sync-cloud-persistence
description: Execute Nursy AI Phase 5 sync and cloud persistence work, including pending-sync fields, WorkManager sync behavior, backend upsert API contract, Room-to-DynamoDB mapping, retries, conflict metadata, and cloud-readable records.
---

# Nursy Phase 5 Sync Cloud Persistence

## Objective

Sync offline mobile records to cloud storage when connectivity returns, without
weakening local-first behavior.

## Start Here

- Read `component` Phase 5.
- Inspect `HealthSyncWorker.kt`.
- Inspect Room entities and DAO unsynced queries.
- Inspect `infra/README.md` and `infra/dynamodb/nursy-ai-table.json`.
- Use the component skills for Sync Engine, Offline Storage, Cloud Backend, and
  Shared Models.

## Work Sequence

1. Implement sync queue or pending-sync fields for syncable entities.
2. Implement WorkManager sync behavior.
3. Define backend upsert API contract.
4. Map local records to DynamoDB single-table records.
5. Add retry behavior and conflict metadata.
6. Expose sync status for dashboard rendering.

## Guardrails

- Mark records synced only after confirmed cloud persistence.
- Never discard newer local data silently.
- Keep cloud failures non-destructive.
- Keep web dashboard reads separate from mobile local source-of-truth behavior.

## Exit Criteria

- Local records sync when connectivity returns.
- Sync failures are retried.
- Dashboard can read synced records from the cloud layer.

## Verify

- Test no-network, transient failure, success, and duplicate-upload paths.
- Confirm DynamoDB keys match the infrastructure pattern.
- Confirm synced records remain queryable by user scope.
