---
name: nursy-web-dashboard
description: Build or modify the Nursy AI Next.js web dashboard, Vercel demo views, health score visualization, trend summaries, synced health data display, loading states, empty states, or backend fetch integration. Use when working on the secondary web app.
---

# Nursy Web Dashboard

## Objective

Build the web app as a secondary visualization dashboard for demos, judge
evaluation, and synced data inspection.

## Start Here

- Inspect `apps/web/src/app/page.tsx`.
- Inspect `apps/web/src/components` and `apps/web/src/lib/mock-data.ts`.
- Inspect `packages/shared/src/index.ts` before duplicating scoring or types.
- Read `component` sections for Web Dashboard and Phase 6.

## Implementation Guidance

- Keep the web dashboard focused on visualization, not full mobile feature
  replacement.
- Show health score, trends, check-ins, symptoms, medications, and sync status.
- Replace mock data through a fetch layer once backend APIs exist.
- Include loading, empty, and error states.
- Match the existing Next.js, React, TailwindCSS, and workspace patterns.
- Keep demo data useful until live backend data is ready.

## Expected Deliverables

- Dashboard components and data mapping.
- Backend fetch layer or mock fallback.
- Loading, empty, and error states.
- Shared model usage where practical.

## Verify

- Run `npm run typecheck:web`.
- Run `npm run build:web` before demo handoff.
- Confirm dashboard renders with mock data and with empty data.
