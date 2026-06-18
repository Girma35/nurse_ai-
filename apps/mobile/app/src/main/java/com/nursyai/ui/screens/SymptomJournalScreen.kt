package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun SymptomJournalScreen(viewModel: NursyViewModel) {
    var journalText by rememberSaveable { mutableStateOf("") }
    var parsedSymptoms by remember {
        mutableStateOf<List<NursyViewModel.ParsedSymptomResult>>(emptyList())
    }
    var hasParsed by rememberSaveable { mutableStateOf(false) }

    NursyScreen(
        eyebrow = "Local AI",
        title = "Symptom Journal",
        subtitle = "Write naturally. The on-device rules extract symptoms without cloud AI."
    ) {
        item {
            NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionTitle(title = "How it works", action = "Offline")
                Text(
                    text = "The phone checks your note for known symptom words and duration clues, then lets you review before saving.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Journal note")
                OutlinedTextField(
                    value = journalText,
                    onValueChange = {
                        journalText = it
                        hasParsed = false
                        parsedSymptoms = emptyList()
                    },
                    label = { Text("How are you feeling?") },
                    placeholder = { Text("I feel tired and have a headache for 3 days") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp),
                    maxLines = 7
                )
                PrimaryActionButton(
                    text = "Analyze Locally",
                    onClick = {
                        parsedSymptoms = viewModel.parseJournalNote(journalText)
                        hasParsed = true
                    },
                    enabled = journalText.isNotBlank()
                )
            }
        }

        if (hasParsed) {
            item {
                NursyCard {
                    SectionTitle(
                        title = "Detected symptoms",
                        action = "${parsedSymptoms.size} found"
                    )
                    if (parsedSymptoms.isEmpty()) {
                        Text(
                            text = "No known symptoms were detected. Try words like headache, fever, fatigue, nausea, cough, or pain.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            parsedSymptoms.forEach { symptom ->
                                ParsedSymptomRow(symptom)
                            }
                        }
                        PrimaryActionButton(
                            text = "Save to Symptoms",
                            onClick = {
                                viewModel.saveParsedSymptoms(parsedSymptoms)
                                journalText = ""
                                parsedSymptoms = emptyList()
                                hasParsed = false
                            }
                        )
                    }
                    SecondaryActionButton(
                        text = "Clear",
                        onClick = {
                            journalText = ""
                            parsedSymptoms = emptyList()
                            hasParsed = false
                        }
                    )
                }
            }
        }

        item {
            EmptyStateCard(
                title = "Example phrases",
                message = "\"Woke up with fever and cough\" or \"Mild nausea after eating\"."
            )
        }
    }
}

@Composable
private fun ParsedSymptomRow(symptom: NursyViewModel.ParsedSymptomResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusPill(
            text = "${symptom.severity}/5",
            containerColor = when {
                symptom.severity >= 4 -> NursyColors.coralSoft
                symptom.severity >= 2 -> NursyColors.amberSoft
                else -> NursyColors.mintSoft
            },
            contentColor = when {
                symptom.severity >= 4 -> NursyColors.coral
                symptom.severity >= 2 -> NursyColors.ink
                else -> NursyColors.mossDark
            }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = symptom.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = symptom.durationHours?.let { "$it hours mentioned" } ?: "No duration mentioned",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
