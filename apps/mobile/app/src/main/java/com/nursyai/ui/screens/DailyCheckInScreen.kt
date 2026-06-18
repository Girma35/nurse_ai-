package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(viewModel: NursyViewModel) {
    val savedCheckIn by viewModel.savedCheckIn.collectAsState()
    val latestCheckIn by viewModel.latestCheckIn.collectAsState()
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val existingCheckIn = latestCheckIn?.takeIf { it.date == today }

    var mood by rememberSaveable { mutableStateOf("steady") }
    var energyLevel by rememberSaveable { mutableIntStateOf(5) }
    var sleepHours by rememberSaveable { mutableStateOf("7") }
    var sleepQuality by rememberSaveable { mutableStateOf("fair") }
    var stressLevel by rememberSaveable { mutableIntStateOf(3) }
    var waterIntake by rememberSaveable { mutableStateOf("1000") }
    var notes by rememberSaveable { mutableStateOf("") }
    var loadedCheckInId by rememberSaveable { mutableStateOf<String?>(null) }
    var moodExpanded by remember { mutableStateOf(false) }
    var qualityExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(existingCheckIn?.id) {
        if (existingCheckIn != null && loadedCheckInId != existingCheckIn.id) {
            mood = existingCheckIn.mood
            energyLevel = existingCheckIn.energyLevel
            sleepHours = existingCheckIn.sleepHours.toString().trimDecimalZero()
            sleepQuality = existingCheckIn.sleepQuality
            stressLevel = existingCheckIn.stressLevel
            waterIntake = existingCheckIn.waterIntakeMl.toString()
            notes = existingCheckIn.notes.orEmpty()
            loadedCheckInId = existingCheckIn.id
        }
    }

    LaunchedEffect(savedCheckIn) {
        if (savedCheckIn) {
            viewModel.resetSavedCheckIn()
        }
    }

    val moodOptions = listOf("low", "down", "steady", "good", "great")
    val qualityOptions = listOf("poor", "fair", "good", "excellent")
    val canSave = sleepHours.toDoubleOrNull() != null && waterIntake.toIntOrNull() != null

    NursyScreen(
        eyebrow = "Daily baseline",
        title = "Check-In",
        subtitle = if (existingCheckIn == null) {
            "Capture today's mood, sleep, stress, and hydration offline."
        } else {
            "Update today's saved check-in whenever your day changes."
        }
    ) {
        item {
            NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionTitle(title = "Today", action = today)
                Text(
                    text = checkInSummary(existingCheckIn),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                StatusPill(
                    text = if (existingCheckIn == null) "Not saved yet" else "Saved locally",
                    containerColor = if (existingCheckIn == null) NursyColors.amberSoft else NursyColors.mintSoft,
                    contentColor = if (existingCheckIn == null) NursyColors.ink else NursyColors.mossDark
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Mood and energy")
                ExposedDropdownMenuBox(
                    expanded = moodExpanded,
                    onExpandedChange = { moodExpanded = !moodExpanded }
                ) {
                    OutlinedTextField(
                        value = mood.labelCase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mood") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = moodExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = moodExpanded,
                        onDismissRequest = { moodExpanded = false }
                    ) {
                        moodOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.labelCase()) },
                                onClick = {
                                    mood = option
                                    moodExpanded = false
                                }
                            )
                        }
                    }
                }

                LabeledSlider(
                    label = "Energy",
                    value = energyLevel,
                    valueRange = 1f..10f,
                    steps = 8,
                    suffix = "/10",
                    onValueChange = { energyLevel = it }
                )

                LabeledSlider(
                    label = "Stress",
                    value = stressLevel,
                    valueRange = 1f..10f,
                    steps = 8,
                    suffix = "/10",
                    onValueChange = { stressLevel = it }
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Sleep and hydration")
                OutlinedTextField(
                    value = sleepHours,
                    onValueChange = { sleepHours = it },
                    label = { Text("Sleep") },
                    suffix = { Text("hours") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = sleepHours.isNotBlank() && sleepHours.toDoubleOrNull() == null,
                    supportingText = {
                        Text("Use hours slept, for example 7.5")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = qualityExpanded,
                    onExpandedChange = { qualityExpanded = !qualityExpanded }
                ) {
                    OutlinedTextField(
                        value = sleepQuality.labelCase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sleep quality") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualityExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = qualityExpanded,
                        onDismissRequest = { qualityExpanded = false }
                    ) {
                        qualityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.labelCase()) },
                                onClick = {
                                    sleepQuality = option
                                    qualityExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = waterIntake,
                    onValueChange = { waterIntake = it },
                    label = { Text("Water intake") },
                    suffix = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = waterIntake.isNotBlank() && waterIntake.toIntOrNull() == null,
                    supportingText = {
                        Text("Track the water you remember drinking today.")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Notes")
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Anything important today?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp),
                    maxLines = 5
                )
                PrimaryActionButton(
                    text = if (existingCheckIn == null) "Save Check-In" else "Update Check-In",
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
                    enabled = canSave
                )
            }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    suffix: String,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "$label: $value$suffix",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            steps = steps
        )
    }
}

private fun checkInSummary(checkIn: DailyCheckInEntity?): String {
    return if (checkIn == null) {
        "No check-in has been saved for today."
    } else {
        "Mood ${checkIn.mood.labelCase()}, energy ${checkIn.energyLevel}/10, sleep ${checkIn.sleepHours.toString().trimDecimalZero()}h."
    }
}

private fun String.labelCase(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
}

private fun String.trimDecimalZero(): String = removeSuffix(".0")
