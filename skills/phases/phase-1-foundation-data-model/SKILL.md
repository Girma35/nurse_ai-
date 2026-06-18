---
name: nursy-phase-1-foundation-data-model
description: Execute Nursy AI Phase 1 foundation and data model work, including Room entities, DAOs, profile, check-ins, symptoms, medications, medication events, emergency contacts, sync metadata, shared TypeScript models, and canonical sync fields.
---

# Nursy Phase 1 Foundation Data Model

## Objective

Make the product data model solid before adding many screens. Prioritize local
persistence, shared contracts, and sync-ready metadata.

## Start Here

- Read `component` Phase 1.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local`.
- Inspect `packages/shared/src/index.ts`.
- Inspect `infra/README.md` for sync key expectations.

## Work Sequence

1. Review existing Room entities and DAO methods.
2. Add or complete entities for profile, check-ins, symptoms, medications,
   medication events, emergency contacts, and sync metadata.
3. Add DAO methods for create, update, history, active records, and unsynced
   records.
4. Expand shared TypeScript models to match the mobile data model semantics.
5. Define canonical sync fields: stable id, entity type, user id, `createdAt`,
   `updatedAt`, sync status, `deviceId`, and `syncVersion` where needed.

## Guardrails

- Keep Room DB as the offline source of truth.
- Do not block local record creation on cloud availability.
- Avoid UI-heavy changes until the data model supports the core flows.
- Add migrations when database versioning requires them.

## Exit Criteria

- Mobile can save and read core health records locally.
- Shared models describe the same records used by mobile and web.
- Unsynced local records can be queried.

## Verify

- Compile or test the mobile data layer when available.
- Run `npm run typecheck:web` if shared TypeScript changes affect web imports.
- Confirm every syncable entity has pending-sync query support.
