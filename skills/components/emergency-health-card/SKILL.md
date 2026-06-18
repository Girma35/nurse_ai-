---
name: nursy-emergency-health-card
description: Build or modify the Nursy AI emergency health card, offline critical profile display, blood type, allergies, chronic conditions, emergency contacts, or one-tap access. Use when working on emergency-access health information.
---

# Nursy Emergency Health Card

## Objective

Make critical health information available quickly and offline from the mobile
app.

## Start Here

- Read `component` sections for Emergency Health Card and Authentication and
  Profile.
- Inspect profile-related entities or add them if missing.
- Inspect `MainActivity.kt` and navigation code before adding access points.

## Implementation Guidance

- Display name, blood type, allergies, chronic conditions, and emergency
  contacts.
- Store the data locally and read it without network calls.
- Keep access prominent enough for urgent use but not disruptive to daily flows.
- Avoid requiring session refresh, cloud auth, or sync completion to show saved
  emergency data.
- Reuse profile data where possible to prevent mismatches.

## Expected Deliverables

- Emergency card data model or profile-backed projection.
- Emergency card screen.
- Fast navigation entry point.
- Offline empty state for missing emergency fields.

## Verify

- Confirm emergency card renders in airplane mode.
- Confirm profile edits update emergency card content.
- Confirm long allergy or condition lists remain readable on small screens.
