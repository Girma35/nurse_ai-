---
name: nursy-cloud-backend
description: Build or modify the Nursy AI cloud backend, DynamoDB persistence, backend API routes, mobile sync endpoints, user-scoped record queries, dashboard read APIs, or cloud AI report entry points. Use when working on cloud services.
---

# Nursy Cloud Backend

## Objective

Provide a small cloud layer for synced health records, dashboard reads, and
future cloud AI analysis.

## Start Here

- Read `infra/README.md` and `infra/dynamodb/nursy-ai-table.json`.
- Inspect `apps/web` before adding API routes or dashboard fetches.
- Inspect `packages/shared/src/index.ts` for shared record types.
- Read `component` sections for Cloud Backend, Sync Engine, and Web Dashboard.

## Implementation Guidance

- Keep backend scope focused on sync, read APIs, and analysis entry points.
- Store records using user-scoped keys.
- Validate record type, timestamps, ids, and ownership before writes.
- Return dashboard-friendly summaries without making the web app a full product.
- Keep cloud AI optional until offline data and sync are stable.

## Expected Deliverables

- API contract for upserting synced records.
- DynamoDB access layer.
- User-scoped query path for dashboard data.
- Basic authentication or demo identity boundary.
- Cloud report placeholder only when needed.

## Verify

- Confirm API payloads align with shared models and mobile sync mapping.
- Confirm one user's records cannot be queried as another user.
- Confirm web dashboard can read the intended data shape.
