---
name: nursy-phase-8-polish-testing-demo-readiness
description: Execute Nursy AI Phase 8 polish, testing, and demo readiness work, including mobile tests, web tests, Room tests, rules tests, parser tests, sync mapping tests, empty and error states, demo seed data, Vercel build, and Android run validation.
---

# Nursy Phase 8 Polish Testing Demo Readiness

## Objective

Stabilize the hackathon build and make the demo clearly show offline tracking,
sync, dashboard visualization, and credible health insights.

## Start Here

- Read `component` Phase 8.
- Inspect test setup for mobile and web.
- Inspect README run instructions and Vercel configuration.
- Use relevant component skills based on the area being polished.

## Work Sequence

1. Add focused mobile tests for Room, local rules, parser, and sync mapping.
2. Add web tests or type checks for dashboard data states.
3. Improve empty and error states.
4. Prepare demo seed data.
5. Confirm Vercel build.
6. Confirm Android run path from Android Studio or Gradle.
7. Update README only if run steps changed.

## Guardrails

- Focus polish on demo-critical paths.
- Avoid late broad refactors unless required for correctness.
- Keep seed data realistic and non-sensitive.
- Preserve offline-first behavior while polishing cloud and web flows.

## Exit Criteria

- `npm run build:web` passes.
- Mobile app runs from Android Studio.
- Demo data clearly shows offline tracking, sync, dashboard visualization, and
  health insights.

## Verify

- Run `npm run typecheck:web`.
- Run `npm run build:web`.
- Run available mobile Gradle tests or compile tasks.
- Manually walk through the demo script from offline mobile entry to synced web
  dashboard display.
