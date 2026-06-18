---
name: nursy-phase-3-local-rules-ai-symptom-journal
description: Execute Nursy AI Phase 3 offline intelligence work, including LocalRulesEngine expansion, repeated symptoms, sleep and fatigue warnings, medication adherence insights, missed check-ins, natural-language symptom parsing, review-and-save flow, and tests.
---

# Nursy Phase 3 Local Rules AI Symptom Journal

## Objective

Add offline intelligence through deterministic local rules and a symptom journal
parser that converts notes into reviewed structured records.

## Start Here

- Read `component` Phase 3.
- Inspect `LocalRulesEngine.kt`.
- Inspect symptom, check-in, and medication entities and DAO queries.
- Use the component skills for Local AI Health Insights and AI Symptom Journal.

## Work Sequence

1. Define a structured insight model.
2. Expand rules for repeated symptoms, sleep plus fatigue, medication adherence,
   and missed check-ins.
3. Add deterministic symptom journal parsing for common symptoms and durations.
4. Build review-and-save behavior before writing extracted symptoms.
5. Add focused tests for rules and parser behavior.

## Guardrails

- Keep all rules available offline.
- Avoid diagnostic or treatment claims.
- Keep thresholds deterministic and testable.
- Do not require cloud AI for journal parsing.

## Exit Criteria

- App generates useful local insights without network access.
- Journal text can create structured symptom records after user review.
- Rules are covered by focused tests.

## Verify

- Test positive, negative, and boundary cases for each rule.
- Test common journal text such as "I feel tired and have headache for 3 days".
- Confirm saved parser output appears in symptom history.
