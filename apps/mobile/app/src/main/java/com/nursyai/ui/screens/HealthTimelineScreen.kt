package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.MedicationDoseEventEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.SymptomEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.EmptyStateCard
import com.nursyai.ui.components.MetricTile
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
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
    val groupedEvents = remember(events) { events.groupBy { it.date } }

    NursyScreen(
        eyebrow = "Local history",
        title = "Timeline",
        subtitle = "A chronological record of check-ins, symptoms, and medication activity."
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    label = "Check-ins",
                    value = checkIns.size.toString(),
                    detail = "Daily baselines",
                    modifier = Modifier.weight(1f),
                    accent = NursyColors.mint
                )
                MetricTile(
                    label = "Events",
                    value = events.size.toString(),
                    detail = "Local records",
                    modifier = Modifier.weight(1f),
                    accent = NursyColors.moss
                )
            }
        }

        if (events.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No timeline events yet",
                    message = "Your check-ins, symptom logs, and medication dose events will appear here."
                )
            }
        } else {
            groupedEvents.forEach { (date, dateEvents) ->
                item(key = "date-$date") {
                    SectionTitle(
                        title = date.asDisplayDate(),
                        action = "${dateEvents.size} events"
                    )
                }
                items(dateEvents, key = { it.id }) { event ->
                    TimelineEventCard(event)
                }
            }
        }
    }
}

@Composable
private fun TimelineEventCard(event: TimelineEvent) {
    NursyCard {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = event.time,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    modifier = Modifier.size(12.dp),
                    color = event.type.eventColor(),
                    shape = CircleShape
                ) {}
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.label,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StatusPill(
                        text = event.type.eventLabel(),
                        containerColor = event.type.eventSoftColor(),
                        contentColor = event.type.eventColor()
                    )
                }
                Text(
                    text = event.detail,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun buildTimelineEvents(
    checkIns: List<DailyCheckInEntity>,
    symptoms: List<SymptomEntity>,
    medications: List<MedicationEntity>,
    doseEvents: List<MedicationDoseEventEntity>
): List<TimelineEvent> {
    val events = mutableListOf<TimelineEvent>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    checkIns.forEach { checkIn ->
        events.add(
            TimelineEvent(
                id = "ci-${checkIn.id}",
                date = checkIn.date,
                time = "12:00",
                type = "checkin",
                label = "Check-in recorded",
                detail = "Mood ${checkIn.mood}, energy ${checkIn.energyLevel}/10, sleep ${checkIn.sleepHours.toString().removeSuffix(".0")}h"
            )
        )
    }

    symptoms.forEach { symptom ->
        val date = dateFormat.format(Date(symptom.startedAt))
        val time = timeFormat.format(Date(symptom.startedAt))
        events.add(
            TimelineEvent(
                id = "sym-${symptom.id}",
                date = date,
                time = time,
                type = "symptom",
                label = symptom.name,
                detail = "Severity ${symptom.severity}/5${symptom.durationHours?.let { ", ${it}h duration" } ?: ""}"
            )
        )
    }

    doseEvents.forEach { dose ->
        val date = dateFormat.format(Date(dose.scheduledTime))
        val time = timeFormat.format(Date(dose.scheduledTime))
        val medName = medications.find { it.id == dose.medicationId }?.name ?: "Medication"
        events.add(
            TimelineEvent(
                id = "dose-${dose.id}",
                date = date,
                time = time,
                type = "dose",
                label = medName,
                detail = "Dose marked ${dose.status}"
            )
        )
    }

    return events.sortedWith(
        compareByDescending<TimelineEvent> { it.date }.thenByDescending { it.time }
    )
}

@Composable
private fun String.eventColor() = when (this) {
    "checkin" -> NursyColors.moss
    "symptom" -> NursyColors.amber
    "dose" -> NursyColors.mint
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun String.eventSoftColor() = when (this) {
    "checkin" -> NursyColors.mintSoft
    "symptom" -> NursyColors.amberSoft
    "dose" -> NursyColors.mintSoft
    else -> MaterialTheme.colorScheme.surfaceVariant
}

private fun String.eventLabel() = when (this) {
    "checkin" -> "Check-In"
    "symptom" -> "Symptom"
    "dose" -> "Dose"
    else -> "Event"
}

private fun String.asDisplayDate(): String {
    val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val output = SimpleDateFormat("MMM d, yyyy", Locale.US)
    return runCatching { output.format(input.parse(this) ?: Date()) }.getOrDefault(this)
}
