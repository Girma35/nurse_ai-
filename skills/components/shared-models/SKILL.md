---
name: nursy-shared-models
description: Build or modify Nursy AI shared TypeScript health models, scoring helpers, sync record types, constants, dashboard data contracts, or cross-platform model alignment. Use when coordinating data shapes across mobile, backend, and web.
---

# Nursy Shared Models

## Objective

Keep web, backend, and sync contracts aligned through shared TypeScript models
and deterministic helper functions.

## Start Here

- Inspect `packages/shared/src/index.ts`.
- Inspect mobile Room entities before designing shared record shapes.
- Inspect web dashboard imports to avoid breaking workspace consumers.
- Read `component` sections for Shared Models, Offline Storage, Sync Engine, and
  Web Dashboard.

## Implementation Guidance

- Define shared health record types for check-ins, symptoms, medications,
  medication events, profiles, insights, and sync records.
- Export scoring helpers and constants used by the web dashboard and backend.
- Keep TypeScript shared models aligned with mobile entities by field meaning,
  not necessarily by exact persistence details.
- Avoid importing Android-specific concepts into shared TypeScript.
- Version or document sync fields when changing cross-system contracts.

## Expected Deliverables

- Expanded shared model exports.
- Shared score and summary helpers.
- Sync record type definitions.
- Updated web or backend imports when types change.

## Verify

- Run package type checks.
- Confirm web builds after shared package changes.
- Confirm mobile sync mapping can produce the shared cloud shape.
