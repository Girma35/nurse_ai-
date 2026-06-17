package com.nursyai.ai

import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.SymptomEntity

class LocalRulesEngine {
    fun insights(
        latestCheckIn: DailyCheckInEntity?,
        activeSymptoms: List<SymptomEntity>
    ): List<String> {
        val insights = mutableListOf<String>()

        if (latestCheckIn != null && latestCheckIn.sleepHours < 6.0) {
            insights += "Low sleep may be contributing to fatigue today."
        }

        val repeatedSymptoms = activeSymptoms
            .groupBy { it.name.lowercase() }
            .filterValues { it.size >= 2 }
            .keys

        repeatedSymptoms.forEach { symptom ->
            insights += "Repeated $symptom logs detected. Watch the trend over time."
        }

        return insights
    }

    fun extractSymptoms(note: String): List<String> {
        val knownSymptoms = listOf("headache", "fever", "fatigue", "nausea", "cough")
        val normalizedNote = note.lowercase()

        return knownSymptoms.filter { symptom -> normalizedNote.contains(symptom) }
    }
}
