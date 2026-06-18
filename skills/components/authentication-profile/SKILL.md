---
name: nursy-authentication-profile
description: Build or modify Nursy AI authentication, user profile, health profile, allergies, chronic conditions, blood type, emergency contacts, or profile sync behavior. Use when working on user identity and personal health context.
---

# Nursy Authentication Profile

## Objective

Implement identity and health profile data without blocking offline health
tracking. Store the user's health context locally and sync it when possible.

## Start Here

- Read `component` sections for Authentication and Profile and Emergency Health Card.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local`.
- Inspect `infra/README.md` for DynamoDB sync key patterns when adding cloud mapping.
- Check `packages/shared/src/index.ts` before adding web or backend profile types.

## Implementation Guidance

- Keep profile data available offline after first entry.
- Model age, gender, weight, height, blood type, allergies, chronic conditions,
  and emergency contacts explicitly.
- Separate authentication credentials from health profile records.
- Track `createdAt`, `updatedAt`, and pending sync metadata on local records.
- Make emergency card data read from the same profile source where practical.
- Do not require cloud auth completion before saving local health profile fields
  during hackathon flows unless the feature explicitly needs remote identity.

## Expected Deliverables

- Local profile and emergency contact entities.
- DAO methods for reading and updating profile data.
- Profile edit UI.
- Sync record shape for profile updates.

## Verify

- Confirm profile edits persist after app restart.
- Confirm emergency card can render profile-critical fields offline.
- Confirm shared TypeScript model changes match the mobile data shape if web or
  backend data is touched.
