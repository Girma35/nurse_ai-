---
name: nursy-phase-7-cloud-ai-weekly-reports
description: Execute Nursy AI Phase 7 cloud AI and weekly reports work, including symptom trend analysis, health deterioration patterns, medication effectiveness heuristic, weekly report generation from synced data, cloud insight storage, and mobile/web report display via WeeklyReportCard and InsightCarousel components.
---

# Nursy Phase 7 — Cloud AI & Weekly Reports

## Objective

Add advanced cloud intelligence only after offline data capture and sync are stable. Generate weekly reports and cloud-level health insights from synced user data. All generation logic is server-side and reproducible.

## What Was Built

### Weekly Report Generation (`cloud-ai-reports.ts`)

`generateWeeklyReport(input: ReportInput): WeeklyReport`

Input includes check-ins, symptoms, medications, and dose events for the week. Output includes:

| Field | Calculation |
|---|---|
| `averageHealthScore` | Average of `calculateHealthScore()` across all check-ins |
| `adherenceRate` | `taken / (taken + missed) * 100` across all medications |
| `symptomSummary` | "Headache (3 logs), Fatigue (1 log)" format |
| `highlights` | Completed check-ins count, adherence quality, symptom-free days |
| `recommendations` | Contextual: increase check-ins if < 7, improve adherence if < 80%, discuss patterns if multiple symptoms |

### Symptom Trend Analysis (`cloud-ai-reports.ts`)

`analyzeSymptomTrends(symptoms: SymptomLog[]): HealthInsight[]`

- Groups symptoms by name
- When a symptom appears 3+ times, generates a trend insight with average severity
- Escalates to `severity: "warning"` at 5+ occurrences
- `logsAreDecreasing()` heuristic: compares average severity of first half vs second half of symptom logs

### Web Display

- `WeeklyReportCard.tsx` — renders full weekly report with score, adherence, symptom summary, highlights, recommendations
- `InsightCarousel.tsx` — renders cloud-generated insights with severity coloring
- Both components used in `page.tsx` as part of the main dashboard sections

### Mobile Integration Points

- The `HealthInsight` type is already shared between mobile (`LocalRulesEngine`) and cloud (`cloud-ai-reports.ts`)
- Mobile `DashboardScreen.kt` renders insights via `InsightCard` composable
- Cloud insights can be synced back to mobile as `SyncPayload` records

## Key Files

- `apps/web/src/lib/cloud-ai-reports.ts` — `generateWeeklyReport()`, `analyzeSymptomTrends()`, `logsAreDecreasing()`
- `apps/web/src/components/WeeklyReportCard.tsx` — report display component
- `apps/web/src/components/InsightCarousel.tsx` — insight display component
- `apps/web/src/app/page.tsx` — dashboard sections showing reports and insights
- `packages/shared/src/index.ts` — `WeeklyReport`, `HealthInsight` types

## Guardrails

- Do not block core mobile value on cloud AI — mobile works entirely offline.
- Label cloud outputs as "insights" or "reports", not "diagnosis".
- Store generated results with timestamps and source date ranges.
- Keep report generation reproducible enough to debug demo data — all functions are pure.

## Exit Criteria

- Cloud insights are generated as `HealthInsight[]` from synced symptom data.
- Weekly reports are generated as `WeeklyReport` from synced check-in and medication data.
- Mobile can show synced cloud insights (via shared `HealthInsight` type).
- Web can show weekly report summaries via `WeeklyReportCard`.
- Reports include source date ranges for debugging.

## Verify

- Confirm reports are generated from user-scoped data only (`userId` filtered).
- Confirm reports include source date ranges (`weekStart`, `weekEnd`).
- Confirm mobile and web can render report records without breaking offline flows.
- Run `npm run typecheck:web` to validate type safety.
