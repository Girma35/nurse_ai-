package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.EmptyStateCard
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SecondaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors

@Composable
fun MedicationManagementScreen(viewModel: NursyViewModel) {
    val medications by viewModel.allMedications.collectAsState(initial = emptyList())

    var name by rememberSaveable { mutableStateOf("") }
    var dose by rememberSaveable { mutableStateOf("") }
    var frequency by rememberSaveable { mutableStateOf("") }
    var scheduledTimes by rememberSaveable { mutableStateOf("") }

    val activeCount = medications.count { it.active }
    val canSave = name.isNotBlank() && dose.isNotBlank()

    NursyScreen(
        eyebrow = "Medication plan",
        title = "Medications",
        subtitle = "Keep medication details and reminder schedules available on this phone."
    ) {
        item {
            NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionTitle(title = "Overview", action = "${medications.size} total")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MedicationStatTile(
                        label = "Active",
                        value = activeCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MedicationStatTile(
                        label = "Inactive",
                        value = (medications.size - activeCount).coerceAtLeast(0).toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Add medication")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it },
                    label = { Text("Dose") },
                    placeholder = { Text("65 mg") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency") },
                    placeholder = { Text("Daily") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = scheduledTimes,
                    onValueChange = { scheduledTimes = it },
                    label = { Text("Schedule") },
                    placeholder = { Text("08:00, 20:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    supportingText = {
                        Text("Use 24-hour times separated by commas.")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryActionButton(
                    text = "Add Medication",
                    onClick = {
                        viewModel.addMedication(
                            name = name.trim(),
                            dose = dose.trim(),
                            frequency = frequency.ifBlank { "Daily" },
                            scheduledTimesCsv = scheduledTimes.ifBlank { "08:00" }
                        )
                        name = ""
                        dose = ""
                        frequency = ""
                        scheduledTimes = ""
                    },
                    enabled = canSave
                )
            }
        }

        if (medications.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No medications added",
                    message = "Add medications to track doses locally and prepare reminder events."
                )
            }
        } else {
            item {
                SectionTitle(title = "Medication list", action = "$activeCount active")
            }
            items(medications, key = { it.id }) { medication ->
                MedicationCard(
                    medication = medication,
                    onDeactivate = { viewModel.deactivateMedication(medication.id) },
                    onScheduleDoses = { viewModel.scheduleDoseEventsForMedication(medication) }
                )
            }
        }
    }
}

@Composable
private fun MedicationStatTile(
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
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun MedicationCard(
    medication: MedicationEntity,
    onDeactivate: () -> Unit,
    onScheduleDoses: () -> Unit
) {
    val total = medication.takenCount + medication.missedCount
    val adherence = if (total == 0) null else medication.takenCount * 100 / total

    NursyCard(
        containerColor = if (medication.active) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                color = if (medication.active) NursyColors.mintSoft else NursyColors.cloud,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Rx",
                        color = if (medication.active) NursyColors.moss else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = medication.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StatusPill(
                        text = if (medication.active) "Active" else "Inactive",
                        containerColor = if (medication.active) NursyColors.mintSoft else NursyColors.cloud,
                        contentColor = if (medication.active) NursyColors.mossDark else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${medication.dose} - ${medication.frequency}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Schedule: ${medication.scheduledTimesCsv.ifBlank { "Not set" }}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (adherence == null) {
                        "No dose history yet"
                    } else {
                        "Taken ${medication.takenCount}, missed ${medication.missedCount} - $adherence% adherence"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
                if (medication.active) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SecondaryActionButton(
                            text = "Schedule",
                            onClick = onScheduleDoses,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryActionButton(
                            text = "Deactivate",
                            onClick = onDeactivate,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
