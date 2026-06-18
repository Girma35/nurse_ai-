---
name: nursy-phase-3-local-rules-ai-symptom-journal
description: Execute Nursy AI Phase 3 offline intelligence work, including LocalRulesEngine with 7 insight rule types, 30+ symptom keyword parser with severity and duration extraction, HealthInsight model, ParsedSymptom model, review-and-save flow, and 20+ unit tests.
---

# Nursy Phase 3 — Local Rules & AI Symptom Journal

## Objective

Add offline intelligence through deterministic local rules and a symptom journal parser that converts natural-language notes into reviewed, structured symptom records.

## What Was Built

### LocalRulesEngine.insights() — 7 Rule Types

Ran on-device via `NursyViewModel.insights` StateFlow combining `latestCheckIn` and `activeSymptoms`:

| # | Rule | Condition | Severity |
|---|---|---|---|
| 1 | Welcome/No Data | No check-in, no symptoms, no history | INFO |
| 2 | Low Sleep | sleepHours < 6.0 | WARNING |
| 3 | Good Sleep | sleepHours >= 8.0 | INFO |
| 4 | Sleep + Fatigue Combo | Low sleep AND fatigue symptom | WARNING |
| 5 | Repeated Symptom | Same symptom 2+ times (4+ = ALERT) | WARNING/ALERT |
| 6 | High Severity | Any symptom severity >= 4 | ALERT |
| 7 | Multiple Symptoms | 3+ active symptoms | WARNING |
| 8 | Missed Check-In | No check-in today but history exists | INFO |
| 9 | Low Hydration | waterIntakeMl < 1000 | WARNING |
| 10 | Good Hydration | waterIntakeMl >= 2000 | INFO |
| 11 | High Stress | stressLevel >= 7 | WARNING |

### LocalRulesEngine.parseJournalNote() — NL Parser

- **30+ symptom keywords** with default severity (headache→3, chest pain→5, etc.)
- **Severity modifier extraction**: "severe headache" → 5, "mild headache" → 2
- **Duration extraction**: "for 3 days" → 72h, "for 6 hours" → 6h, "since yesterday" → 24h
- **Multi-word support**: "body ache", "sore throat", "chest pain", "shortness of breath"
- **No match = empty list**: Unknown text (like "I feel okay today") returns nothing

### SymptomJournalScreen — Review-and-Save Flow

1. User types natural language note
2. Taps "Analyze" → parses via `LocalRulesEngine.parseJournalNote()`
3. Review extracted symptoms with name, severity, duration displayed
4. Tap "Save All to Symptoms" → creates `SymptomEntity` records via `NursyViewModel.saveParsedSymptoms()`
5. Tap "Discard" → clears without saving

### Tests (`LocalRulesEngineTest.kt`) — 20+ test cases

Covers every rule: positive, negative, boundary cases. Parser tests include empty input, single symptom, multiple symptoms, severity modifiers ("severe", "mild"), duration patterns ("for N days", "for N hours", "since yesterday"), and unknown text.

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt` — `insights()`, `parseJournalNote()`, `extractSeverityHint()`, `extractDuration()`
- `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt` — 20+ unit tests
- `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomJournalScreen.kt` — review-and-save UI
- `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `parseJournalNote()` bridge, `saveParsedSymptoms()`
- `apps/web/src/lib/cloud-ai-reports.ts` — cloud-level weekly report generation (Phase 7 complement)

## Guardrails

- Keep all rules available offline — no network dependency.
- Avoid diagnostic or treatment claims — frame as "tracking guidance" and "insights".
- Keep thresholds deterministic and testable — all rules use simple arithmetic.
- Do not require cloud AI for journal parsing — entirely local keyword matching.
- Parser must gracefully handle unknown input without creating records.

## Exit Criteria

- App generates useful local insights without network access.
- Journal text can create structured symptom records after user review.
- Rules and parser are covered by focused unit tests.
- Insights render in the mobile dashboard offline.

## Verify

- Run `cd apps/mobile && ./gradlew test` to execute all 20+ tests.
- Test positive, negative, and boundary cases for each rule.
- Test common journal text: "I feel tired and have headache for 3 days".
- Confirm saved parser output appears in symptom history.
- Confirm no-data, low-sleep, high-severity, repeated-symptom rules all trigger correctly.
