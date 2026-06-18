package com.nursyai.ai.offline

class SafeHealthWordingFilter {
    fun filter(text: String): String {
        val cleaned = forbiddenPatterns.fold(text) { current, pattern ->
            current.replace(pattern, "this is a pattern worth reviewing")
        }.trim()

        val safetyNote = "No diagnosis is provided, only tracking insights."
        return if (cleaned.contains(safetyNote, ignoreCase = true)) {
            cleaned
        } else {
            "$cleaned\n\n$safetyNote"
        }
    }

    private companion object {
        val forbiddenPatterns = listOf(
            Regex("you may have\\s+[a-zA-Z\\s-]+", RegexOption.IGNORE_CASE),
            Regex("this is likely\\s+[a-zA-Z\\s-]+", RegexOption.IGNORE_CASE),
            Regex("likely\\s+(diabetes|depression|infection)", RegexOption.IGNORE_CASE),
            Regex("you should take\\s+[a-zA-Z\\s-]+", RegexOption.IGNORE_CASE),
            Regex("prescribe\\s+[a-zA-Z\\s-]+", RegexOption.IGNORE_CASE)
        )
    }
}
