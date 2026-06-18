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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.data.local.entity.SymptomEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun SymptomTrackingScreen(viewModel: NursyViewModel) {
    val symptoms by viewModel.allSymptoms.collectAsState()

    var symptomName by remember { mutableStateOf("") }
    var severity by remember { mutableIntStateOf(3) }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Symptom Tracking",
                color = NursyColors.ink,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Add symptom form
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NursyColors.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Log a Symptom",
                        color = NursyColors.ink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = symptomName,
                        onValueChange = { symptomName = it },
                        label = { Text("Symptom Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Severity: $severity/5",
                        color = NursyColors.ink,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Slider(
                        value = severity.toFloat(),
                        onValueChange = { severity = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (hours, optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (symptomName.isNotBlank()) {
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
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss),
                        enabled = symptomName.isNotBlank()
                    ) {
                        Text("Add Symptom", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Active symptoms header
        if (symptoms.isNotEmpty()) {
            item {
                Text(
                    text = "Symptom History",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(symptoms) { symptom ->
                SymptomCard(
                    symptom = symptom,
                    onResolve = { viewModel.resolveSymptom(symptom.id) }
                )
            }
        }

        // Empty state
        if (symptoms.isEmpty()) {
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
                            text = "No symptoms logged yet",
                            color = NursyColors.ink,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Use the form above or the journal feature to track your symptoms.",
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
private fun SymptomCard(symptom: SymptomEntity, onResolve: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (symptom.active) NursyColors.surface else NursyColors.cloud,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = symptom.name,
                        color = NursyColors.ink,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!symptom.active) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Resolved)",
                            color = NursyColors.inkMuted,
                            fontSize = 12.sp
                        )
                    }
                }
                Text(
                    text = "Severity: ${symptom.severity}/5${symptom.durationHours?.let { " · ${it}h duration" } ?: ""}",
                    color = NursyColors.inkMuted,
                    fontSize = 13.sp
                )
                symptom.notes?.let {
                    Text(
                        text = it,
                        color = NursyColors.inkMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (symptom.active) {
                OutlinedButton(
                    onClick = onResolve,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Resolve", fontSize = 13.sp)
                }
            }
        }
    }
}
