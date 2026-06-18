---
name: nursy-symptom-tracking
description: Build or modify Nursy AI symptom tracking, severity, duration, notes, active and resolved symptoms, symptom history, or repeated symptom detection hooks. Use when working on symptom records.
---

# Nursy Symptom Tracking

## Objective

Track symptoms over time so local rules, timeline views, sync, and reports can
detect meaningful patterns.

## Start Here

- Inspect `SymptomEntity.kt`, `HealthDao.kt`, and `LocalRulesEngine.kt`.
- Read `component` sections for Symptom Tracking, Health Timeline, and Local AI
  Health Insights.
- Check shared models before exposing symptom data to web or backend code.

## Implementation Guidance

- Store symptom name, severity from 1 to 5, duration, notes, occurrence date,
  active/resolved state, and sync metadata.
- Keep symptom names normalized enough for repeated symptom rules, while
  preserving user-facing labels.
- Support active symptoms separately from full history.
- Allow AI symptom journal flows to create draft symptom records for review.
- Avoid medical diagnosis language; frame outputs as tracking and insight data.

## Expected Deliverables

- Symptom entry and history UI.
- DAO methods for active, history, by-date, and unsynced symptom records.
- Hooks for local repeated symptom detection.
- Timeline-ready symptom event mapping.

## Verify

- Save, update, resolve, and reload symptoms offline.
- Confirm severity boundaries are enforced.
- Confirm repeated symptom rules can query enough data to work.
