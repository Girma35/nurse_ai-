---
name: nursy-emergency-health-card
description: Build or modify the Nursy AI offline emergency health card, blood type, allergies, chronic conditions, emergency contacts, quick access navigation, or local profile display.
---

# Nursy Emergency Health Card

## Objective

Keep critical medical context available instantly and offline.

## Data Displayed

- full name
- blood type
- allergies
- chronic conditions
- emergency contacts

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/EmergencyCardScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/ProfileEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/EmergencyContactEntity.kt`
- Inspect `HealthDao.kt` profile/contact queries

## Implementation Guidance

- Do not depend on network, sync state, or auth refresh.
- Make critical fields scannable.
- Prefer direct navigation from the main app shell.
- Keep empty states actionable, e.g. prompt profile completion.

## Expected Deliverables

- Offline emergency card screen.
- Emergency contact list.
- Clear empty states for missing profile data.

## Verify

- Confirm emergency card opens in airplane mode.
- Confirm contact data persists after app restart.
- Confirm missing profile fields do not crash the screen.
