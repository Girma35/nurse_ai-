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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(viewModel: NursyViewModel) {
    val savedCheckIn by viewModel.savedCheckIn.collectAsState()
    val latestCheckIn by viewModel.latestCheckIn.collectAsState()

    var mood by remember { mutableStateOf("steady") }
    var energyLevel by remember { mutableIntStateOf(5) }
    var sleepHours by remember { mutableStateOf("7") }
    var sleepQuality by remember { mutableStateOf("fair") }
    var stressLevel by remember { mutableIntStateOf(3) }
    var waterIntake by remember { mutableStateOf("1000") }
    var notes by remember { mutableStateOf("") }

    // Pre-fill from today's existing check-in
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val existingCheckIn = latestCheckIn?.takeIf { it.date == today }
    if (existingCheckIn != null && mood == "steady" && energyLevel == 5) {
        mood = existingCheckIn.mood
        energyLevel = existingCheckIn.energyLevel
        sleepHours = existingCheckIn.sleepHours.toInt().toString()
        sleepQuality = existingCheckIn.sleepQuality
        stressLevel = existingCheckIn.stressLevel
        waterIntake = existingCheckIn.waterIntakeMl.toString()
        notes = existingCheckIn.notes ?: ""
    }

    var moodExpanded by remember { mutableStateOf(false) }
    var qualityExpanded by remember { mutableStateOf(false) }

    val moodOptions = listOf("low", "down", "steady", "good", "great")
    val qualityOptions = listOf("poor", "fair", "good", "excellent")

    if (savedCheckIn) {
        viewModel.resetSavedCheckIn()
        mood = "steady"
        energyLevel = 5
        sleepHours = "7"
        sleepQuality = "fair"
        stressLevel = 3
        waterIntake = "1000"
        notes = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Daily Check-In",
            color = NursyColors.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = today,
            color = NursyColors.inkMuted,
            fontSize = 14.sp
        )

        // Mood
        ExposedDropdownMenuBox(
            expanded = moodExpanded,
            onExpandedChange = { moodExpanded = !moodExpanded }
        ) {
            OutlinedTextField(
                value = mood,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mood") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = moodExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = moodExpanded, onDismissRequest = { moodExpanded = false }) {
                moodOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = { mood = option; moodExpanded = false }
                    )
                }
            }
        }

        // Energy Level
        Text("Energy Level: $energyLevel/10", color = NursyColors.ink, fontWeight = FontWeight.Medium)
        Slider(
            value = energyLevel.toFloat(),
            onValueChange = { energyLevel = it.toInt() },
            valueRange = 1f..10f,
            steps = 8
        )

        // Sleep Hours
        OutlinedTextField(
            value = sleepHours,
            onValueChange = { sleepHours = it },
            label = { Text("Sleep Hours") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Sleep Quality
        ExposedDropdownMenuBox(
            expanded = qualityExpanded,
            onExpandedChange = { qualityExpanded = !qualityExpanded }
        ) {
            OutlinedTextField(
                value = sleepQuality,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sleep Quality") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualityExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = qualityExpanded, onDismissRequest = { qualityExpanded = false }) {
                qualityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = { sleepQuality = option; qualityExpanded = false }
                    )
                }
            }
        }

        // Stress Level
        Text("Stress Level: $stressLevel/10", color = NursyColors.ink, fontWeight = FontWeight.Medium)
        Slider(
            value = stressLevel.toFloat(),
            onValueChange = { stressLevel = it.toInt() },
            valueRange = 1f..10f,
            steps = 8
        )

        // Water Intake
        OutlinedTextField(
            value = waterIntake,
            onValueChange = { waterIntake = it },
            label = { Text("Water Intake (ml)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.saveCheckIn(
                    mood = mood,
                    energyLevel = energyLevel,
                    sleepHours = sleepHours.toDoubleOrNull() ?: 7.0,
                    sleepQuality = sleepQuality,
                    stressLevel = stressLevel,
                    waterIntakeMl = waterIntake.toIntOrNull() ?: 1000,
                    notes = notes.ifBlank { null }
                )
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss)
        ) {
            Text("Save Check-In", fontWeight = FontWeight.SemiBold)
        }

        if (savedCheckIn) {
            Text(
                text = "✓ Check-in saved successfully!",
                color = NursyColors.moss,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
