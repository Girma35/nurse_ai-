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
                        message = "You slept ${checkIn.sleepHours} hours. Low sleep may contribute to fatigue and lower energy. Aim for 7-9 hours.",
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
                        message = "Low sleep combined with fatigue symptoms suggests rest quality may need attention.",
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
                    message = "$symptomName has been logged $count times recently. Watch the trend and consult a professional if it persists.",
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
                    message = "$names are at high severity (4-5/5). Monitor closely and seek medical attention if needed.",
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
                    message = "You have ${activeSymptoms.size} active symptoms. This may indicate an underlying pattern worth discussing with a healthcare provider.",
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
                        message = "You've had ${checkIn.waterIntakeMl}ml of water today. Consider increasing intake toward the daily goal of 2000ml.",
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
                        message = "Your stress level is ${checkIn.stressLevel}/10. Consider relaxation techniques or a short break.",
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

        // Known symptom keywords with default severity
        val symptomKeywords = mapOf(
            "headache" to 3,
            "head" to 2,
            "migraine" to 4,
            "fever" to 4,
            "temperature" to 3,
            "fatigue" to 2,
            "tired" to 2,
            "exhausted" to 3,
            "nausea" to 3,
            "vomiting" to 4,
            "cough" to 2,
            "sore throat" to 2,
            "runny nose" to 1,
            "congestion" to 2,
            "body ache" to 3,
            "muscle pain" to 3,
            "joint pain" to 3,
            "dizziness" to 3,
            "chest pain" to 5,
            "shortness of breath" to 5,
            "rash" to 2,
            "itching" to 2,
            "bloating" to 2,
            "cramps" to 3,
            "diarrhea" to 3,
            "constipation" to 2,
            "anxiety" to 3,
            "insomnia" to 3,
            "chills" to 3,
            "sweating" to 2,
            "back pain" to 3,
            "neck pain" to 3
        )

        // Check for multi-word symptoms first
        for ((keyword, defaultSeverity) in symptomKeywords.entries.sortedByDescending { it.key.length }) {
            if (normalized.contains(keyword) && parsed.none { it.name == keyword }) {
                val severity = extractSeverityHint(normalized, keyword) ?: defaultSeverity
                val duration = extractDuration(normalized, keyword)

                parsed.add(
                    ParsedSymptom(
                        name = keyword.replaceFirstChar { it.uppercase() },
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
