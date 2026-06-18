package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nursyai.data.local.entity.SymptomEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.EmptyStateCard
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SecondaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SymptomTrackingScreen(viewModel: NursyViewModel) {
    val symptoms by viewModel.allSymptoms.collectAsState(initial = emptyList())

    var symptomName by rememberSaveable { mutableStateOf("") }
    var severity by rememberSaveable { mutableIntStateOf(3) }
    var duration by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    val activeCount = symptoms.count { it.active }
    val canSave = symptomName.isNotBlank()

    NursyScreen(
        eyebrow = "Symptom log",
        title = "Symptoms",
        subtitle = "Record what you feel now and keep a local history for patterns."
    ) {
        item {
            NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionTitle(title = "Current status", action = "${symptoms.size} total")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SymptomStatTile(
                        label = "Active",
                        value = activeCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    SymptomStatTile(
                        label = "Resolved",
                        value = (symptoms.size - activeCount).coerceAtLeast(0).toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Log a symptom")
                OutlinedTextField(
                    value = symptomName,
                    onValueChange = { symptomName = it },
                    label = { Text("Symptom name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Severity: $severity/5",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = severity.toFloat(),
                        onValueChange = { severity = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    suffix = { Text("hours") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    maxLines = 4
                )

                PrimaryActionButton(
                    text = "Add Symptom",
                    onClick = {
                        viewModel.addSymptom(
                            name = symptomName.trim(),
                            severity = severity,
                            durationHours = duration.toIntOrNull(),
                            notes = notes.ifBlank { null }
                        )
                        symptomName = ""
                        severity = 3
                        duration = ""
                        notes = ""
                    },
                    enabled = canSave
                )
            }
        }

        if (symptoms.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No symptoms logged",
                    message = "Use this screen or the journal to record symptoms when they appear."
                )
            }
        } else {
            item {
                SectionTitle(title = "History", action = "${activeCount} active")
            }
            items(symptoms, key = { it.id }) { symptom ->
                SymptomCard(
                    symptom = symptom,
                    onResolve = { viewModel.resolveSymptom(symptom.id) }
                )
            }
        }
    }
}

@Composable
private fun SymptomStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SymptomCard(symptom: SymptomEntity, onResolve: () -> Unit) {
    NursyCard(
        containerColor = if (symptom.active) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeverityIndicator(severity = symptom.severity, active = symptom.active)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = symptom.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StatusPill(
                        text = if (symptom.active) "Active" else "Resolved",
                        containerColor = if (symptom.active) NursyColors.amberSoft else NursyColors.mintSoft,
                        contentColor = if (symptom.active) NursyColors.ink else NursyColors.mossDark
                    )
                }
                Text(
                    text = "Severity ${symptom.severity}/5${symptom.durationHours?.let { ", ${it}h duration" } ?: ""}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = symptom.startedAt.asShortDate(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
                if (!symptom.notes.isNullOrBlank()) {
                    Text(
                        text = symptom.notes,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (symptom.active) {
                    SecondaryActionButton(
                        text = "Mark Resolved",
                        onClick = onResolve,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SeverityIndicator(severity: Int, active: Boolean) {
    Surface(
        modifier = Modifier.size(38.dp),
        color = if (!active) {
            NursyColors.cloud
        } else {
            when {
                severity >= 4 -> NursyColors.coralSoft
                severity >= 2 -> NursyColors.amberSoft
                else -> NursyColors.mintSoft
            }
        },
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = severity.toString(),
                color = when {
                    !active -> MaterialTheme.colorScheme.onSurfaceVariant
                    severity >= 4 -> NursyColors.coral
                    severity >= 2 -> NursyColors.ink
                    else -> NursyColors.moss
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun Long.asShortDate(): String {
    return SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(this))
}
