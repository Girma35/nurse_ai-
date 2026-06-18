---
name: nursy-web-dashboard
description: Build or modify the Nursy AI Next.js web dashboard, Vercel demo views, health score visualization, trend summaries, synced health data display, loading/empty/error states, backend fetch integration, or demo/live mode toggle. Use when working on the secondary web app.
---

# Nursy Web Dashboard

## Objective

Build the web app as a secondary visualization dashboard for demos, judge evaluation, and synced data inspection. Built with Next.js 15, React 19, TypeScript, and TailwindCSS.

## Architecture

```
apps/web/
├── src/
│   ├── app/
│   │   ├── page.tsx              ← Main dashboard ("use client" with data fetching)
│   │   ├── layout.tsx            ← Root layout with metadata
│   │   └── globals.css           ← Tailwind + custom styles
│   ├── components/
│   │   ├── HealthScore.tsx       ← Conic gradient score ring
│   │   ├── SyncStatus.tsx        ← Sync state cards
│   │   ├── DashboardSkeleton.tsx ← Loading state skeleton
│   │   ├── EmptyState.tsx        ← Reusable empty state
│   │   ├── ErrorState.tsx        ← Error state with retry
│   │   ├── InsightCarousel.tsx   ← Health insight cards
│   │   └── WeeklyReportCard.tsx  ← Weekly report visualization
│   └── lib/
│       ├── api.ts                ← Backend fetch layer with mock fallback
│       ├── mock-data.ts          ← Basic demo data
│       ├── enhanced-mock-data.ts ← Rich demo data (7-day history)
│       └── cloud-ai-reports.ts   ← Server-side report generation logic
├── package.json
├── next.config.ts
├── tailwind.config.ts            ← Custom colors (ink, moss, mint, coral, amber, cloud)
├── tsconfig.json
└── postcss.config.js
```

## Dashboard Sections (`page.tsx`)

| Section | Content | Component |
|---|---|---|
| Header | Nursy AI branding + Demo/Live toggle + action buttons | inline |
| Health Score | Conic gradient ring + score label + description | `HealthScore.tsx` |
| Quick Metrics | Water, Adherence, Sleep cards | `MetricCard` inline |
| Insights | Cloud-generated health insight cards | `InsightCarousel.tsx` |
| Weekly Report | Avg score, adherence, highlights, recommendations | `WeeklyReportCard.tsx` |
| Symptoms | Active symptoms with severity badges | inline |
| Timeline | Last 4 chronological health events | `buildTimelineEvents()` from shared |
| Sync Status | Room DB, sync worker, cloud, network state | `SyncStatus.tsx` |
| Emergency Card | Blood type, allergies, conditions, contacts | inline |

## Data Fetching (`api.ts`)

```typescript
type DashboardState =
  | { status: "loading" }
  | { status: "error"; error: string }
  | { status: "success"; data: DashboardData };

async function fetchDashboardData(): Promise<DashboardState>
```

- Uses `fetch` with 10-second timeout and `AbortController`
- Falls back to mock data if API unavailable
- 6 parallel requests: check-in, symptoms, medications, insights, sync summary
- Demo/Live toggle in header switches between mock and API data

## Theming (`tailwind.config.ts`)

```typescript
colors: {
  ink: "#16211c",
  moss: "#285947",
  mint: "#7bd9a2",
  coral: "#ef7b63",
  amber: "#f4b860",
  cloud: "#eef3ef"
}
```

## Start Here

- Inspect `apps/web/src/app/page.tsx` — main dashboard with data fetching and rendering
- Inspect `apps/web/src/components/` — all reusable components
- Inspect `apps/web/src/lib/api.ts` — fetch layer with `DashboardState` type
- Inspect `apps/web/src/lib/mock-data.ts` and `apps/web/src/lib/enhanced-mock-data.ts` — demo data
- Inspect `packages/shared/src/index.ts` — scoring helpers and types
- Inspect `apps/web/tailwind.config.ts` — color theme
- Read component skills for Web Dashboard and Phase 6

## Implementation Guidance

- **Visualization focus**: Web is a secondary dashboard, not a full product replacement. No mobile-style editing workflows.
- **Mock fallback**: `fetchDashboardData()` catches API errors and returns mock data. This ensures the dashboard is always renderable.
- **Three states**: Every data section handles loading (`DashboardSkeleton`), empty (`EmptyState`), and error (`ErrorState`).
- **Shared models**: Uses `@nursy/shared` for scoring (`calculateHealthScore`, `calculateAdherenceScore`, `formatSeverityLabel`, `buildTimelineEvents`, `getHealthScoreLabel`).
- **Vercel ready**: `next.config.ts` includes `transpilePackages: ["@nursy/shared"]` for workspace package support.

## Expected Deliverables

- `page.tsx` with all dashboard sections, data fetching, and state management.
- 7 reusable components: `HealthScore`, `SyncStatus`, `DashboardSkeleton`, `EmptyState`, `ErrorState`, `InsightCarousel`, `WeeklyReportCard`.
- `api.ts` — typed fetch layer with mock fallback.
- `enhanced-mock-data.ts` — rich demo seed data with 7-day history.
- TailwindCSS configuration with Nursy palette.

## Verify

- Run `npm run typecheck:web` — must pass with 0 errors.
- Run `npm run build:web` — must succeed for Vercel deployment.
- Confirm dashboard renders with mock data, with empty data, and with error state.
- Confirm Demo/Live toggle switches between mock and API data sources.
- Confirm all responsive breakpoints render correctly (mobile → desktop).
