---
name: nursy-offline-storage
description: Build or modify Nursy AI offline storage, Room database schema, entities, DAOs, migrations, pending sync fields, local source-of-truth behavior, or unsynced record queries. Use when working on local persistence.
---

# Nursy Offline Storage

## Objective

Make Room DB the reliable local source of truth for all core mobile health data.

## Start Here

- Inspect `NursyDatabase.kt`, all `entity` files, and `HealthDao.kt`.
- Read `component` Phase 1 and Offline Storage sections.
- Check sync requirements before changing timestamps or pending sync fields.

## Implementation Guidance

- Store all user-entered health data locally first.
- Include stable ids, `createdAt`, `updatedAt`, and sync status metadata.
- Provide DAO methods for current state, history, active records, and unsynced
  records.
- Add migrations when changing persisted schema after a database version exists.
- Avoid making network availability part of local save success.
- Keep entities persistence-focused and map to UI models separately when needed.

## Expected Deliverables

- Complete Room entities for core health records.
- DAO methods for app flows and sync.
- Database version and migration updates when required.
- Unsynced record query support.

## Verify

- Compile the Android app after schema changes.
- Run Room-related tests when available.
- Confirm records save and reload offline with pending sync metadata intact.
