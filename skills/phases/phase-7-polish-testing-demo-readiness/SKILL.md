---
name: nursy-phase-7-polish-testing-demo-readiness
description: Execute Nursy AI mobile polish, testing, and demo readiness work, including Android build validation, LocalRulesEngine tests, offline flow verification, sync failure testing, notification checks, realistic local demo data, and APK readiness.
---

# Nursy Phase 7 — Polish, Testing & Mobile Demo Readiness

## Objective

Stabilize the mobile product and make the demo clearly show offline health tracking, local insights, reminders, sync readiness, and optional cloud-enhanced reports.

## Focus Areas

- Offline-first mobile flows
- Local Room persistence
- LocalRulesEngine correctness
- AI symptom journal review-and-save flow
- Notification scheduling behavior
- WorkManager sync success/failure paths
- APK build readiness

## Test Coverage

Prioritize focused tests for:

- `LocalRulesEngine`
- journal parsing rules
- health score behavior
- repeated symptom rules
- sync payload mapping
- DAO behavior when Room tests are added

## Demo Data

Use realistic non-sensitive local records:

- 5-7 daily check-ins
- several symptoms with changing severity
- 1-2 medications with taken/missed events
- profile and emergency contacts
- generated local insights
- optional cached cloud report record

## Guardrails

- Avoid broad refactors during polish unless required for correctness.
- Keep demo data local and fake.
- Keep offline behavior visible in the demo.
- Cloud sync/report features should degrade gracefully when unavailable.

## Key Files

- `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/screens/*.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/*.kt`
- `apps/mobile/app/src/main/java/com/nursyai/notification/*.kt`

## Exit Criteria

- `cd apps/mobile && ./gradlew test` passes.
- `cd apps/mobile && ./gradlew assembleDebug` succeeds.
- Core screens render with empty and populated local data.
- Offline data entry works without network.
- Sync failures are non-destructive.

## Verify

```bash
cd apps/mobile
./gradlew test
./gradlew assembleDebug
./gradlew build
```

Manually walk through: create local records offline, view dashboard/timeline, parse a journal note, schedule a reminder, then trigger sync when online.
