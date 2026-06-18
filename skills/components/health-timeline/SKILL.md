---
name: nursy-health-timeline
description: Build or modify the Nursy AI health timeline, chronological event history, date grouping, filters, check-in events, symptom events, medication events, or insight events. Use when working on history views.
---

# Nursy Health Timeline

## Objective

Present health history as a chronological, filterable record built from local check-ins, symptoms, medication events, and insights. The timeline exists in two forms: a **Compose screen** (`HealthTimelineScreen.kt`) for mobile and a **shared TypeScript helper** (`buildTimelineEvents()` in `@nursy/shared`) for the web dashboard.

## Mobile Timeline (`HealthTimelineScreen.kt`)

```
LazyColumn
├─ Header: "Timeline" + "Your health history"
├─ Date group headers ("Jun 15, 2026")
│   └─ TimelineEventCard: time + colored dot + label + detail
│       ├─ "checkin" → mint dot
│       ├─ "symptom" → amber dot
│       ├─ "medication" / "dose" → moss dot
│       └─ "insight" → coral dot
└─ Empty state (no events)
```

### TimelineEvent data class (Kotlin, local to screen)

```kotlin
data class TimelineEvent(
    val id: String,
    val date: String,       // "yyyy-MM-dd"
    val time: String,       // "HH:mm"
    val type: String,       // "checkin" | "symptom" | "dose" | "insight"
    val label: String,
    val detail: String
)
```

### buildTimelineEvents() — Kotlin version (local to HealthTimelineScreen)

- Iterates all check-ins → creates events with mood, energy, sleep details
- Iterates all symptoms → creates events with severity, duration
- Iterates all dose events → creates events with medication name and taken/missed status
- Sorts by `date DESC, time DESC` using `compareByDescending`

## Shared TypeScript Timeline (`packages/shared/src/index.ts`)

```typescript
export type TimelineEvent = {
  id, date, time,
  type: "checkin" | "symptom" | "medication" | "dose" | "insight" | "reminder",
  label, detail, sourceRecordId
};

export function buildTimelineEvents(
  checkIns: DailyCheckIn[],
  symptoms: SymptomLog[],
  medications: Medication[],
  doseEvents: MedicationDoseEvent[]
): TimelineEvent[];
```

The TypeScript version creates events for check-ins, symptoms, and dose events, then sorts by `date DESC, time DESC`. The web dashboard uses this to display the latest 4 events.

## Start Here

- Mobile: Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/HealthTimelineScreen.kt`
- Shared: Inspect `packages/shared/src/index.ts` — `buildTimelineEvents()`, `TimelineEvent` type
- Web: Inspect `apps/web/src/app/page.tsx` — uses `buildTimelineEvents()` and displays top 4 events
- Room: Inspect all entity files and DAO queries that provide source data

## Implementation Guidance

- **Event construction**: Build timeline events from source records instead of storing duplicate display rows. Source records are `DailyCheckInEntity`, `SymptomEntity`, `MedicationDoseEventEntity`.
- **Date grouping**: Group events by local date and sort newest first by default. Each date gets a header "MMM dd, yyyy".
- **Source IDs**: Preserve source record ids so users can navigate back to details (future feature).
- **Offline**: Keep timeline useful offline — all source data comes from Room.
- **Filters**: Not yet implemented, but the type system supports filtering by `type` field.

## Expected Deliverables

- `HealthTimelineScreen.kt` with date grouping, event cards, colored dots, empty state.
- `TimelineEvent` data class (Kotlin) and `buildTimelineEvents()` (Kotlin).
- `TimelineEvent` type (TypeScript) and `buildTimelineEvents()` (TypeScript) in shared models.
- Web dashboard integration showing latest events.

## Verify

- Confirm mixed events (check-in, symptom, dose) sort correctly across dates.
- Confirm edits to source records are reflected in the timeline on next render.
- Confirm empty and single-day histories render cleanly.
- Run `npm run typecheck:web` after shared model timeline changes.
