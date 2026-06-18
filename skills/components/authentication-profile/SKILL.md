---
name: nursy-authentication-profile
description: Build or modify Nursy AI mobile authentication, local profile storage, health profile fields, emergency contacts, Cognito integration boundaries, or profile sync behavior.
---

# Nursy Authentication & Profile

## Objective

Keep identity and health profile data available locally while allowing AWS auth and recovery when the user is online.

## Profile Data

- full name
- date of birth
- gender
- weight kg
- height cm
- blood type
- allergies
- chronic conditions
- emergency contacts

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/ProfileEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/EmergencyContactEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/ProfileScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/EmergencyCardScreen.kt`
- Inspect profile and contact queries in `HealthDao.kt`

## Implementation Guidance

- Store profile data locally first.
- Treat Cognito identity as the cloud account boundary when auth is implemented.
- Never require a fresh network login to show the emergency card.
- Keep allergies and chronic conditions readable offline.
- Sync profile updates as queued records like other health data.

## Expected Deliverables

- Local profile edit flow.
- Emergency contacts flow.
- Profile records queued for sync.
- Clear user id mapping for future Cognito integration.

## Verify

- Confirm profile saves offline.
- Confirm emergency card remains available offline.
- Confirm profile/contact sync payloads are user-scoped.
- Confirm auth failures do not erase local health data.
