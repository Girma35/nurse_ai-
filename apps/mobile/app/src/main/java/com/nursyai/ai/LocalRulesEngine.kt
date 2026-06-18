package com.nursyai.ai

import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.SymptomEntity

data class HealthInsight(
    val id: String,
    val severity: InsightSeverity,
    val title: String,
    val message: String,
    val sourceRecordIds: List<String> = emptyList()
)

enum class InsightSeverity { INFO, WARNING, ALERT }

data class ParsedSymptom(
    val name: String,
    val severity: Int = 2,
    val durationHours: Int? = null
)

class LocalRulesEngine {

    /**
     * Generate deterministic local health insights from check-in and symptom data.
     */
    fun insights(
        latestCheckIn: DailyCheckInEntity?,
        activeSymptoms: List<SymptomEntity>,
        checkInHistory: List<DailyCheckInEntity> = emptyList()
    ): List<HealthInsight> {
        val insights = mutableListOf<HealthInsight>()

        // ─── Missed Check-In Rule (checked before no-data guard) ──

        if (checkInHistory.isNotEmpty()) {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())
            val recentCheckIns = checkInHistory.take(5)

            if (recentCheckIns.none { it.date == today }) {
                val lastDate = recentCheckIns.firstOrNull()?.date ?: "unknown"
                insights.add(
                    HealthInsight(
                        id = "missed-checkin",
                        severity = InsightSeverity.INFO,
                        title = "Check-In Reminder",
                        message = "You haven't checked in today. Last check-in was $lastDate.",
                        sourceRecordIds = recentCheckIns.map { it.id }
                    )
                )
                // Still generate other insights from latestCheckIn if available
            }
        }

        // No data insight (only if no check-in history and no symptoms)
        if (latestCheckIn == null && activeSymptoms.isEmpty() && checkInHistory.isEmpty()) {
            insights.add(
                HealthInsight(
                    id = "no-data",
                    severity = InsightSeverity.INFO,
                    title = "Welcome",
                    message = "Start tracking your health by logging a check-in or symptom."
                )
            )
            return insights
        }

        // ─── Sleep Rules ────────────────────────────────────────

