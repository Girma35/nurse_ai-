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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun MedicationManagementScreen(viewModel: NursyViewModel) {
    val medications by viewModel.allMedications.collectAsState()

    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var scheduledTimes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Medications",
                color = NursyColors.ink,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Add medication form
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NursyColors.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Medication",
                        color = NursyColors.ink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Medication Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dose,
                        onValueChange = { dose = it },
                        label = { Text("Dose (e.g., 65 mg)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text("Frequency (e.g., Daily)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = scheduledTimes,
                        onValueChange = { scheduledTimes = it },
                        label = { Text("Schedule (comma-separated, e.g., 08:00,20:00)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && dose.isNotBlank()) {
                                viewModel.addMedication(
                                    name = name.trim(),
                                    dose = dose.trim(),
                                    frequency = frequency.ifBlank { "Daily" },
                                    scheduledTimesCsv = scheduledTimes
                                )
                                name = ""
                                dose = ""
                                frequency = ""
                                scheduledTimes = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss),
                        enabled = name.isNotBlank() && dose.isNotBlank()
                    ) {
                        Text("Add Medication", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Medication list
        if (medications.isNotEmpty()) {
            item {
                Text(
                    text = "Your Medications",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(medications) { medication ->
                MedicationCard(
                    medication = medication,
                    onDeactivate = { viewModel.deactivateMedication(medication.id) },
                    onScheduleDoses = { viewModel.scheduleDoseEventsForMedication(medication) }
                )
            }
        }

        // Empty state
        if (medications.isEmpty()) {
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
                            text = "No medications added",
                            color = NursyColors.ink,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Add your medications to track doses and get reminders.",
                            color = NursyColors.inkMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(
    medication: MedicationEntity,
    onDeactivate: () -> Unit,
    onScheduleDoses: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (medication.active) NursyColors.surface else NursyColors.cloud,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        color = NursyColors.ink,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${medication.dose} · ${medication.frequency}",
                        color = NursyColors.inkMuted,
                        fontSize = 13.sp
                    )
                }
                if (!medication.active) {
                    Text(
                        text = "Inactive",
                        color = NursyColors.inkMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (medication.active) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Taken: ${medication.takenCount} · Missed: ${medication.missedCount}",
                    color = NursyColors.inkMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onScheduleDoses,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Schedule Doses", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDeactivate,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Deactivate", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
