---
name: nursy-local-ai-health-insights
description: Build or modify Nursy AI local health insights, on-device rules, repeated symptom detection, sleep and fatigue warnings, medication reminder insights, missed check-in rules, or LocalRulesEngine behavior. Use when adding offline intelligence.
---

# Nursy Local AI Health Insights

## Objective

Generate practical offline insights from local data without depending on cloud AI.

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`.
- Inspect Room entities and DAO queries that provide check-ins, symptoms, and
  medication events.
- Read `component` sections for Local AI Health Insights, AI Symptom Journal,
  and Health Dashboard.

## Implementation Guidance

- Keep rules deterministic, explainable, and testable.
- Implement repeated symptom detection, sleep plus fatigue warnings, missed dose
  patterns, and missed check-in reminders.
- Return structured insight objects with id, severity, title, message, source
  record ids, and timestamps when possible.
- Avoid medical diagnosis and emergency triage claims.
- Prefer simple thresholds that can be explained in code comments or tests.

## Expected Deliverables

- Expanded local rules engine.
- Insight model.
- Dashboard integration.
- Focused unit tests for each rule.

## Verify

- Test each rule with positive, negative, and boundary inputs.
- Confirm insights render offline.
- Confirm insight messages are health-tracking guidance, not diagnosis.
