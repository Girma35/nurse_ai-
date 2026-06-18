package com.nursyai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TimelineEvent(
    val id: String,
    val date: String,
    val time: String,
    val type: String,
    val label: String,
    val detail: String
)

@Composable
fun HealthTimelineScreen(viewModel: NursyViewModel) {
    val checkIns by viewModel.checkIns.collectAsState(initial = emptyList())
    val symptoms by viewModel.allSymptoms.collectAsState(initial = emptyList())
    val medications by viewModel.allMedications.collectAsState(initial = emptyList())
    val doseEvents by viewModel.doseEvents.collectAsState(initial = emptyList())

    val events = remember(checkIns, symptoms, medications, doseEvents) {
        buildTimelineEvents(checkIns, symptoms, medications, doseEvents)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Timeline",
                color = NursyColors.ink,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your health history",
                color = NursyColors.inkMuted,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (events.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = NursyColors.cloud,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No events yet",
                            color = NursyColors.ink,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Your check-ins, symptoms, and medication events will appear here.",
                            color = NursyColors.inkMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Group events by date
        val groupedEvents = events.groupBy { it.date }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        for ((date, dateEvents) in groupedEvents) {
            item {
                val displayDate = try {
                    displayFormat.format(dateFormat.parse(date) ?: Date())
                } catch (e: Exception) { date }

                Text(
                    text = displayDate,
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(dateEvents) { event ->
                TimelineEventCard(event)
            }
        }
    }
}

@Composable
private fun TimelineEventCard(event: TimelineEvent) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NursyColors.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            // Time column
            Text(
                text = event.time,
                color = NursyColors.inkMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(44.dp)
            )

            // Dot indicator
            val dotColor = when (event.type) {
                "checkin" -> NursyColors.mint
                "symptom" -> NursyColors.amber
                "medication", "dose" -> NursyColors.moss
                "insight" -> NursyColors.coral
                else -> NursyColors.inkMuted
            }
            Surface(
                modifier = Modifier.padding(top = 6.dp).size(8.dp),
                color = dotColor,
                shape = RoundedCornerShape(50)
            ) {}

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = event.label,
                    color = NursyColors.ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = event.detail,
                    color = NursyColors.inkMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun buildTimelineEvents(
    checkIns: List<com.nursyai.data.local.entity.DailyCheckInEntity>,
    symptoms: List<com.nursyai.data.local.entity.SymptomEntity>,
    medications: List<com.nursyai.data.local.entity.MedicationEntity>,
    doseEvents: List<com.nursyai.data.local.entity.MedicationDoseEventEntity>
): List<TimelineEvent> {
    val events = mutableListOf<TimelineEvent>()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    for (checkIn in checkIns) {
        events.add(
            TimelineEvent(
                id = "ci-${checkIn.id}",
                date = checkIn.date,
                time = "12:00",
                type = "checkin",
                label = "Check-in recorded",
                detail = "Mood: ${checkIn.mood}, Energy: ${checkIn.energyLevel}/10, Sleep: ${checkIn.sleepHours}h"
            )
        )
    }

    for (symptom in symptoms) {
        val date = try {
            dateFormat.format(Date(symptom.startedAt))
        } catch (e: Exception) { dateFormat.format(Date()) }
        val time = try {
            timeFormat.format(Date(symptom.startedAt))
        } catch (e: Exception) { "12:00" }
        events.add(
            TimelineEvent(
                id = "sym-${symptom.id}",
                date = date,
                time = time,
                type = "symptom",
                label = "Symptom: ${symptom.name}",
                detail = "Severity: ${symptom.severity}/5${symptom.durationHours?.let { ", ${it}h" } ?: ""}"
            )
        )
    }

    for (dose in doseEvents) {
        val date = try {
            dateFormat.format(Date(dose.scheduledTime))
        } catch (e: Exception) { dateFormat.format(Date()) }
        val time = try {
            timeFormat.format(Date(dose.scheduledTime))
        } catch (e: Exception) { "12:00" }
        val medName = medications.find { it.id == dose.medicationId }?.name ?: "Medication"
        events.add(
            TimelineEvent(
                id = "dose-${dose.id}",
                date = date,
                time = time,
                type = "dose",
                label = "Dose: ${dose.status.replaceFirstChar { it.uppercase() }}",
                detail = "${medName} — ${dose.status}"
            )
        )
    }

    events.sortWith(compareByDescending<TimelineEvent> { it.date }.thenByDescending { it.time })
    return events
}
