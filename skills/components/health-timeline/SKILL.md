---
name: nursy-health-timeline
description: Build or modify the Nursy AI health timeline, chronological event history, date grouping, filters, check-in events, symptom events, medication events, or insight events. Use when working on history views.
---

# Nursy Health Timeline

## Objective

Present health history as a chronological, filterable record built from local
check-ins, symptoms, medication events, and insights.

## Start Here

- Inspect Room entities and DAO methods in `apps/mobile/app/src/main/java/com/nursyai/data/local`.
- Read `component` sections for Daily Health Check-In, Symptom Tracking,
  Medication Management, and Health Timeline.
- Check shared models before exposing timeline data to the web dashboard.

## Implementation Guidance

- Build timeline events from source records instead of storing duplicate display
  rows unless performance requires it.
- Group events by local date and sort newest first by default.
- Preserve source record ids so users can navigate back to details.
- Add simple filters only when the underlying data model supports them cleanly.
- Keep timeline useful offline and independent of cloud report generation.

## Expected Deliverables

- Timeline event model.
- DAO or repository queries that can assemble event history.
- Timeline screen with date grouping.
- Optional filters for check-ins, symptoms, medications, and insights.

## Verify

- Confirm mixed events sort correctly across dates.
- Confirm edits to source records are reflected in the timeline.
- Confirm empty and single-day histories render cleanly.
