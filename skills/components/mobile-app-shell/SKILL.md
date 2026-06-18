---
name: nursy-mobile-app-shell
description: Build or modify the Nursy AI Android mobile app shell, Jetpack Compose navigation, primary screens, offline-first mobile UX, dashboard entry points, or emergency card access. Use when working on the main mobile user experience.
---

# Nursy Mobile App Shell

## Objective

Build the Android app as the primary Nursy AI product. Keep the experience
offline-first and make daily health tracking reachable with minimal navigation.

## Start Here

- Read `component` and `README.md` for product scope.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/MainActivity.kt`.
- Inspect `apps/mobile/app/build.gradle.kts` before adding Compose or navigation dependencies.
- Reuse local Room, sync, and rules packages instead of inventing parallel app layers.

## Implementation Guidance

- Treat mobile as the main product and web as a secondary dashboard.
- Use Jetpack Compose for screens and state rendering.
- Create stable routes for dashboard, daily check-in, symptoms, medications,
  timeline, profile, and emergency card.
- Keep emergency card access fast and available without network access.
- Prefer simple screen state objects over passing database entities directly
  through the UI.
- Keep cloud-dependent actions secondary to local capture and review.

## Expected Deliverables

- Compose navigation structure.
- Dashboard entry screen.
- Daily check-in, symptom, medication, profile, timeline, and emergency card
  entry points.
- Offline-friendly empty and loading states.

## Verify

- Confirm the mobile project syncs in Android Studio.
- Run relevant Gradle compile or test tasks when available.
- Manually trace the no-network user path from launch to saving a local health
  record.
