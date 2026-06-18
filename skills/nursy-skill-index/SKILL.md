---
name: nursy-skill-index
description: Route Nursy AI project work to the correct repo-local component or phase skill. Use when an agent needs to list available Nursy skills, choose a skill for Mobile App Shell, profile, check-ins, symptoms, medications, dashboard, timeline, AI journal, insights, emergency card, notifications, offline storage, sync, backend, web dashboard, shared models, or build phases.
---

# Nursy Skill Index

## Purpose

Use this skill as the routing map for Nursy AI project skills. There are 24
repo-local skills total: 16 component skills and 8 phase skills.

## How To Use

1. Match the task to a component or phase below.
2. Read that skill's `SKILL.md` before editing code.
3. Follow the selected skill's start files, guardrails, deliverables, and
   verification steps.

Example prompts:

- `Use nursy-mobile-app-shell to build the Android navigation shell.`
- `Use nursy-phase-1-foundation-data-model to complete Room entities.`
- `Use nursy-sync-engine to implement WorkManager sync retries.`

## Component Skills

| Skill | Related component | Path |
|---|---|---|
| `nursy-mobile-app-shell` | Mobile App Shell | `skills/components/mobile-app-shell/SKILL.md` |
| `nursy-authentication-profile` | Authentication & Profile | `skills/components/authentication-profile/SKILL.md` |
| `nursy-daily-health-check-in` | Daily Health Check-In | `skills/components/daily-health-check-in/SKILL.md` |
| `nursy-symptom-tracking` | Symptom Tracking | `skills/components/symptom-tracking/SKILL.md` |
| `nursy-medication-management` | Medication Management | `skills/components/medication-management/SKILL.md` |
| `nursy-health-dashboard` | Health Dashboard | `skills/components/health-dashboard/SKILL.md` |
| `nursy-health-timeline` | Health Timeline | `skills/components/health-timeline/SKILL.md` |
| `nursy-ai-symptom-journal` | AI Symptom Journal | `skills/components/ai-symptom-journal/SKILL.md` |
| `nursy-local-ai-health-insights` | Local AI Health Insights | `skills/components/local-ai-health-insights/SKILL.md` |
| `nursy-emergency-health-card` | Emergency Health Card | `skills/components/emergency-health-card/SKILL.md` |
| `nursy-notifications` | Notifications | `skills/components/notifications/SKILL.md` |
| `nursy-offline-storage` | Offline Storage / Room DB | `skills/components/offline-storage/SKILL.md` |
| `nursy-sync-engine` | Sync Engine | `skills/components/sync-engine/SKILL.md` |
| `nursy-cloud-backend` | Cloud Backend | `skills/components/cloud-backend/SKILL.md` |
| `nursy-web-dashboard` | Web Dashboard | `skills/components/web-dashboard/SKILL.md` |
| `nursy-shared-models` | Shared Models | `skills/components/shared-models/SKILL.md` |

## Phase Skills

| Skill | Related phase | Path |
|---|---|---|
| `nursy-phase-1-foundation-data-model` | Phase 1: Foundation and Data Model | `skills/phases/phase-1-foundation-data-model/SKILL.md` |
| `nursy-phase-2-core-mobile-offline-experience` | Phase 2: Core Mobile Offline Experience | `skills/phases/phase-2-core-mobile-offline-experience/SKILL.md` |
| `nursy-phase-3-local-rules-ai-symptom-journal` | Phase 3: Local Rules and AI Symptom Journal | `skills/phases/phase-3-local-rules-ai-symptom-journal/SKILL.md` |
| `nursy-phase-4-notifications-reminders` | Phase 4: Notifications and Reminders | `skills/phases/phase-4-notifications-reminders/SKILL.md` |
| `nursy-phase-5-sync-cloud-persistence` | Phase 5: Sync and Cloud Persistence | `skills/phases/phase-5-sync-cloud-persistence/SKILL.md` |
| `nursy-phase-6-web-dashboard-integration` | Phase 6: Web Dashboard Integration | `skills/phases/phase-6-web-dashboard-integration/SKILL.md` |
| `nursy-phase-7-cloud-ai-weekly-reports` | Phase 7: Cloud AI and Weekly Reports | `skills/phases/phase-7-cloud-ai-weekly-reports/SKILL.md` |
| `nursy-phase-8-polish-testing-demo-readiness` | Phase 8: Polish, Testing, and Demo Readiness | `skills/phases/phase-8-polish-testing-demo-readiness/SKILL.md` |

## Access Pattern For Agentic AI

Ask the agent to use the skill by name, then include the path if it does not
auto-discover repo-local skills:

```text
Use nursy-skill-index at skills/nursy-skill-index/SKILL.md.
Then choose the right Nursy component or phase skill and read its SKILL.md before making changes.
```

For a specific component:

```text
Use nursy-mobile-app-shell at skills/components/mobile-app-shell/SKILL.md to build the Android app shell.
```

For a phase:

```text
Use nursy-phase-2-core-mobile-offline-experience at skills/phases/phase-2-core-mobile-offline-experience/SKILL.md.
```
