---
name: nursy-cloud-backend
description: Build or modify the Nursy AI cloud backend, DynamoDB persistence, backend API routes, mobile sync endpoints, user-scoped record queries, dashboard read APIs, or cloud AI report entry points. Use when working on cloud services.
---

# Nursy Cloud Backend

## Objective

Provide a small cloud layer for synced health records, dashboard reads, and future cloud AI analysis. The backend uses a DynamoDB single-table design with CloudFormation infrastructure definitions.

## Infrastructure

### DynamoDB Table (`infra/dynamodb/nursy-ai-table.json`)

Created via CloudFormation with `TableName: "nursy-ai-health-records"`, `PAY_PER_REQUEST` billing, and point-in-time recovery.

**Key Schema**:
- `PK` (String, HASH) — `USER#<userId>`
- `SK` (String, RANGE) — `<entityType>#<timestamp>#<entityId>`

**GSI1**:
- `GSI1PK` (String) — entity type for type-scoped queries
- `GSI1SK` (String) — `updatedAt` for time-based sorting

### CloudFormation Output

The table name is exported via CloudFormation output `NursyAiTableName` for reference by other stacks (API Gateway, Lambda functions, etc.).

## API Contract

The mobile `SyncApiClient` expects these REST endpoints:

### POST `/v1/sync/upsert`
Upsert a single sync record.

**Request Body**: `SyncPayload` JSON
**Response**: `{ "success": true, "recordCount": 1, "message": "" }`

### POST `/v1/sync/batch`
Batch upsert multiple records in a single request.

**Request Body**: `SyncPayload[]` JSON array
**Response**: `{ "success": true, "recordCount": N, "message": "" }`

### GET `/v1/sync/records/:userId`
Fetch all records for a user.

### GET `/v1/sync/records/:userId/latest-checkin`
Fetch the latest check-in for a user.

### GET `/v1/sync/records/:userId/symptoms`
Fetch symptom records for a user.

### GET `/v1/sync/records/:userId/medications`
Fetch medication records for a user.

### GET `/v1/sync/summary/:userId`
Return pending count and last synced timestamp.

### GET `/v1/insights/:userId`
Return cloud-generated health insights.

### GET `/v1/reports/weekly/:userId`
Return the latest weekly report for a user.

## Start Here

- Read `infra/README.md` — DynamoDB key pattern documentation
- Inspect `infra/dynamodb/nursy-ai-table.json` — CloudFormation template
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/SyncApiClient.kt` — client-side API contract
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt` — payload shapes
- Inspect `apps/web/src/lib/api.ts` — web-side fetch client
- Inspect `packages/shared/src/index.ts` for shared record types
- Read component skills for Sync Engine and Web Dashboard

## Implementation Guidance

- **User-scoped keys**: All records use `PK = "USER#<userId>"`. One user's records cannot be queried as another user at the key level.
- **Type-scoped GSI**: `GSI1PK = entityType` allows querying all symptoms or all check-ins across a user without scanning the entire table.
- **Payload validation**: Server should validate record type, timestamps, ids, and ownership before writes.
- **Demo identity**: Currently uses `demo-user` as the user ID. No real auth is wired — the boundary is the `userId` field.
- **Cloud AI**: Report generation endpoints (`/insights`, `/reports/weekly`) are stubs. The `cloud-ai-reports.ts` module provides the server-side logic that should be deployed as a Lambda or serverless function.
- **Vercel compatibility**: The web dashboard (`apps/web`) deploys to Vercel and reads from the backend API.

## Expected Deliverables

- CloudFormation template `nursy-ai-table.json` with DynamoDB table definition.
- `infra/README.md` with key pattern documentation.
- `SyncApiClient.kt` — client implementing the API contract.
- `RecordMapper.kt` — entity-to-payload mapping.
- `api.ts` — web fetch layer with all endpoint calls.
- `cloud-ai-reports.ts` — server-side report generation logic.

## Verify

- Confirm API payloads align with shared models and mobile sync mapping.
- Confirm one user's records cannot be queried as another user (DynamoDB key design).
- Confirm web dashboard can read the intended data shape.
- Confirm `npm run typecheck:web` passes after API layer changes.
