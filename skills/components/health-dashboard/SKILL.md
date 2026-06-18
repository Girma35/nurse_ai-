---
name: nursy-health-dashboard
description: Build or modify the Nursy AI health dashboard, health score ring, today's summary, active symptoms, medication status, quick insights, or sync status display. Use when working on the main summary view on mobile and web.
---

# Nursy Health Dashboard

## Objective

Show a compact, useful summary of the user's current health state from local data first, then include synced or cloud-generated data when available. The dashboard exists in two forms: a **Compose screen** (`DashboardScreen.kt`) for mobile and a **Next.js page** (`page.tsx`) for web.

## Mobile Dashboard (`DashboardScreen.kt`)

```
LazyColumn
├─ Header: "Nursy AI" + "Today"
├─ HealthScoreCard: score ring + label + insight
├─ QuickMetricRow: Sleep, Water, Energy
├─ Insights list
├─ Active Symptoms list
├─ Active Medications list
└─ EmptyDashboardCard (when no data)
```

### Health Score Calculation (local, in-composable)

```kotlin
val sleepScore   = min(sleepHours / 8.0, 1.0) * 20    // max 20
val energyScore  = (energyLevel / 10.0) * 20            // max 20
val stressScore  = max(0, (5 - stressLevel) / 5.0) * 15 // max 15
val hydration    = min(waterIntakeMl / 2000.0, 1.0) * 15  // max 15
val penalty      = symptoms.sumBy { it.severity * 3 }  // max penalty ~75
val totalScore   = clamp(sleepScore + energyScore + stressScore + hydration - penalty, 0, 100)
```

### Quick Metric Cards

- **Sleep**: `${sleepHours} h`
- **Water**: `${waterIntakeMl} ml`
- **Energy**: `${energyLevel}/10`

## Web Dashboard (`app/page.tsx`)

The web dashboard is a Next.js client component (`"use client"`) that:

- Renders a **HealthScore** component with conic gradient ring
- Shows **quick metric cards** (Water, Adherence, Sleep)
- Shows **InsightCarousel** with health insights from `HealthInsight[]`
- Shows **WeeklyReportCard** for weekly report visualization
- Shows **active symptoms** and **timeline events**
- Shows **SyncStatus** with pending count and last synced time
- Has a **Demo/Live toggle** button to switch between mock data and live API
- Uses `fetchDashboardData()` from `lib/api.ts` with graceful mock fallback
- Has loading (`DashboardSkeleton`), empty (`EmptyState`), and error (`ErrorState`) states

## Start Here

- Mobile: Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DashboardScreen.kt`
- Web: Inspect `apps/web/src/app/page.tsx` (main dashboard)
- Web components: Inspect `apps/web/src/components/HealthScore.tsx`, `apps/web/src/components/SyncStatus.tsx`, `apps/web/src/components/InsightCarousel.tsx`, `apps/web/src/components/WeeklyReportCard.tsx`
- Web states: Inspect `apps/web/src/components/DashboardSkeleton.tsx`, `apps/web/src/components/EmptyState.tsx`, `apps/web/src/components/ErrorState.tsx`
- API layer: Inspect `apps/web/src/lib/api.ts` (fetch with mock fallback)
- Scoring: Inspect `packages/shared/src/index.ts` — `calculateHealthScore()`, `calculateAdherenceScore()`, `getHealthScoreLabel()`

## Implementation Guidance

- **Mobile**: Compute the dashboard from local Room data via `NursyViewModel`. All flows are `StateFlow` collected with `collectAsState()`.
- **Web**: Dashboard is a client component that calls `fetchDashboardData()`. Uses `useEffect` + `useCallback` for data loading. Falls back to mock data if the API is unavailable.
- **Empty states**: Both mobile and web have empty states for first-run with no records. Mobile has `EmptyDashboardCard`; web uses `EmptyState` component.
- **Insights**: Mobile shows insights from `LocalRulesEngine`. Web shows cloud-generated `HealthInsight[]` via `InsightCarousel`.
- **Score label**: `getHealthScoreLabel()`: >=80 "Good", >=60 "Fair", >=40 "Concerning", else "Needs attention".

## Expected Deliverables

- `DashboardScreen.kt` with score ring, metrics, insights, symptoms, medications, empty state.
- `HealthScore.tsx` with conic gradient score ring.
- `SyncStatus.tsx` with pending count and last synced time.
- `DashboardSkeleton.tsx`, `EmptyState.tsx`, `ErrorState.tsx` for loading/empty/error states.
- `InsightCarousel.tsx` and `WeeklyReportCard.tsx` for rich data visualization.
- `lib/api.ts` with `fetchDashboardData()` and typed `DashboardState`.

## Verify

- Confirm dashboard renders with no records, partial records, and rich demo data.
- Confirm dashboard works offline (mobile).
- Confirm score and summaries are deterministic for the same local data.
- Run `npm run typecheck:web` for web dashboard type safety.
- Run `npm run build:web` before demo handoff.
