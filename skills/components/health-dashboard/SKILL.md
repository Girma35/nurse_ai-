---
name: nursy-health-dashboard
description: Build or modify the Nursy AI health dashboard, health score, today's summary, active symptoms, medication status, quick insights, or sync status display. Use when working on the main summary view.
---

# Nursy Health Dashboard

## Objective

Show a compact, useful summary of the user's current health state from local
data first, then include synced or cloud-generated data when available.

## Start Here

- Inspect mobile dashboard code in `MainActivity.kt` or related Compose files.
- Inspect `packages/shared/src/index.ts` for score calculations used by the web
  dashboard.
- Inspect `apps/web/src/components/HealthScore.tsx` only when aligning visual or
  scoring semantics.
- Read `component` sections for Health Dashboard, Local AI Health Insights, and
  Sync Engine.

## Implementation Guidance

- Compute the dashboard from local Room data.
- Show health score, today's check-in, active symptoms, medication status,
  quick insights, and sync status.
- Keep summary components small and stable so they can update independently.
- Use local insights before cloud insights.
- Make empty states useful without explaining the app's feature list.

## Expected Deliverables

- Dashboard screen or component updates.
- Health score mapping.
- Insight list rendering.
- Medication and symptom summaries.
- Sync status indicator.

## Verify

- Confirm dashboard renders with no records, partial records, and rich demo data.
- Confirm dashboard works offline.
- Confirm score and summaries are deterministic for the same local data.
