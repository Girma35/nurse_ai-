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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun SymptomJournalScreen(viewModel: NursyViewModel) {
    var journalText by remember { mutableStateOf("") }
    var parsedSymptoms by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var hasParsed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Symptom Journal",
            color = NursyColors.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Describe how you feel naturally, and we'll extract the details.",
            color = NursyColors.inkMuted,
            fontSize = 14.sp
        )

        // Journal input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NursyColors.surface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = journalText,
                    onValueChange = { journalText = it; hasParsed = false },
                    label = { Text("How are you feeling?") },
                    placeholder = { Text("e.g., I feel tired and have headache for 3 days") },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (journalText.isNotBlank()) {
                            val extracted = viewModel.parseJournalNote(journalText)
                            parsedSymptoms = extracted.map { name ->
                                val severity = when (name.lowercase()) {
                                    "headache" -> 3
                                    "fever" -> 4
                                    "fatigue" -> 2
                                    "nausea" -> 3
                                    "cough" -> 2
                                    else -> 2
                                }
                                name.replaceFirstChar { it.uppercase() } to severity
                            }
                            hasParsed = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss),
                    enabled = journalText.isNotBlank()
                ) {
                    Text("Analyze", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Parsed results
        if (hasParsed) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NursyColors.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detected Symptoms",
                        color = NursyColors.ink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    if (parsedSymptoms.isEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No known symptoms detected. Try using words like headache, fever, fatigue, nausea, or cough.",
                            color = NursyColors.inkMuted,
                            fontSize = 14.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        parsedSymptoms.forEach { (name, severity) ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                color = NursyColors.cloud,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            color = NursyColors.ink,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Severity: $severity/5",
                                            color = NursyColors.inkMuted,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.saveParsedSymptoms(parsedSymptoms)
                                journalText = ""
                                parsedSymptoms = emptyList()
                                hasParsed = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss)
                        ) {
                            Text("Save All to Symptoms", fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                journalText = ""
                                parsedSymptoms = emptyList()
                                hasParsed = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Discard")
                        }
                    }
                }
            }
        }

        // Examples
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NursyColors.cloud,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Try these examples:",
                    color = NursyColors.ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "\"I feel tired and have headache for 3 days\"",
                    "\"Woke up with fever and cough\"",
                    "\"Mild nausea after eating\""
                ).forEach { example ->
                    Text(
                        text = example,
                        color = NursyColors.inkMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
