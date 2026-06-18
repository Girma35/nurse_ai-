package com.nursyai.ai.assistant

import com.nursyai.ai.HealthInsight
import com.nursyai.ai.InsightSeverity
import com.nursyai.ai.offline.SafeHealthWordingFilter
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.SymptomEntity

data class AiAssistantReview(
    val title: String,
    val summary: String,
    val urgentReviewNote: String?,
    val insightCount: Int
)

class AiHealthAssistant(
    private val wordingFilter: SafeHealthWordingFilter = SafeHealthWordingFilter()
) {
    fun dailyReview(
        latestCheckIn: DailyCheckInEntity?,
        symptoms: List<SymptomEntity>,
        medications: List<MedicationEntity>,
        insights: List<HealthInsight>
    ): AiAssistantReview {
        val checkInSummary = latestCheckIn?.let {
            "Today's check-in: mood ${it.mood}, energy ${it.energyLevel}/10, sleep ${it.sleepHours}h, stress ${it.stressLevel}/10, water ${it.waterIntakeMl}ml."
        } ?: "No check-in has been logged today."

        val symptomSummary = if (symptoms.isEmpty()) {
            "No active symptoms are saved right now."
        } else {
            "Active symptoms: ${symptoms.joinToString { "${it.name} severity ${it.severity}/5" }}."
        }

        val medicationSummary = if (medications.isEmpty()) {
            "No active medications are saved."
        } else {
            "Active medications: ${medications.joinToString { "${it.name} ${it.dose}" }}."
        }

        val insightSummary = insights.take(3).joinToString(" ") { insight ->
            "${insight.title}: ${insight.message}"
        }

        val summary = wordingFilter.filter(
            listOf(checkInSummary, symptomSummary, medicationSummary, insightSummary)
                .filter { it.isNotBlank() }
                .joinToString("\n")
        )

        return AiAssistantReview(
            title = "Daily AI review",
            summary = summary,
            urgentReviewNote = urgentReviewNote(symptoms, insights),
            insightCount = insights.size
        )
    }

    fun weeklyReview(
        checkIns: List<DailyCheckInEntity>,
        symptoms: List<SymptomEntity>,
        medications: List<MedicationEntity>,
        insights: List<HealthInsight>
    ): AiAssistantReview {
        val recentCheckIns = checkIns.take(7)
        val averageSleep = recentCheckIns.takeIf { it.isNotEmpty() }
            ?.map { it.sleepHours }
            ?.average()
        val averageEnergy = recentCheckIns.takeIf { it.isNotEmpty() }
            ?.map { it.energyLevel }
            ?.average()
        val activeSymptoms = symptoms.filter { it.active }
        val activeMedications = medications.filter { it.active }

        val summary = wordingFilter.filter(
            buildString {
                append("Weekly summary from saved records. ")
                if (averageSleep != null) {
                    append("Average logged sleep was ${"%.1f".format(averageSleep)} hours. ")
                } else {
                    append("No check-ins were available for sleep averaging. ")
                }
                if (averageEnergy != null) {
                    append("Average logged energy was ${"%.1f".format(averageEnergy)}/10. ")
                }
                if (activeSymptoms.isNotEmpty()) {
                    append("Active symptoms include ${activeSymptoms.joinToString { "${it.name} severity ${it.severity}/5" }}. ")
                } else {
                    append("No active symptoms are saved right now. ")
                }
                if (activeMedications.isNotEmpty()) {
                    append("Active medications saved this week include ${activeMedications.joinToString { it.name }}. ")
                }
                if (insights.isNotEmpty()) {
                    append("Local rule insights to review: ${insights.take(3).joinToString { it.title }}.")
                }
            }
        )

        return AiAssistantReview(
            title = "Weekly AI review",
            summary = summary,
            urgentReviewNote = urgentReviewNote(activeSymptoms, insights),
            insightCount = insights.size
        )
    }

    fun customInsight(
        prompt: String,
        latestCheckIn: DailyCheckInEntity?,
        symptoms: List<SymptomEntity>,
        medications: List<MedicationEntity>,
        insights: List<HealthInsight>
    ): AiAssistantReview {
        val question = prompt.trim().ifBlank { "Summarize my saved records." }
        val summary = wordingFilter.filter(
            buildString {
                append("Question: $question\n")
                append("Based on records saved on this device: ")
                if (latestCheckIn != null) {
                    append("latest check-in mood ${latestCheckIn.mood}, energy ${latestCheckIn.energyLevel}/10, sleep ${latestCheckIn.sleepHours}h. ")
                } else {
                    append("no latest check-in is available. ")
                }
                if (symptoms.isNotEmpty()) {
                    append("Active symptoms: ${symptoms.joinToString { "${it.name} severity ${it.severity}/5" }}. ")
                }
                if (medications.isNotEmpty()) {
                    append("Active medications: ${medications.joinToString { "${it.name} ${it.dose}" }}. ")
                }
                if (insights.isNotEmpty()) {
                    append("Local patterns worth reviewing: ${insights.take(3).joinToString { it.message }}")
                }
            }
        )

        return AiAssistantReview(
            title = "Custom AI insight",
            summary = summary,
            urgentReviewNote = urgentReviewNote(symptoms, insights),
            insightCount = insights.size
        )
    }

    private fun urgentReviewNote(
        symptoms: List<SymptomEntity>,
        insights: List<HealthInsight>
    ): String? {
        val severeSymptoms = symptoms.filter { it.severity >= 5 }
        val alertInsights = insights.filter { it.severity == InsightSeverity.ALERT }
        if (severeSymptoms.isEmpty() && alertInsights.isEmpty()) return null

        val symptomNames = severeSymptoms.joinToString { it.name }.ifBlank { "high-severity symptoms" }
        return "You logged $symptomNames. If this feels urgent or severe to you, use local emergency services. Otherwise, this is a pattern worth reviewing with a qualified professional."
    }
}
