package com.nursyai.ai

import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.SymptomEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalRulesEngineTest {

    private lateinit var engine: LocalRulesEngine

    @Before
    fun setUp() {
        engine = LocalRulesEngine()
    }

    // ─── Insights Tests ───────────────────────────────────────

    @Test
    fun `no data returns welcome insight`() {
        val results = engine.insights(null, emptyList())
        assertEquals(1, results.size)
        assertEquals("no-data", results[0].id)
        assertEquals(InsightSeverity.INFO, results[0].severity)
    }

    @Test
    fun `low sleep produces warning`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-1", userId = "u1", date = "2026-06-17",
            mood = "steady", energyLevel = 5, sleepHours = 5.0,
            sleepQuality = "poor", stressLevel = 5, waterIntakeMl = 1000
        )
        val results = engine.insights(checkIn, emptyList())
        assertTrue(results.any { it.id == "low-sleep" })
        assertEquals(InsightSeverity.WARNING, results.first { it.id == "low-sleep" }.severity)
    }

    @Test
    fun `good sleep at 8 hours produces info`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-2", userId = "u1", date = "2026-06-17",
            mood = "great", energyLevel = 8, sleepHours = 8.5,
            sleepQuality = "excellent", stressLevel = 2, waterIntakeMl = 2000
        )
        val results = engine.insights(checkIn, emptyList())
        assertTrue(results.any { it.id == "good-sleep" })
    }

    @Test
    fun `repeated symptoms trigger warning`() {
        val symptoms = listOf(
            SymptomEntity(id = "s1", userId = "u1", name = "Headache", severity = 2, startedAt = 1000),
            SymptomEntity(id = "s2", userId = "u1", name = "Headache", severity = 3, startedAt = 2000),
            SymptomEntity(id = "s3", userId = "u1", name = "Cough", severity = 2, startedAt = 1500)
        )
        val results = engine.insights(null, symptoms)
        assertTrue(results.any { it.id == "repeated-symptom-headache" })
        assertFalse(results.any { it.id == "repeated-symptom-cough" })
    }

    @Test
    fun `repeated symptoms 4 or more times produces alert`() {
        val symptoms = (1..4).map {
            SymptomEntity(id = "s$it", userId = "u1", name = "Headache", severity = 2, startedAt = it * 1000L)
        }
        val results = engine.insights(null, symptoms)
        val headacheInsight = results.first { it.id == "repeated-symptom-headache" }
        assertEquals(InsightSeverity.ALERT, headacheInsight.severity)
    }

    @Test
    fun `high severity symptoms produce alert`() {
        val symptoms = listOf(
            SymptomEntity(id = "s1", userId = "u1", name = "Chest pain", severity = 5, startedAt = 1000)
        )
        val results = engine.insights(null, symptoms)
        assertTrue(results.any { it.id == "high-severity-symptoms" })
        assertEquals(InsightSeverity.ALERT, results.first { it.id == "high-severity-symptoms" }.severity)
    }

    @Test
    fun `three plus symptoms trigger multiple symptoms warning`() {
        val symptoms = listOf(
            SymptomEntity(id = "s1", userId = "u1", name = "Headache", severity = 2, startedAt = 1000),
            SymptomEntity(id = "s2", userId = "u1", name = "Cough", severity = 2, startedAt = 1500),
            SymptomEntity(id = "s3", userId = "u1", name = "Fatigue", severity = 3, startedAt = 2000)
        )
        val results = engine.insights(null, symptoms)
        assertTrue(results.any { it.id == "multiple-symptoms" })
    }

    @Test
    fun `sleep and fatigue combination detected`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-1", userId = "u1", date = "2026-06-17",
            mood = "low", energyLevel = 3, sleepHours = 5.0,
            sleepQuality = "poor", stressLevel = 6, waterIntakeMl = 800
        )
        val symptoms = listOf(
            SymptomEntity(id = "s1", userId = "u1", name = "Fatigue", severity = 3, startedAt = 1000)
        )
        val results = engine.insights(checkIn, symptoms)
        assertTrue(results.any { it.id == "sleep-fatigue-combo" })
    }

    @Test
    fun `missed check-in detected when no check-in today`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-old", userId = "u1", date = "2026-06-15",
            mood = "steady", energyLevel = 5, sleepHours = 7.0,
            sleepQuality = "good", stressLevel = 3, waterIntakeMl = 1500
        )
        val results = engine.insights(null, emptyList(), listOf(checkIn))
        // With the rule ordering fix, missed-checkin is checked before the no-data guard
        assertTrue("Expected missed-checkin insight", results.any { it.id == "missed-checkin" })
        assertEquals(InsightSeverity.INFO, results.first { it.id == "missed-checkin" }.severity)
    }

    @Test
    fun `low water intake produces warning`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-1", userId = "u1", date = "2026-06-17",
            mood = "steady", energyLevel = 5, sleepHours = 7.0,
            sleepQuality = "good", stressLevel = 3, waterIntakeMl = 500
        )
        val results = engine.insights(checkIn, emptyList())
        assertTrue(results.any { it.id == "low-hydration" })
    }

    @Test
    fun `high stress level produces warning`() {
        val checkIn = DailyCheckInEntity(
            id = "ci-1", userId = "u1", date = "2026-06-17",
            mood = "down", energyLevel = 4, sleepHours = 6.0,
            sleepQuality = "poor", stressLevel = 8, waterIntakeMl = 1000
        )
        val results = engine.insights(checkIn, emptyList())
        assertTrue(results.any { it.id == "high-stress" })
    }

    // ─── Journal Parser Tests ─────────────────────────────────

    @Test
    fun `parse empty note returns empty list`() {
        assertTrue(engine.parseJournalNote("").isEmpty())
        assertTrue(engine.parseJournalNote("   ").isEmpty())
    }

    @Test
    fun `parse headache and fatigue from natural text`() {
        val result = engine.parseJournalNote("I feel tired and have headache for 3 days")
        val names = result.map { it.name.lowercase() }
        assertTrue(names.contains("headache"))
        assertTrue(names.contains("fatigue"))
    }

    @Test
    fun `parse extracts duration from for pattern`() {
        val result = engine.parseJournalNote("I have headache for 3 days")
        assertEquals(1, result.size)
        assertEquals("Headache", result[0].name)
        assertEquals(72, result[0].durationHours) // 3 days * 24 hours
    }

    @Test
    fun `parse extracts severity modifier`() {
        val result = engine.parseJournalNote("I have severe headache")
        assertTrue(result.any { it.name.lowercase() == "headache" })
        val headache = result.first { it.name.lowercase() == "headache" }
        assertEquals(5, headache.severity)
    }

    @Test
    fun `parse extracts mild severity`() {
        val result = engine.parseJournalNote("mild headache and slight cough")
        val headache = result.first { it.name.lowercase() == "headache" }
        assertEquals(2, headache.severity)
    }

    @Test
    fun `parse fever and cough`() {
        val result = engine.parseJournalNote("Woke up with fever and cough")
        val names = result.map { it.name.lowercase() }
        assertTrue(names.contains("fever"))
        assertTrue(names.contains("cough"))
    }

    @Test
    fun `parse nausea from text`() {
        val result = engine.parseJournalNote("Mild nausea after eating")
        assertTrue(result.any { it.name.lowercase() == "nausea" })
        val nausea = result.first { it.name.lowercase() == "nausea" }
        assertEquals(2, nausea.severity) // mild modifier
    }

    @Test
    fun `unknown text does not create records silently`() {
        val result = engine.parseJournalNote("I feel okay today")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `multiple symptoms parsed from compound text`() {
        val result = engine.parseJournalNote("severe headache, fever, and body ache for 2 days")
        val names = result.map { it.name.lowercase() }
        assertTrue(names.contains("headache"))
        assertTrue(names.contains("fever"))
        assertTrue(names.contains("body ache"))
    }

    @Test
    fun `chest pain gets max severity`() {
        val result = engine.parseJournalNote("I have chest pain")
        assertTrue(result.any { it.name.lowercase() == "chest pain" })
        val chestPain = result.first { it.name.lowercase() == "chest pain" }
        assertEquals(5, chestPain.severity)
    }

    @Test
    fun `extractSymptoms legacy wrapper works`() {
        val result = engine.extractSymptoms("headache and fever")
        assertTrue(result.contains("Headache"))
        assertTrue(result.contains("Fever"))
    }

    @Test
    fun `duration with hours parsed correctly`() {
        val result = engine.parseJournalNote("I have cough for 6 hours")
        assertEquals(1, result.size)
        assertEquals(6, result[0].durationHours)
    }
}
