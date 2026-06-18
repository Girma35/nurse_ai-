---
name: nursy-shared-models
description: Build or modify Nursy AI shared TypeScript health models, scoring helpers, sync record types, constants, dashboard data contracts, or cross-platform model alignment. Use when coordinating data shapes across mobile, backend, and web.
---

# Nursy Shared Models

## Objective

Keep web, backend, and sync contracts aligned through shared TypeScript models and deterministic helper functions. Located in `packages/shared/src/index.ts`.

## Package Structure

```
packages/shared/
├── src/
│   └── index.ts              ← All types, helpers, and constants
├── package.json              ← @nursy/shared, exports "./src/index.ts"
├── tsconfig.json             ← strict, ES2022, bundler resolution
```

## Exported Types (17 types)

### Core Health Records
```typescript
export type MoodLevel = "low" | "down" | "steady" | "good" | "great";
export type SleepQuality = "poor" | "fair" | "good" | "excellent";
export type DoseStatus = "taken" | "missed" | "skipped";
export type SyncState = "local" | "queued" | "synced" | "conflict";
export type EntityType = "checkIn" | "symptom" | "medication" | "doseEvent" | "profile" | "emergencyContact" | "insight";

export type DailyCheckIn = { id, userId, date, mood, energyLevel, sleepHours, sleepQuality, stressLevel, waterIntakeMl, notes? };
export type SymptomLog = { id, userId, name, severity: 1|2|3|4|5, startedAt, durationHours?, active?, notes? };
export type Medication = { id, userId, name, dose, frequency, scheduledTimes: string[], takenCount, missedCount, active };
export type MedicationDoseEvent = { id, userId, medicationId, scheduledTime, takenAt?, status: DoseStatus };
```

### Profile & Emergency
```typescript
export type UserProfile = { id, userId, fullName, dateOfBirth, gender, weightKg|null, heightCm|null, bloodType, allergies: string[], chronicConditions: string[] };
export type EmergencyContact = { id, userId, name, relationship, phoneNumber };
export type EmergencyHealthCard = { userId, fullName, bloodType, allergies: string[], conditions: string[], emergencyContacts: EmergencyContact[] };
```

### Insights & Reports
```typescript
export type HealthInsight = { id, userId, title, message, severity: "info"|"warning"|"alert", sourceRecordIds: string[], createdAt };
export type WeeklyReport = { id, userId, weekStart, weekEnd, averageHealthScore, adherenceRate, symptomSummary, highlights: string[], recommendations: string[], generatedAt };
```

### Sync & Timeline
```typescript
export type SyncRecord<T> = { id, userId, entityType, payload: T, updatedAt, syncState };
export type SyncSummary = { pendingCheckIns, pendingSymptoms, pendingMedications, lastSyncedAt|null };
export type TimelineEvent = { id, date, time, type: "checkin"|"symptom"|"medication"|"dose"|"insight"|"reminder", label, detail, sourceRecordId };
```

## Exported Functions (6 helpers)

| Function | Purpose |
|---|---|
| `calculateAdherenceScore(medications)` | `takenCount / (takenCount + missedCount) * 100` |
| `calculateHealthScore(checkIn, symptoms, medications)` | Combines sleep, energy, stress, hydration, adherence, minus symptom penalty |
| `calculateHealthScoreFromFields(energy, sleep, stress, water, symptomSum, adherence)` | Standalone score calculation from raw fields |
| `formatSeverityLabel(severity)` | Maps 1→"Very mild" through 5→"Severe" |
| `getHealthScoreLabel(score)` | Maps 0-100 to "Needs attention" through "Good" |
| `buildTimelineEvents(checkIns, symptoms, medications, doseEvents)` | Assembles mixed chronological timeline events |

## Start Here

- Inspect `packages/shared/src/index.ts` — all type definitions and functions
- Inspect `packages/shared/package.json` — workspace configuration, exports field
- Inspect `packages/shared/tsconfig.json` — strict mode, ES2022 target
- Inspect mobile Room entities before designing shared record shapes
- Inspect web dashboard imports to avoid breaking workspace consumers
- Read component skills for Offline Storage, Sync Engine, and Web Dashboard

## Implementation Guidance

- **Field alignment**: TypeScript types align with mobile entity field meanings, not exact persistence details. For example, `scheduledTimes: string[]` in TS maps to `scheduledTimesCsv: String` in Room.
- **No Android concepts**: Avoid importing Android-specific concepts into shared TypeScript.
- **Score determinism**: All scoring helpers are pure functions — same inputs always produce same outputs.
- **Versioning**: Sync fields (`SyncRecord`, `SyncPayload`) should be versioned when changing cross-system contracts.
- **Web consumption**: The web dashboard imports from `@nursy/shared` via npm workspace. `next.config.ts` transpiles the package.

## Expected Deliverables

- 17 exported types covering all health records, sync contracts, and dashboard shapes.
- 6 helper functions for health score, adherence, severity labels, timeline building.
- Workspace-configured `package.json` with `exports` and `typecheck` script.

## Verify

- Run `npm run typecheck --workspace @nursy/shared` — must pass with 0 errors.
- Run `npm run typecheck:web` — must pass after shared model changes.
- Confirm mobile sync mapping (`RecordMapper.kt`) can produce the shared cloud shapes.
- Confirm functions are pure and deterministic.
