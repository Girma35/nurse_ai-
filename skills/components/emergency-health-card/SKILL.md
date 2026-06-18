---
name: nursy-emergency-health-card
description: Build or modify the Nursy AI emergency health card, offline critical profile display, blood type, allergies, chronic conditions, emergency contacts, or one-tap access. Use when working on emergency-access health information.
---

# Nursy Emergency Health Card

## Objective

Make critical health information available quickly and offline from the mobile app. The emergency card combines `ProfileEntity` (blood type, allergies, conditions) with `EmergencyContactEntity` (name, relationship, phone) into a single screen accessible via the app's navigation.

## Screen Layout (`EmergencyCardScreen.kt`)

```
Column
├─ Header: "Emergency Card" + "Critical health info — available offline"
├─ Emergency Card Surface (coral tint, #FFF7F4)
│   ├─ "🔴 Emergency Card" badge
│   ├─ Profile: fullName, bloodType, allergies, conditions
│   └─ Emergency Contacts (each in a white card)
│       ├─ Name, Relationship, Phone number
│       └─ (empty state when no contacts)
└─ Add Emergency Contact Form Surface
    ├─ Name, Relationship, Phone inputs
    └─ "Add Contact" button (coral color)
```

## Data Sources

| Field | Source Entity | Type |
|---|---|---|
| Full Name | `ProfileEntity.fullName` | Room |
| Blood Type | `ProfileEntity.bloodType` | Room |
| Allergies | `ProfileEntity.allergies` (comma-separated) | Room |
| Chronic Conditions | `ProfileEntity.chronicConditions` (comma-separated) | Room |
| Emergency Contacts | `EmergencyContactEntity[]` | Room |

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/EmergencyCardScreen.kt` — the Compose screen
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/ProfileEntity.kt` — health profile fields
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/EmergencyContactEntity.kt` — emergency contact fields
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `profile` StateFlow, `emergencyContacts` Flow, `addEmergencyContact()`, `deleteEmergencyContact()`
- Inspect `apps/web/src/app/page.tsx` — web emergency card display (read-only)
- Inspect `apps/web/src/lib/enhanced-mock-data.ts` — rich demo emergency data
- Read component skills for Authentication & Profile

## Implementation Guidance

- **Offline-first**: Reads data directly from Room. No network calls needed.
- **Profile reuse**: Uses the same `ProfileEntity` and `EmergencyContactEntity` as the Profile screen — no data duplication.
- **Fast access**: Emergency card is accessible from navigation. Does not require session refresh, cloud auth, or sync.
- **Empty state**: When no profile or contacts exist, the card shows "Complete your profile and add emergency contacts to see them here."
- **Add contact inline**: The screen includes a form to add emergency contacts directly without navigating away.
- **Web display**: The web dashboard shows a read-only version of the emergency card from mock data.

## Expected Deliverables

- `EmergencyCardScreen.kt` — emergency card display + add contact form + empty state.
- Shared data via `ProfileEntity` and `EmergencyContactEntity`.
- `NursyViewModel.addEmergencyContact()` and `deleteEmergencyContact()`.
- Web emergency card section in `page.tsx` with `EmergencyHealthCard` type.

## Verify

- Confirm emergency card renders in airplane mode.
- Confirm profile edits from Profile screen update emergency card content.
- Confirm long allergy or condition lists remain readable on small screens.
- Confirm emergency contacts can be added and deleted.
