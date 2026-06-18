---
name: nursy-local-ai-health-insights
description: Build or modify Nursy AI local health insights, on-device rules, repeated symptom detection, sleep and fatigue warnings, medication reminder insights, missed check-in rules, or LocalRulesEngine behavior. Use when adding offline intelligence.
---

# Nursy Local AI Health Insights

## Objective

Generate practical offline insights from local data without depending on cloud AI. The `LocalRulesEngine` provides 7 deterministic rule types that run entirely on-device.

## HealthInsight Model

```kotlin
data class HealthInsight(
    val id: String,
    val severity: InsightSeverity,  // INFO | WARNING | ALERT
    val title: String,
    val message: String,
    val sourceRecordIds: List<String> = emptyList()
)

enum class InsightSeverity { INFO, WARNING, ALERT }
```

## The 7 Rules

### 1. No Data / Welcome
- **Trigger**: `latestCheckIn == null && activeSymptoms.isEmpty() && checkInHistory.isEmpty()`
- **Output**: `INFO` — "Start tracking your health by logging a check-in or symptom."

### 2. Low Sleep / Good Sleep
- **Trigger**: `sleepHours < 6.0` → `WARNING` "Low sleep may contribute to fatigue..."
- **Trigger**: `sleepHours >= 8.0` → `INFO` "You slept X hours, meets recommended range."

### 3. Sleep + Fatigue Combo
- **Trigger**: `sleepHours < 6.0` **AND** active symptom includes "fatigue" or "tired"
- **Output**: `WARNING` — "Low sleep combined with fatigue suggests rest quality may need attention."

### 4. Repeated Symptom
- **Trigger**: Symptom name appears ≥ 2 times → `WARNING`; ≥ 4 times → `ALERT`
- **Output**: `"Symptom has been logged X times recently. Watch the trend..."`

### 5. High Severity Symptom
- **Trigger**: Any active symptom with severity ≥ 4
- **Output**: `ALERT` — "High severity symptoms detected. Monitor closely..."

### 6. Multiple Active Symptoms
- **Trigger**: `activeSymptoms.size >= 3`
- **Output**: `WARNING` — "You have X active symptoms. May indicate an underlying pattern..."

### 7. Missed Check-In Reminder
- **Trigger**: No check-in today BUT check-in history exists
- **Output**: `INFO` — "You haven't checked in today. Last check-in was [date]."

### Bonus Rules (within latestCheckIn):
- **Low Hydration**: `waterIntakeMl < 1000` → `WARNING`
- **Good Hydration**: `waterIntakeMl >= 2000` → `INFO`
- **High Stress**: `stressLevel >= 7` → `WARNING`

## Code Location

- Rules engine: `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt` — `insights()` method
- Unit tests: `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt` — 20+ test cases
- ViewModel integration: `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `insights` StateFlow combining `latestCheckIn` and `activeSymptoms`
- Mobile dashboard display: `apps/mobile/app/src/main/java/com/nursyai/ui/screens/DashboardScreen.kt` — `InsightCard` composable
- Web display: `apps/web/src/components/InsightCarousel.tsx` — renders `HealthInsight[]`

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- Inspect `apps/mobile/app/src/test/java/com/nursyai/ai/LocalRulesEngineTest.kt`
- Inspect Room entities and DAO queries that provide check-ins and symptoms
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — the `insights` StateFlow
- Read component skills for AI Symptom Journal and Health Dashboard

## Implementation Guidance

- **Deterministic**: All rules use simple arithmetic comparisons. No ML, no randomness, no network calls.
- **Testable**: Each rule has positive, negative, and boundary tests in `LocalRulesEngineTest.kt`.
- **No medical claims**: Avoid medical diagnosis, emergency triage, or treatment recommendations. Frame as "tracking guidance."
- **Ordering**: Missed Check-In is evaluated before the no-data guard to ensure returning users see reminders even with no current data.
- **Dashboard integration**: Insights render both on mobile (Compose `InsightCard`) and web (`InsightCarousel`).

## Expected Deliverables

- `LocalRulesEngine.insights()` with 7+ rule types returning `List<HealthInsight>`.
- `HealthInsight` data class and `InsightSeverity` enum.
- `NursyViewModel.insights` StateFlow combining check-in and symptom flows.
- `DashboardScreen.kt` insight cards.
- `InsightCarousel.tsx` for web dashboard.
- 15+ unit tests covering all rules.

## Verify

- Test each rule with positive, negative, and boundary inputs (see `LocalRulesEngineTest.kt`).
- Confirm insights render offline on mobile.
- Confirm insight messages are health-tracking guidance, not diagnosis.
- Run `cd apps/mobile && ./gradlew test` to execute all tests.
