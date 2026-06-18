---
name: nursy-symptom-tracking
description: Build or modify Nursy AI symptom tracking, severity, duration, notes, active and resolved symptoms, symptom history, or repeated symptom detection hooks. Use when working on symptom records.
---

# Nursy Symptom Tracking

## Objective

Track symptoms over time so local rules, timeline views, sync, and reports can detect meaningful patterns. Symptoms have severity (1-5), duration, active/resolved state, and sync metadata.

## Data Model

### SymptomEntity (Room entity, table `symptoms`)

```kotlin
@Entity(tableName = "symptoms")
data class SymptomEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val severity: Int,          // 1-5
    val startedAt: Long,        // epoch millis
    val durationHours: Int? = null,
    val notes: String? = null,
    val active: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### Shared TypeScript type

```typescript
export type SymptomLog = { id, userId, name, severity: 1 | 2 | 3 | 4 | 5, startedAt, durationHours?, active?, notes? };
export function formatSeverityLabel(severity): "Very mild" | "Mild" | "Moderate" | "High" | "Severe";
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/SymptomEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — `observeSymptoms()`, `observeActiveSymptoms()`, `getSymptomById()`, `getAllSymptoms()`, `getActiveSymptoms()`, `upsertSymptom()`, `resolveSymptom()`, `pendingSymptoms()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomTrackingScreen.kt` — add symptom form + symptom history list with resolve button
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomJournalScreen.kt` — NL parser creates symptom entries from journal text
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt` — `parseJournalNote()` extracts structured symptoms from natural language
- Inspect `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt` — parser unit tests
- Check `packages/shared/src/index.ts` before exposing symptom data to web or backend code

## Implementation Guidance

- **Severity**: 1-5 scale. The `formatSeverityLabel()` helper maps: 1→Very mild, 2→Mild, 3→Moderate, 4→High, 5→Severe.
- **Active state**: `active: Boolean = true`. The `resolveSymptom()` DAO method sets `active = 0`. Active symptoms appear in the dashboard and rules engine; resolved symptoms remain in history.
- **Journal integration**: The AI Symptom Journal (`SymptomJournalScreen.kt`) calls `NursyViewModel.parseJournalNote()` which delegates to `LocalRulesEngine.parseJournalNote()`. Extracted `ParsedSymptom` objects include name, severity, and duration. Users review and save — the ViewModel's `saveParsedSymptoms()` creates `SymptomEntity` records.
- **Repeated symptom detection**: The `LocalRulesEngine` groups active symptoms by name. When 2+ entries share a name, a "Repeated symptom" insight is generated. At 4+, severity escalates to ALERT.
- **Sync**: Pending symptoms are uploaded via `HealthSyncWorker` using `RecordMapper.symptomToPayload()`.

## Expected Deliverables

- `SymptomEntity` with all fields.
- DAO: observe active/full, get by id, upsert, resolve, pending, mark synced.
- `SymptomTrackingScreen.kt` — add symptom form + history list + resolve action.
- `SymptomJournalScreen.kt` — NL-to-symptom flow with review-and-save.
- `LocalRulesEngine.parseJournalNote()` returning `List<ParsedSymptom>`.
- Shared `SymptomLog` type and `formatSeverityLabel()` helper.

## Verify

- Save, update, resolve, and reload symptoms offline.
- Confirm severity ≥ 5 and ≤ 1 boundaries are enforced (clamp in UI).
- Confirm repeated symptom rules in `LocalRulesEngine` can query active symptoms.
- Confirm journal-parsed symptoms appear in symptom history after save.
- Confirm `npm run typecheck:web` passes after shared model changes.
