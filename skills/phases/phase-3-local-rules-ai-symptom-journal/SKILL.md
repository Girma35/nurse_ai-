---
name: nursy-phase-3-local-rules-ai-symptom-journal
description: Execute Nursy AI Phase 3 offline intelligence work, including LocalRulesEngine insight rules, symptom keyword parser with severity and duration extraction, HealthInsight model, ParsedSymptom model, review-and-save flow, and unit tests.
---

# Nursy Phase 3 — Local Rules & AI Symptom Journal

## Objective

Add offline intelligence through deterministic local rules and a symptom journal parser that converts natural-language notes into reviewed, structured symptom records.

## LocalRulesEngine

Rules should cover:

- welcome/no data
- missed check-in
- low sleep and good sleep
- sleep plus fatigue
- repeated symptom
- high severity symptom
- multiple active symptoms
- hydration
- high stress

## Journal Parser

Support:

- known symptom keywords
- severity modifiers such as mild, moderate, severe
- duration extraction such as `for 3 days`, `for 6 hours`, `since yesterday`
- multi-word symptoms such as sore throat and chest pain
- empty result for unknown input

## Review-And-Save Flow

1. User types a health note.
2. App parses locally.
3. User reviews extracted symptoms.
4. User saves selected symptoms to Room.
5. Saved records appear in symptom history and dashboard.

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/screens/SymptomJournalScreen.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`

## Guardrails

- Keep all rules available offline.
- Avoid diagnosis, disease prediction, treatment instructions, and emergency diagnosis claims.
- Use tracking-safe phrasing such as "This is a pattern worth reviewing with a doctor", "You might want to mention this at your next appointment", "Here is a summary of your symptoms", and "No diagnosis is provided, only tracking insights".
- Do not write "you may have disease X" or "this is likely diabetes/depression/infection".
- Keep thresholds deterministic and testable.
- Do not require cloud AI for journal parsing.
- Parser must handle unknown input without creating records.

## Exit Criteria

- App generates useful local insights without network access.
- Journal text can create structured symptom records after user review.
- Rules and parser are covered by focused unit tests.

## Verify

```bash
cd apps/mobile
./gradlew test
```

Test common journal text: `I feel tired and have headache for 3 days`.
