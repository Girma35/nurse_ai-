---
name: nursy-local-ai-health-insights
description: Build or modify Nursy AI local health insights, on-device rules, repeated symptom detection, sleep and fatigue warnings, medication reminder insights, missed check-in rules, or LocalRulesEngine behavior. Use when adding offline intelligence.
---

# Nursy Local AI Health Insights

## Objective

Generate practical offline insights from local data without depending on cloud AI. `LocalRulesEngine` is deterministic, testable, and runs entirely on-device.

## HealthInsight Model

```kotlin
data class HealthInsight(
    val id: String,
    val severity: InsightSeverity,
    val title: String,
    val message: String,
    val sourceRecordIds: List<String> = emptyList()
)

enum class InsightSeverity { INFO, WARNING, ALERT }
```

## Rule Set

- welcome/no data
- missed check-in
- low sleep and good sleep
- sleep plus fatigue
- repeated symptom
- high severity symptom
- multiple active symptoms
- low/good hydration
- high stress

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- Inspect `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DashboardScreen.kt`
- Inspect Room entities and DAO queries that provide check-ins and symptoms

## Implementation Guidance

- Use simple arithmetic and comparisons.
- Avoid randomness, network calls, and hidden state.
- Add positive, negative, and boundary tests for each rule.
- Avoid diagnosis, disease prediction, treatment instructions, and replacement for clinician advice.
- Phrase outputs as tracking guidance, summaries, or patterns.
- Prefer safe wording such as "This is a pattern worth reviewing with a doctor", "You might want to mention this at your next appointment", "Here is a summary of your symptoms", and "No diagnosis is provided, only tracking insights".
- Never write "you may have disease X", "this is likely diabetes/depression/infection", prescribing instructions, or emergency diagnosis claims.
- Keep insight source record ids for explainability.

## Expected Deliverables

- `LocalRulesEngine.insights()` returning `List<HealthInsight>`.
- `HealthInsight` and `InsightSeverity`.
- `NursyViewModel` flow integration.
- Dashboard insight cards.
- Unit tests covering rules and parser behavior.

## Verify

- Run `cd apps/mobile && ./gradlew test`.
- Confirm insights render offline on mobile.
- Confirm messages are health-tracking guidance, not diagnosis, disease prediction, or treatment.
