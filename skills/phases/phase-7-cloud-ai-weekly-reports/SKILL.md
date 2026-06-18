---
name: nursy-phase-7-cloud-ai-weekly-reports
description: Execute Nursy AI Phase 7 cloud AI and weekly reports work, including symptom trend analysis, health deterioration patterns, medication effectiveness, weekly report generation, cloud insight storage, and mobile or web report display.
---

# Nursy Phase 7 Cloud AI Weekly Reports

## Objective

Add advanced cloud intelligence only after offline data capture and sync are
stable.

## Start Here

- Read `component` Phase 7.
- Inspect synced record shapes and backend query paths.
- Inspect local insight model to keep cloud insight display consistent.
- Use the component skills for Cloud Backend, Shared Models, Web Dashboard, and
  Local AI Health Insights.

## Work Sequence

1. Define cloud insight and weekly report record types.
2. Analyze synced symptoms, check-ins, and medication events for trends.
3. Detect deterioration-style patterns conservatively.
4. Estimate medication effectiveness from symptom and adherence patterns.
5. Generate weekly report summaries.
6. Surface cloud-generated insights in mobile and web.

## Guardrails

- Do not block core mobile value on cloud AI.
- Label cloud outputs as insights or reports, not diagnosis.
- Store generated results with timestamps and source ranges.
- Keep report generation reproducible enough to debug demo data.

## Exit Criteria

- Cloud insights are stored as health records or report records.
- Mobile can show synced cloud insights.
- Web can show weekly report summaries.

## Verify

- Confirm reports are generated from user-scoped data only.
- Confirm reports include source date ranges.
- Confirm mobile and web can render report records without breaking offline
  flows.