        latestCheckIn?.let { checkIn ->
            if (checkIn.sleepHours < 6.0) {
                insights.add(
                    HealthInsight(
                        id = "low-sleep",
                        severity = InsightSeverity.WARNING,
                        title = "Low Sleep Detected",
                        message = "You slept ${checkIn.sleepHours} hours. This is a sleep pattern worth tracking alongside energy and fatigue.",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            } else if (checkIn.sleepHours >= 8.0) {
                insights.add(
                    HealthInsight(
                        id = "good-sleep",
                        severity = InsightSeverity.INFO,
                        title = "Good Sleep",
                        message = "You slept ${checkIn.sleepHours} hours, which meets the recommended range. Keep it up!",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            }

            // Sleep + fatigue combined rule
            val hasFatigue = activeSymptoms.any {
                it.name.lowercase().contains("fatigue") || it.name.lowercase().contains("tired")
            }
            if (checkIn.sleepHours < 6.0 && hasFatigue) {
                insights.add(
                    HealthInsight(
                        id = "sleep-fatigue-combo",
                        severity = InsightSeverity.WARNING,
                        title = "Sleep & Fatigue Connection",
                        message = "Low sleep and fatigue appeared together. You might want to mention this pattern at your next appointment if it continues.",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            }
        }

        // ─── Repeated Symptom Rules ─────────────────────────────

        val symptomCounts = activeSymptoms
            .groupBy { it.name.lowercase().trim() }
            .mapValues { it.value.size }

        symptomCounts.filterValues { it >= 2 }.forEach { (symptomName, count) ->
            val matchingSymptoms = activeSymptoms.filter {
                it.name.lowercase().trim() == symptomName
            }
            insights.add(
                HealthInsight(
                    id = "repeated-symptom-$symptomName",
                    severity = if (count >= 4) InsightSeverity.ALERT else InsightSeverity.WARNING,
                    title = "Repeated $symptomName",
                    message = "$symptomName has been logged $count times recently. This is a pattern worth reviewing with a doctor if it continues.",
                    sourceRecordIds = matchingSymptoms.map { it.id }
                )
            )
        }

        // ─── High Severity Symptom Rule ─────────────────────────

        val highSeveritySymptoms = activeSymptoms.filter { it.severity >= 4 }
        if (highSeveritySymptoms.isNotEmpty()) {
            val names = highSeveritySymptoms.joinToString(", ") { it.name }
            insights.add(
                HealthInsight(
                    id = "high-severity-symptoms",
                    severity = InsightSeverity.ALERT,
                    title = "High Severity Symptoms",
                    message = "Here is a summary of your high-severity symptoms: $names. No diagnosis is provided, only tracking insights.",
                    sourceRecordIds = highSeveritySymptoms.map { it.id }
                )
            )
        }

        // ─── Multiple Symptoms Rule ─────────────────────────────

        if (activeSymptoms.size >= 3) {
            insights.add(
                HealthInsight(
                    id = "multiple-symptoms",
                    severity = InsightSeverity.WARNING,
                    title = "Multiple Active Symptoms",
                    message = "You have ${activeSymptoms.size} active symptoms. This summary may be useful to mention at your next appointment.",
                    sourceRecordIds = activeSymptoms.map { it.id }
                )
            )
        }



        // ─── Hydration Rule ─────────────────────────────────────

        latestCheckIn?.let { checkIn ->
            if (checkIn.waterIntakeMl < 1000) {
                insights.add(
                    HealthInsight(
                        id = "low-hydration",
                        severity = InsightSeverity.WARNING,
                        title = "Low Water Intake",
                        message = "You've logged ${checkIn.waterIntakeMl}ml of water today. Keep tracking hydration so patterns are easier to review.",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            } else if (checkIn.waterIntakeMl >= 2000) {
                insights.add(
                    HealthInsight(
                        id = "good-hydration",
                        severity = InsightSeverity.INFO,
                        title = "Good Hydration",
                        message = "Great job reaching ${checkIn.waterIntakeMl}ml of water today!",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            }
        }

        // ─── Stress Rule ────────────────────────────────────────

        latestCheckIn?.let { checkIn ->
            if (checkIn.stressLevel >= 7) {
                insights.add(
                    HealthInsight(
                        id = "high-stress",
                        severity = InsightSeverity.WARNING,
                        title = "High Stress Level",
                        message = "Your stress level is ${checkIn.stressLevel}/10. Keep this in your summary so stress patterns are easier to review.",
                        sourceRecordIds = listOf(checkIn.id)
                    )
                )
            }
        }

        return insights
    }

    /**
     * Parse natural language health notes into structured symptom candidates.
     */
    fun parseJournalNote(note: String): List<ParsedSymptom> {
        if (note.isBlank()) return emptyList()

        val normalized = note.lowercase().trim()
        val parsed = mutableListOf<ParsedSymptom>()

        data class SymptomKeyword(
            val keyword: String,
            val displayName: String = keyword.replaceFirstChar { it.uppercase() },
            val defaultSeverity: Int
        )

        val symptomKeywords = listOf(
            SymptomKeyword("headache", defaultSeverity = 3),
            SymptomKeyword("head", defaultSeverity = 2),
            SymptomKeyword("migraine", defaultSeverity = 4),
            SymptomKeyword("fever", defaultSeverity = 4),
            SymptomKeyword("temperature", defaultSeverity = 3),
            SymptomKeyword("fatigue", defaultSeverity = 2),
            SymptomKeyword("tired", displayName = "Fatigue", defaultSeverity = 2),
            SymptomKeyword("exhausted", displayName = "Fatigue", defaultSeverity = 3),
            SymptomKeyword("nausea", defaultSeverity = 3),
            SymptomKeyword("vomiting", defaultSeverity = 4),
            SymptomKeyword("cough", defaultSeverity = 2),
            SymptomKeyword("sore throat", defaultSeverity = 2),
            SymptomKeyword("runny nose", defaultSeverity = 1),
            SymptomKeyword("congestion", defaultSeverity = 2),
            SymptomKeyword("body ache", defaultSeverity = 3),
            SymptomKeyword("muscle pain", defaultSeverity = 3),
            SymptomKeyword("joint pain", defaultSeverity = 3),
            SymptomKeyword("dizziness", defaultSeverity = 3),
            SymptomKeyword("chest pain", defaultSeverity = 5),
            SymptomKeyword("shortness of breath", defaultSeverity = 5),
            SymptomKeyword("rash", defaultSeverity = 2),
            SymptomKeyword("itching", defaultSeverity = 2),
            SymptomKeyword("bloating", defaultSeverity = 2),
            SymptomKeyword("cramps", defaultSeverity = 3),
            SymptomKeyword("diarrhea", defaultSeverity = 3),
            SymptomKeyword("constipation", defaultSeverity = 2),
            SymptomKeyword("anxiety", defaultSeverity = 3),
            SymptomKeyword("insomnia", defaultSeverity = 3),
            SymptomKeyword("chills", defaultSeverity = 3),
            SymptomKeyword("sweating", defaultSeverity = 2),
            SymptomKeyword("back pain", defaultSeverity = 3),
            SymptomKeyword("neck pain", defaultSeverity = 3)
        )

        for (entry in symptomKeywords.sortedByDescending { it.keyword.length }) {
            val keywordPattern = Regex("\\b${Regex.escape(entry.keyword)}\\b", RegexOption.IGNORE_CASE)
            if (keywordPattern.containsMatchIn(normalized) && parsed.none { it.name == entry.displayName }) {
                val severity = extractSeverityHint(normalized, entry.keyword) ?: entry.defaultSeverity
                val duration = extractDuration(normalized, entry.keyword)

                parsed.add(
                    ParsedSymptom(
                        name = entry.displayName,
                        severity = severity.coerceIn(1, 5),
                        durationHours = duration
                    )
                )
            }
        }

        return parsed
    }

    /**
     * Extract a severity hint from text near the symptom keyword.
     * Looks for patterns like "severe headache", "mild headache", etc.
     */
    private fun extractSeverityHint(text: String, symptom: String): Int? {
        val contextPattern = Regex(
            "(\\w+)\\s+$symptom",
            RegexOption.IGNORE_CASE
        )
        val match = contextPattern.find(text) ?: return null
        val modifier = match.groupValues[1].lowercase()

        return when (modifier) {
            "severe", "very", "extreme", "intense", "debilitating" -> 5
            "quite", "pretty", "significant", "bad" -> 4
            "moderate", "some" -> 3
            "mild", "slight", "little" -> 2
            "barely", "minor" -> 1
            else -> null
        }
    }

    /**
     * Extract duration information from text near the symptom keyword.
     * Supports: "for X days", "for X hours", "since yesterday", etc.
     */
    private fun extractDuration(text: String, symptom: String): Int? {
        // Pattern: "symptom for N days/hours"
        val forPattern = Regex(
            "$symptom\\s+for\\s+(\\d+)\\s*(day|hour|week)s?",
            RegexOption.IGNORE_CASE
        )
        val forMatch = forPattern.find(text)
        if (forMatch != null) {
            val amount = forMatch.groupValues[1].toIntOrNull() ?: return null
            val unit = forMatch.groupValues[2].lowercase()
            return when (unit) {
                "day" -> amount * 24
                "week" -> amount * 24 * 7
                "hour" -> amount
                else -> null
            }
        }

        // Pattern: "N days/hours of symptom"
        val ofPattern = Regex(
            "(\\d+)\\s*(day|hour|week)s?\\s+of\\s+$symptom",
            RegexOption.IGNORE_CASE
        )
        val ofMatch = ofPattern.find(text)
        if (ofMatch != null) {
            val amount = ofMatch.groupValues[1].toIntOrNull() ?: return null
            val unit = ofMatch.groupValues[2].lowercase()
            return when (unit) {
                "day" -> amount * 24
                "week" -> amount * 24 * 7
                "hour" -> amount
                else -> null
            }
        }

        // Duration keyword: "since yesterday"
        if (Regex("since yesterday", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            return 24
        }
        if (Regex("since last week", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            return 24 * 7
        }

        return null
    }

    /**
     * Legacy compatibility wrapper.
     */
    fun extractSymptoms(note: String): List<String> {
        return parseJournalNote(note).map { it.name }
    }
}
