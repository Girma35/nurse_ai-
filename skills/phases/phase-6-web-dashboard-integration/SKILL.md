---
name: nursy-phase-6-web-dashboard-integration
description: Execute Nursy AI Phase 6 web dashboard integration work, including fetching from backend API with graceful mock fallback, DashboardState type (loading/error/success), 7 reusable components, live/demo toggle, health score visualization, symptoms, medications, insights, weekly report, timeline, sync status, and emergency card display.
---

# Nursy Phase 6 — Web Dashboard Integration

## Objective

Turn the Next.js dashboard from a mock demo into a live visualization surface for synced health data, while keeping mock fallback for demo scenarios.

## What Was Built

### Core Dashboard (`app/page.tsx`)

Client component (`"use client"`) with:

- **State management**: `DashboardState` discriminated union (`loading | error | success`)
- **Data loading**: `useEffect` + `useCallback` pattern with `fetchDashboardData()`
- **Mode toggle**: Demo/Live button switches between mock data and live API

### API Fetch Layer (`lib/api.ts`)

```typescript
type DashboardState =
  | { status: "loading" }
  | { status: "error"; error: string }
  | { status: "success"; data: DashboardData };
```

- 6 parallel API requests with `Promise.all`
- 10-second `AbortController` timeout
- Graceful mock fallback on API failure
- Endpoints: latest-checkin, symptoms, medications, insights, sync summary, weekly report

### Components (7 reusable)

| Component | File | Purpose |
|---|---|---|
| `HealthScore` | `components/HealthScore.tsx` | Conic gradient score ring (CSS `conic-gradient`), score label, description |
| `SyncStatus` | `components/SyncStatus.tsx` | 4-panel grid (Room DB, Sync worker, Cloud, Network), pending count, last synced time |
| `DashboardSkeleton` | `components/DashboardSkeleton.tsx` | Animated loading skeleton matching dashboard layout |
| `EmptyState` | `components/EmptyState.tsx` | Reusable empty state with icon, title, description |
| `ErrorState` | `components/ErrorState.tsx` | Error display with AlertTriangle icon and retry button |
| `InsightCarousel` | `components/InsightCarousel.tsx` | Health insight cards with severity-colored borders (coral/amber/mint) |
| `WeeklyReportCard` | `components/WeeklyReportCard.tsx` | Score, adherence trend arrow, symptom summary, highlights, recommendations |

### Demo Data

- `lib/mock-data.ts` — basic demo (1 check-in, 2 symptoms, 2 medications, 1 emergency contact)
- `lib/enhanced-mock-data.ts` — rich demo (7-day check-ins, 5 symptoms, dose events, profile, 3 insights, weekly report)

### Styling

- TailwindCSS with custom palette (`ink`, `moss`, `mint`, `coral`, `amber`, `cloud`)
- `boxShadow.soft` for elevated cards
- Background gradient: `linear-gradient(180deg, rgba(123, 217, 162, 0.18), rgba(247, 250, 247, 0) 320px)`

## Key Files

- `apps/web/src/app/page.tsx`
- `apps/web/src/components/*.tsx` (7 components)
- `apps/web/src/lib/api.ts`
- `apps/web/src/lib/mock-data.ts` and `enhanced-mock-data.ts`
- `apps/web/tailwind.config.ts`
- `apps/web/package.json` (dependencies: next, react, lucide-react, @nursy/shared, clsx)
- `apps/web/next.config.ts` (transpilePackages for @nursy/shared)

## Guardrails

- Keep web as a secondary dashboard, not the full product.
- Do not add mobile-only editing workflows to web unless explicitly requested.
- Keep Vercel build compatibility intact — `next.config.ts` handles workspace transpilation.
- Use shared types and helpers (`@nursy/shared`) instead of duplicating contracts.

## Exit Criteria

- Vercel dashboard displays real synced health data (or mock fallback).
- Dashboard remains focused on visualization and demo evaluation.
- Loading, empty, and error states render correctly for all data sections.
- `npm run typecheck:web` passes with 0 errors.

## Verify

- Run `npm run typecheck:web` — must pass.
- Run `npm run build:web` — must succeed for Vercel deployment.
- Confirm dashboard renders with mock data, empty data, and error state.
- Confirm Demo/Live toggle switches data sources.
- Confirm all responsive breakpoints.
