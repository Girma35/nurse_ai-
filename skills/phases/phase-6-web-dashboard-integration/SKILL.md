---
name: nursy-phase-6-web-dashboard-integration
description: Execute Nursy AI Phase 6 web dashboard integration work, including replacing mock data, backend fetch layer, loading states, empty states, error states, health score visualization, symptoms, medications, check-ins, and sync status on Vercel.
---

# Nursy Phase 6 Web Dashboard Integration

## Objective

Turn the Next.js dashboard from a mock demo into a live visualization surface for
synced health data.

## Start Here

- Read `component` Phase 6.
- Inspect `apps/web/src/app/page.tsx`, `apps/web/src/components`, and
  `apps/web/src/lib/mock-data.ts`.
- Inspect `packages/shared/src/index.ts`.
- Use the component skills for Web Dashboard, Cloud Backend, and Shared Models.

## Work Sequence

1. Add a backend fetch layer for user-scoped dashboard data.
2. Replace mock data where live APIs are available.
3. Keep a demo fallback only when explicitly useful for hackathon presentation.
4. Add loading, empty, and error states.
5. Visualize check-ins, symptoms, medications, health score, and sync status.

## Guardrails

- Keep web as a secondary dashboard, not the full product.
- Do not add mobile-only editing workflows to web unless explicitly requested.
- Keep Vercel build compatibility intact.
- Use shared types and helpers instead of duplicating contracts.

## Exit Criteria

- Vercel dashboard displays real synced health data.
- Dashboard remains focused on visualization and demo evaluation.

## Verify

- Run `npm run typecheck:web`.
- Run `npm run build:web`.
- Confirm loading, empty, error, mock, and live-data states render correctly.
