---
name: nursy-health-timeline
description: Build or modify the Nursy AI mobile health timeline, chronological event history, date grouping, filters, check-in events, symptom events, medication events, or insight events. Use when working on Android history views.
---

# Nursy Health Timeline

## Objective

Present local health history as a chronological, filterable record built from Room check-ins, symptoms, medication dose events, and insights.

## Mobile Timeline (`HealthTimelineScreen.kt`)

```text
LazyColumn
├─ Header: Timeline + Your health history
├─ Date group headers
│  └─ TimelineEventCard: time + colored dot + label + detail
│     ├─ checkin
│     ├─ symptom
│     ├─ dose
│     └─ insight
└─ Empty state
```

## Timeline Event Model

```kotlin
data class TimelineEvent(
    val id: String,
    val date: String,
    val time: String,
    val type: String,
    val label: String,
    val detail: String
)
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/HealthTimelineScreen.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- Inspect Room entities under `apps/mobile/app/src/main/java/com/nursyai/data/local/entity`
- Inspect timeline source queries in `HealthDao.kt`

## Implementation Guidance

- Build display events from source records instead of storing duplicate timeline rows.
- Sort newest first by date and time.
- Preserve source ids for future navigation back to details.
- Keep timeline fully offline.
- Add filters only when they reduce real user friction.

## Expected Deliverables

- `HealthTimelineScreen.kt` with date grouping, event cards, colored dots, and empty state.
- Kotlin event construction from local Room records.

## Verify

- Confirm mixed events sort correctly across dates.
- Confirm edits to source records are reflected on next render.
- Confirm empty and single-day histories render cleanly.
