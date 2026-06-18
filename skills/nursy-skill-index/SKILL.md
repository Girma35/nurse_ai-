---
name: nursy-skill-index
description: Route Nursy AI project work to the correct repo-local component or phase skill. Use when an agent needs to list available Nursy skills, choose a skill for Mobile App Shell, profile, check-ins, symptoms, medications, dashboard, timeline, AI journal, insights, emergency card, notifications, offline storage, sync, backend, web dashboard, shared models, NursyViewModel, navigation, or build phases.
---

# Nursy Skill Index

## Purpose

Use this skill as the routing map for Nursy AI project skills. There are **24 repo-local skills total**: 16 component skills and 8 phase skills. This index is the entry point — match your task below, then read the selected skill's `SKILL.md` before editing any code.

## How To Use

1. **Match the task** to a component or phase in the tables below.
2. **Read that skill's `SKILL.md`** before editing any code — it contains guardrails, file paths, data models, and verification steps.
3. **Follow the selected skill's guidance** — start files, guardrails, deliverables, and verification.

If the task spans multiple components (e.g. "build the check-in screen with Room data"), use the relevant component skill for the UI layer and the relevant phase skill for the integration sequencing.

Example prompts:

- `Use nursy-mobile-app-shell at skills/components/mobile-app-shell/SKILL.md to build the Android navigation shell.`
- `Use nursy-phase-1-foundation-data-model at skills/phases/phase-1-foundation-data-model/SKILL.md to complete Room entities.`
- `Use nursy-sync-engine at skills/components/sync-engine/SKILL.md to implement WorkManager sync retries.`

## Component Skills (16)

These describe individual feature modules and their concrete implementations.

| Skill | Component | Key Files | Path |
|---|---|---|---|
| `nursy-mobile-app-shell` | Mobile App Shell — Compose nav, bottom bar, app scaffold | `MainActivity.kt`, `NavigationRoutes.kt`, `build.gradle.kts` | `skills/components/mobile-app-shell/SKILL.md` |
| `nursy-authentication-profile` | Authentication & Profile — user health profile, emergency contacts | `ProfileEntity.kt`, `ProfileScreen.kt`, `EmergencyContactEntity.kt` | `skills/components/authentication-profile/SKILL.md` |
| `nursy-daily-health-check-in` | Daily Health Check-In — mood, energy, sleep, stress, water form | `DailyCheckInEntity.kt`, `DailyCheckInScreen.kt`, `NursyViewModel.kt` | `skills/components/daily-health-check-in/SKILL.md` |
| `nursy-symptom-tracking` | Symptom Tracking — symptom CRUD, severity, duration, resolve | `SymptomEntity.kt`, `SymptomTrackingScreen.kt`, `HealthDao.kt` | `skills/components/symptom-tracking/SKILL.md` |
| `nursy-medication-management` | Medication Management — meds, doses, adherence, scheduling | `MedicationEntity.kt`, `MedicationManagementScreen.kt`, `MedicationDoseEventEntity.kt` | `skills/components/medication-management/SKILL.md` |
| `nursy-health-dashboard` | Health Dashboard — score ring, quick metrics, insights list | `DashboardScreen.kt`, `HealthScore.tsx`, `HealthScore.kt` (compose variant) | `skills/components/health-dashboard/SKILL.md` |
| `nursy-health-timeline` | Health Timeline — chronological events from all sources | `HealthTimelineScreen.kt`, `buildTimelineEvents()` in Kotlin & `@nursy/shared` | `skills/components/health-timeline/SKILL.md` |
| `nursy-ai-symptom-journal` | AI Symptom Journal — NL parser, review-and-save, severity/duration | `SymptomJournalScreen.kt`, `LocalRulesEngine.kt` (`parseJournalNote`) | `skills/components/ai-symptom-journal/SKILL.md` |
| `nursy-local-ai-health-insights` | Local AI Health Insights — 7 rule types, `HealthInsight` model, tests | `LocalRulesEngine.kt`, `LocalRulesEngineTest.kt` | `skills/components/local-ai-health-insights/SKILL.md` |
| `nursy-emergency-health-card` | Emergency Health Card — offline blood type, allergies, contacts | `EmergencyCardScreen.kt`, `EmergencyContactEntity.kt`, `ProfileEntity.kt` | `skills/components/emergency-health-card/SKILL.md` |
| `nursy-notifications` | Notifications — permission, channels, alarm reminders | `NotificationHelper.kt`, `ReminderScheduler.kt`, `AndroidManifest.xml` | `skills/components/notifications/SKILL.md` |
| `nursy-offline-storage` | Offline Storage / Room DB — all 6 entities, DAO, migrations | `NursyDatabase.kt`, `*Entity.kt`, `HealthDao.kt` | `skills/components/offline-storage/SKILL.md` |
| `nursy-sync-engine` | Sync Engine — WorkManager, API client, DynamoDB mapper | `HealthSyncWorker.kt`, `SyncApiClient.kt`, `RecordMapper.kt` | `skills/components/sync-engine/SKILL.md` |
| `nursy-cloud-backend` | Cloud Backend — API contract, DynamoDB table, infra | `infra/dynamodb/nursy-ai-table.json`, `infra/README.md`, `SyncApiClient.kt` | `skills/components/cloud-backend/SKILL.md` |
| `nursy-web-dashboard` | Web Dashboard — Next.js, components, API layer, mock fallback | `apps/web/src/app/page.tsx`, `components/*`, `lib/api.ts`, `lib/mock-data.ts` | `skills/components/web-dashboard/SKILL.md` |
| `nursy-shared-models` | Shared Models — TypeScript types, scoring helpers, sync records | `packages/shared/src/index.ts` | `skills/components/shared-models/SKILL.md` |

