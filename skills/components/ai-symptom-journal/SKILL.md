---
name: nursy-ai-symptom-journal
description: Build or modify the Nursy AI symptom journal, natural-language symptom parsing, structured symptom extraction, duration extraction, severity suggestions, or review-and-save flow. Use when converting health notes into records.
---

# Nursy AI Symptom Journal

## Objective

Convert natural-language health notes into structured symptom records while
keeping the user in control before saving.

## Start Here

- Inspect `LocalRulesEngine.kt` for existing local AI logic.
- Inspect `SymptomEntity.kt` and symptom DAO methods.
- Read `component` sections for AI Symptom Journal and Symptom Tracking.

## Implementation Guidance

- Start with a deterministic local keyword parser.
- Extract symptom candidates, duration phrases, and optional severity hints.
- Present extracted records for review before writing to Room.
- Preserve the original journal text as notes or audit context when useful.
- Avoid diagnosis, treatment instructions, or high-confidence medical claims.
- Keep parser behavior covered by focused unit tests.

## Expected Deliverables

- Journal text input UI.
- Local parser for common symptoms and durations.
- Review-and-save screen or state.
- Integration with symptom creation.
- Tests for parser examples and edge cases.

## Verify

- Confirm "I feel tired and have headache for 3 days" extracts fatigue,
  headache, and duration.
- Confirm unknown text does not create records silently.
- Confirm saved records appear in symptom history and dashboard summaries.
