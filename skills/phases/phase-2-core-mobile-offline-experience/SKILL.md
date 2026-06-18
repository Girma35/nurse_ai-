---
name: nursy-phase-2-core-mobile-offline-experience
description: Execute Nursy AI Phase 2 core mobile offline experience work, including Jetpack Compose navigation, dashboard, check-in, symptoms, medications, timeline, profile, emergency card screens, Room integration, and local score summaries.
---

# Nursy Phase 2 Core Mobile Offline Experience

## Objective

Make the Android app genuinely useful without internet by connecting the main
mobile screens to local Room data.

## Start Here

- Read `component` Phase 2.
- Inspect `MainActivity.kt`, mobile UI files, Room entities, and `HealthDao.kt`.
- Use the component skills for Mobile App Shell, Daily Health Check-In, Symptom
  Tracking, Medication Management, Health Dashboard, Health Timeline,
  Authentication Profile, and Emergency Health Card as relevant.

## Work Sequence

1. Build or refine Compose navigation.
2. Implement dashboard, check-in, symptoms, medications, timeline, profile, and
   emergency card screens.
3. Connect each screen to local Room data through a clear state layer.
4. Add local health score and summary calculations.
5. Add empty states for first-run offline use.

## Guardrails

- Keep the app usable in airplane mode.
- Prefer local save success over cloud sync success in user-facing flows.
- Do not make the web app a dependency for mobile behavior.
- Keep screen scope practical for hackathon delivery.

## Exit Criteria

- User can use the main health tracking flows offline.
- Dashboard reflects locally saved data.
- Emergency card is available offline.

## Verify

- Run the Android app from Android Studio or compile with Gradle when possible.
- Manually save check-in, symptom, medication, and profile data offline.
- Confirm dashboard and timeline update from local data.
