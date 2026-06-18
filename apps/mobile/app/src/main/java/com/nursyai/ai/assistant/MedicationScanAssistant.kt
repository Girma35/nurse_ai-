package com.nursyai.ai.assistant

data class MedicationDraft(
    val name: String = "",
    val dose: String = "",
    val frequency: String = "",
    val scheduledTimesCsv: String = "",
    val rawText: String = "",
    val uncertainFields: List<String> = emptyList()
) {
    val hasAnyField: Boolean
        get() = name.isNotBlank() || dose.isNotBlank() || frequency.isNotBlank() || scheduledTimesCsv.isNotBlank()
}

class MedicationScanAssistant {
    fun parseOcrText(rawText: String): MedicationDraft {
        val cleaned = rawText
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (cleaned.isBlank()) {
            return MedicationDraft(
                rawText = rawText,
                uncertainFields = listOf("name", "dose", "frequency", "schedule")
            )
        }

        val dose = extractDose(cleaned).orEmpty()
        val frequency = extractFrequency(cleaned).orEmpty()
        val schedule = extractSchedule(cleaned, frequency).orEmpty()
        val name = extractName(cleaned, dose).orEmpty()

        val uncertain = buildList {
            if (name.isBlank()) add("name")
            if (dose.isBlank()) add("dose")
            if (frequency.isBlank()) add("frequency")
            if (schedule.isBlank()) add("schedule")
        }

        return MedicationDraft(
            name = name,
            dose = dose,
            frequency = frequency,
            scheduledTimesCsv = schedule,
            rawText = cleaned,
            uncertainFields = uncertain
        )
    }

    private fun extractDose(text: String): String? {
        val dosePattern = Regex(
            "\\b\\d+(?:\\.\\d+)?\\s*(mg|mcg|g|ml|units?|iu)\\b",
            RegexOption.IGNORE_CASE
        )
        return dosePattern.find(text)?.value?.normalizeUnitSpacing()
    }

    private fun extractFrequency(text: String): String? {
        val normalized = text.lowercase()
        return when {
            Regex("\\bonce\\s+daily\\b|\\bdaily\\b|\\bevery\\s+day\\b").containsMatchIn(normalized) -> "Daily"
            Regex("\\btwice\\s+daily\\b|\\btwo\\s+times\\s+daily\\b|\\b2\\s+times\\s+daily\\b").containsMatchIn(normalized) -> "Twice daily"
            Regex("\\bthree\\s+times\\s+daily\\b|\\b3\\s+times\\s+daily\\b").containsMatchIn(normalized) -> "Three times daily"
            Regex("\\bevery\\s+morning\\b|\\bin\\s+the\\s+morning\\b").containsMatchIn(normalized) -> "Every morning"
            Regex("\\bevery\\s+night\\b|\\bat\\s+night\\b|\\bbefore\\s+bed\\b").containsMatchIn(normalized) -> "Every night"
            Regex("\\bevery\\s+\\d+\\s+hours?\\b").containsMatchIn(normalized) ->
                Regex("\\bevery\\s+\\d+\\s+hours?\\b").find(normalized)?.value?.replaceFirstChar { it.uppercase() }
            else -> null
        }
    }

    private fun extractSchedule(text: String, frequency: String?): String? {
        val explicitTimes = Regex("\\b([01]?\\d|2[0-3]):[0-5]\\d\\b")
            .findAll(text)
            .map { it.value.padStart(5, '0') }
            .distinct()
            .toList()
        if (explicitTimes.isNotEmpty()) return explicitTimes.joinToString(", ")

        val normalized = text.lowercase()
        val schedule = linkedSetOf<String>()
        if ("morning" in normalized) schedule.add("08:00")
        if ("afternoon" in normalized) schedule.add("14:00")
        if ("evening" in normalized) schedule.add("18:00")
        if ("night" in normalized || "bed" in normalized) schedule.add("20:00")
        if (schedule.isNotEmpty()) return schedule.joinToString(", ")

        return when (frequency) {
            "Daily", "Every morning" -> "08:00"
            "Twice daily" -> "08:00, 20:00"
            "Three times daily" -> "08:00, 14:00, 20:00"
            "Every night" -> "20:00"
            else -> null
        }
    }

    private fun extractName(text: String, dose: String?): String? {
        val beforeDose = dose?.let { text.substringBefore(it, missingDelimiterValue = text) } ?: text
        val tokens = beforeDose
            .replace(Regex("[^A-Za-z\\s-]"), " ")
            .split(Regex("\\s+"))
            .map { it.trim('-', ' ') }
            .filter { it.length >= 3 }
            .filterNot { it.lowercase() in ignoredNameWords }

        return tokens.firstOrNull()?.replaceFirstChar { it.uppercase() }
    }

    private fun String.normalizeUnitSpacing(): String {
        return replace(Regex("\\s+"), " ").trim().lowercase()
            .replaceFirstChar { it.uppercase() }
    }

    private companion object {
        val ignoredNameWords = setOf(
            "take",
            "tablet",
            "tablets",
            "capsule",
            "capsules",
            "dose",
            "daily",
            "morning",
            "night",
            "evening",
            "afternoon",
            "with",
            "food",
            "before",
            "after",
            "prescription",
            "pharmacy"
        )
    }
}
