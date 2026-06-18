---
name: nursy-cloud-backend
description: Build or modify the Nursy AI AWS backend for the mobile-only product, including Cognito auth boundaries, API Gateway/Lambda sync endpoints, DynamoDB persistence, user-scoped recovery reads, and optional cloud AI report entry points.
---

# Nursy Cloud Backend

## Objective

Provide a small cloud layer for the Android app: auth, backup, sync, recovery, and optional premium reports. The backend must not be required for daily offline use.

## Infrastructure

### DynamoDB Table

`infra/dynamodb/nursy-ai-table.json` creates `nursy-ai-health-records` with `PAY_PER_REQUEST` billing and point-in-time recovery.

Key schema:

- `PK`: `USER#<userId>`
- `SK`: `<entityType>#<timestamp>#<entityId>`

GSI:

- `GSI1PK`: entity type
- `GSI1SK`: `updatedAt`

## API Contract

The mobile `SyncApiClient` expects:

- `POST /v1/sync/upsert`
- `POST /v1/sync/batch`
- `GET /v1/sync/records/{userId}`

Optional future endpoints:

- `GET /v1/sync/summary/{userId}`
- `GET /v1/reports/{userId}`
- `POST /v1/reports/{userId}/generate`

## Start Here

- Read `infra/README.md`
- Inspect `infra/dynamodb/nursy-ai-table.json`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/SyncApiClient.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/HealthSyncWorker.kt`

## Implementation Guidance

- Use Cognito identity as the trusted user boundary when auth is wired.
- Validate record type, timestamps, ids, and ownership before writes.
- Keep all writes user-scoped by `PK = USER#<userId>`.
- Batch writes should be idempotent.
- Cloud report generation should read synced structured records and write cached report records.
- Avoid always-on infrastructure during the AWS credit period.

## Expected Deliverables

- DynamoDB table definition.
- Mobile sync API contract.
- User-scoped upsert and batch upsert behavior.
- Recovery read endpoint for a user's records.
- Optional report generation entry point.

## Verify

- Confirm one user cannot read or write another user's records.
- Confirm payloads align with `RecordMapper`.
- Confirm duplicate uploads do not create conflicting records.
- Confirm backend failure never deletes local mobile data.
