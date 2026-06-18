package com.nursyai.ai.offline

class OfflineAiSummaryEngine(
    private val wordingFilter: SafeHealthWordingFilter = SafeHealthWordingFilter()
) {
    fun generateTestSummary(): String {
        return wordingFilter.filter(
            "Offline AI is ready to summarize symptom journals, weekly patterns, and doctor-visit notes from records saved on this device. " +
                "Use it for clear summaries and explanations, while keeping local rules as the trusted core."
        )
    }
}