## Phase Skills (8)

These describe build phases that sequence cross-cutting work across multiple components.

| Skill | Phase | Dependencies | Path |
|---|---|---|---|
| `nursy-phase-1-foundation-data-model` | Phase 1: Foundation & Data Model — entities, DAO, shared types, sync metadata | Shared Models, Offline Storage | `skills/phases/phase-1-foundation-data-model/SKILL.md` |
| `nursy-phase-2-core-mobile-offline-experience` | Phase 2: Core Mobile Offline — Compose nav, all screens, ViewModel | Mobile App Shell, Dashboard, Check-In, Symptoms, Medications, Timeline, Profile, Emergency Card | `skills/phases/phase-2-core-mobile-offline-experience/SKILL.md` |
| `nursy-phase-3-local-rules-ai-symptom-journal` | Phase 3: Local Rules & AI Journal — rules engine, parser, tests | Local AI Insights, AI Symptom Journal | `skills/phases/phase-3-local-rules-ai-symptom-journal/SKILL.md` |
| `nursy-phase-4-notifications-reminders` | Phase 4: Notifications & Reminders — alarms, permissions, dose tracking | Notifications, Medication Management | `skills/phases/phase-4-notifications-reminders/SKILL.md` |
| `nursy-phase-5-sync-cloud-persistence` | Phase 5: Sync & Cloud — WorkManager, DynamoDB, API client | Sync Engine, Cloud Backend, Offline Storage | `skills/phases/phase-5-sync-cloud-persistence/SKILL.md` |
| `nursy-phase-6-web-dashboard-integration` | Phase 6: Web Dashboard — live data, states, API layer | Web Dashboard, Cloud Backend, Shared Models | `skills/phases/phase-6-web-dashboard-integration/SKILL.md` |
| `nursy-phase-7-cloud-ai-weekly-reports` | Phase 7: Cloud AI & Weekly Reports — trends, report gen, insights | Cloud Backend, Shared Models | `skills/phases/phase-7-cloud-ai-weekly-reports/SKILL.md` |
| `nursy-phase-8-polish-testing-demo-readiness` | Phase 8: Polish & Demo — tests, seed data, build validation | All components | `skills/phases/phase-8-polish-testing-demo-readiness/SKILL.md` |

## Access Pattern For Agentic AI

Ask the agent to use the skill by name, then include the path if it does not auto-discover repo-local skills:

```text
Use nursy-skill-index at skills/nursy-skill-index/SKILL.md.
Then choose the right Nursy component or phase skill and read its SKILL.md before making changes.
```

For a specific component:

```text
Use nursy-health-dashboard at skills/components/health-dashboard/SKILL.md to update the dashboard health score card.
```

For a phase:

```text
Use nursy-phase-5-sync-cloud-persistence at skills/phases/phase-5-sync-cloud-persistence/SKILL.md.
```
