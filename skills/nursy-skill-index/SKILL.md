---
name: nursy-skill-index
description: Route Nursy AI mobile-only project work to the correct repo-local component or phase skill. Use when an agent needs to list available Nursy skills, choose a skill for Android UI, premium mobile feel, app shell, profile, check-ins, symptoms, medications, mobile dashboard, timeline, AI journal, local insights, emergency card, notifications, offline storage, sync, AWS backend, cloud reports, NursyViewModel, navigation, or build phases.
---

# Nursy Skill Index

## Purpose

Use this skill as the routing map for Nursy AI project skills. Nursy AI is now mobile-only:

```text
Mobile-first, offline-first, cloud-enhanced.
```

The Android app is the product. AWS supports auth, backup, sync, recovery, and optional premium reports. There is no web dashboard or shared TypeScript workspace.

## Health Safety Boundary

All Nursy skills must keep the product in the health tracking companion category. App copy, insight text, notifications, prompts, and generated reports must not diagnose, predict disease, prescribe treatment, or replace clinician advice.

Use safe wording such as:

- "This is a pattern worth reviewing with a doctor."
- "You might want to mention this at your next appointment."
- "Here is a summary of your symptoms."
- "No diagnosis is provided, only tracking insights."

Do not write phrases such as "you may have disease X", "this is likely diabetes/depression/infection", treatment instructions, or emergency diagnosis claims.

## How To Use

1. Match the task to a component or phase below.
2. Read that skill's `SKILL.md` before editing code.
3. Follow the selected skill's start files, guardrails, deliverables, and verification.

Example prompts:

- `Use nursy-mobile-app-shell at skills/components/mobile-app-shell/SKILL.md to update Android navigation.`
- `Use nursy-sync-engine at skills/components/sync-engine/SKILL.md to implement WorkManager sync retries.`
- `Use nursy-phase-6-cloud-ai-reports at skills/phases/phase-6-cloud-ai-reports/SKILL.md for optional premium reports.`

## Component Skills

| Skill | Component | Key Files | Path |
|---|---|---|---|
| `android-mobile-ui` | Android UI quality, Material 3, premium feel, accessibility | Compose screens, theme, reusable UI components | `skills/components/android-mobile-ui/SKILL.md` |
| `nursy-mobile-app-shell` | Android shell, Compose nav, bottom bar, scaffold | `MainActivity.kt`, `NavigationRoutes.kt`, `build.gradle.kts` | `skills/components/mobile-app-shell/SKILL.md` |
| `nursy-authentication-profile` | Auth profile, health profile, emergency contacts | `ProfileEntity.kt`, `ProfileScreen.kt`, `EmergencyContactEntity.kt` | `skills/components/authentication-profile/SKILL.md` |
| `nursy-daily-health-check-in` | Mood, energy, sleep, stress, water form | `DailyCheckInEntity.kt`, `DailyCheckInScreen.kt`, `NursyViewModel.kt` | `skills/components/daily-health-check-in/SKILL.md` |
| `nursy-symptom-tracking` | Symptom CRUD, severity, duration, resolve | `SymptomEntity.kt`, `SymptomTrackingScreen.kt`, `HealthDao.kt` | `skills/components/symptom-tracking/SKILL.md` |
| `nursy-medication-management` | Medications, dose events, adherence, scheduling | `MedicationEntity.kt`, `MedicationManagementScreen.kt`, `MedicationDoseEventEntity.kt` | `skills/components/medication-management/SKILL.md` |
| `nursy-health-dashboard` | Mobile dashboard, score, metrics, insights, sync status | `DashboardScreen.kt`, `NursyViewModel.kt`, `LocalRulesEngine.kt` | `skills/components/health-dashboard/SKILL.md` |
| `nursy-health-timeline` | Chronological local health history | `HealthTimelineScreen.kt`, local Room entities | `skills/components/health-timeline/SKILL.md` |
| `nursy-ai-symptom-journal` | Local natural-language symptom parser | `SymptomJournalScreen.kt`, `LocalRulesEngine.kt` | `skills/components/ai-symptom-journal/SKILL.md` |
| `nursy-local-ai-health-insights` | Offline deterministic health insights | `LocalRulesEngine.kt`, `LocalRulesEngineTest.kt` | `skills/components/local-ai-health-insights/SKILL.md` |
| `nursy-emergency-health-card` | Offline blood type, allergies, contacts | `EmergencyCardScreen.kt`, `EmergencyContactEntity.kt`, `ProfileEntity.kt` | `skills/components/emergency-health-card/SKILL.md` |
| `nursy-notifications` | Notification permission, channels, reminders | `NotificationHelper.kt`, `ReminderScheduler.kt`, `AndroidManifest.xml` | `skills/components/notifications/SKILL.md` |
| `nursy-offline-storage` | Room DB, entities, DAO, migrations | `NursyDatabase.kt`, `*Entity.kt`, `HealthDao.kt` | `skills/components/offline-storage/SKILL.md` |
| `nursy-sync-engine` | WorkManager sync, API client, DynamoDB mapper | `HealthSyncWorker.kt`, `SyncApiClient.kt`, `RecordMapper.kt` | `skills/components/sync-engine/SKILL.md` |
| `nursy-cloud-backend` | AWS auth/sync/recovery and optional cloud AI endpoints | `infra/dynamodb/nursy-ai-table.json`, `infra/README.md`, `SyncApiClient.kt` | `skills/components/cloud-backend/SKILL.md` |

## Phase Skills

| Skill | Phase | Dependencies | Path |
|---|---|---|---|
| `nursy-phase-1-foundation-data-model` | Phase 1: Foundation and Room data model | Offline Storage | `skills/phases/phase-1-foundation-data-model/SKILL.md` |
| `nursy-phase-2-core-mobile-offline-experience` | Phase 2: Core mobile offline experience | App Shell, Dashboard, Check-In, Symptoms, Medications, Timeline, Profile, Emergency Card | `skills/phases/phase-2-core-mobile-offline-experience/SKILL.md` |
| `nursy-phase-3-local-rules-ai-symptom-journal` | Phase 3: Local rules and AI symptom journal | Local AI Insights, AI Symptom Journal | `skills/phases/phase-3-local-rules-ai-symptom-journal/SKILL.md` |
| `nursy-phase-4-notifications-reminders` | Phase 4: Notifications and reminders | Notifications, Medication Management | `skills/phases/phase-4-notifications-reminders/SKILL.md` |
| `nursy-phase-5-sync-cloud-persistence` | Phase 5: Sync and cloud persistence | Sync Engine, Cloud Backend, Offline Storage | `skills/phases/phase-5-sync-cloud-persistence/SKILL.md` |
| `nursy-phase-6-cloud-ai-reports` | Phase 6: Optional cloud AI reports | Cloud Backend, Sync Engine, Local AI Insights | `skills/phases/phase-6-cloud-ai-reports/SKILL.md` |
| `nursy-phase-7-polish-testing-demo-readiness` | Phase 7: Polish, testing, and mobile demo readiness | All mobile components | `skills/phases/phase-7-polish-testing-demo-readiness/SKILL.md` |

## Access Pattern

For broad work:

```text
Use nursy-skill-index at skills/nursy-skill-index/SKILL.md.
Then choose the right Nursy component or phase skill and read its SKILL.md before making changes.
```

For a specific component:

```text
Use nursy-health-dashboard at skills/components/health-dashboard/SKILL.md to update the mobile dashboard score card.
```

For a phase:

```text
Use nursy-phase-5-sync-cloud-persistence at skills/phases/phase-5-sync-cloud-persistence/SKILL.md.
```
