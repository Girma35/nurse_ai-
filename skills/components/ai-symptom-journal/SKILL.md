---
name: nursy-ai-symptom-journal
description: Build or modify the Nursy AI symptom journal, natural-language symptom parsing, structured symptom extraction with severity and duration, or review-and-save flow. Use when converting health notes into records.
---

# Nursy AI Symptom Journal

## Objective

Convert natural-language health notes into structured symptom records while keeping the user in control before saving. The parser runs **locally and deterministically** — no cloud AI required.

## Architecture

```
User types: "I feel tired and have headache for 3 days"
         ↓
SymptomJournalScreen.kt
  → NursyViewModel.parseJournalNote()
    → LocalRulesEngine.parseJournalNote()
      → ParsedSymptom(name="Fatigue", severity=2, durationHours=null)
      → ParsedSymptom(name="Headache", severity=3, durationHours=72)
         ↓
Review step: User sees extracted symptoms with severities
         ↓
User taps "Save All to Symptoms"
  → NursyViewModel.saveParsedSymptoms()
    → Creates SymptomEntity records in Room
```

## ParsedSymptom Model

```kotlin
data class ParsedSymptom(
    val name: String,
    val severity: Int = 2,      // 1-5
    val durationHours: Int? = null
)
```

## Parser Capabilities (`LocalRulesEngine.parseJournalNote()`)

### Symptom Keywords (30+)

| Keyword | Default Severity | Keyword | Default Severity |
|---|---|---|---|
| headache | 3 | fever | 4 |
| fatigue | 2 | nausea | 3 |
| cough | 2 | sore throat | 2 |
| chest pain | 5 | shortness of breath | 5 |
| dizziness | 3 | body ache | 3 |
| rash | 2 | cramps | 3 |
| diarrhea | 3 | constipation | 2 |
| insomnia | 3 | anxiety | 3 |
| back pain | 3 | joint pain | 3 |
| ... and more | | | |

### Severity Modifier Extraction

Detects words before symptom: `severe`→5, `quite`/`pretty`→4, `moderate`→3, `mild`/`slight`→2, `minor`→1

### Duration Extraction

- "for N days" → `N * 24` hours
- "for N hours" → `N` hours
- "N days of [symptom]" → `N * 24` hours
- "since yesterday" → 24 hours
- "since last week" → 168 hours

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt` — `parseJournalNote()` method, `extractSeverityHint()`, `extractDuration()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomJournalScreen.kt` — text input, parse button, review list, save/discard actions
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `parseJournalNote()` returns `ParsedSymptomResult`, `saveParsedSymptoms()`
- Inspect `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt` — parser unit tests
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/SymptomEntity.kt` — where extracted symptoms are stored

## Implementation Guidance

- **Deterministic only**: The parser uses keyword matching, not ML. All behaviors are testable.
- **Review gate**: Users always review extracted symptoms before saving. Any can be discarded.
- **No diagnosis**: Avoid diagnosis, treatment instructions, or high-confidence medical claims. Frame as "detected symptoms" for user review.
- **Original text**: Preserve original journal entry context (stored in SymptomEntity.notes if needed).
- **Unknown input**: If no known symptoms are detected, the screen shows "No known symptoms detected" and examples.
- **Example texts**: Screen shows examples like "I feel tired and have headache for 3 days", "Woke up with fever and cough", "Mild nausea after eating".

## Expected Deliverables

- `LocalRulesEngine.parseJournalNote()` → `List<ParsedSymptom>` with severity + duration.
- `SymptomJournalScreen.kt` — text input UI, analyze button, review list, save/discard.
- `NursyViewModel` bridge: `parseJournalNote()` → `List<ParsedSymptomResult>`, `saveParsedSymptoms()`.
- Unit tests for parser with 15+ test cases (empty, single symptom, multiple, severity modifiers, duration patterns, unknown).

## Verify

- Confirm "I feel tired and have headache for 3 days" extracts Fatigue (severity 2) and Headache (severity 3, 72h duration).
- Confirm "severe chest pain" extracts Chest Pain (severity 5).
- Confirm unknown text like "I feel okay today" returns empty list.
- Confirm saved records appear in symptom history and dashboard summaries.
- Run parser tests: `cd apps/mobile && ./gradlew test`.
