package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors

@Composable
fun ProfileScreen(viewModel: NursyViewModel) {
    val profile by viewModel.profile.collectAsState()

    var fullName by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var weightKg by rememberSaveable { mutableStateOf("") }
    var heightCm by rememberSaveable { mutableStateOf("") }
    var bloodType by rememberSaveable { mutableStateOf("") }
    var allergies by rememberSaveable { mutableStateOf("") }
    var chronicConditions by rememberSaveable { mutableStateOf("") }
    var loadedProfileId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(profile?.id) {
        val current = profile
        if (current != null && loadedProfileId != current.id) {
            fullName = current.fullName
            dateOfBirth = current.dateOfBirth
            gender = current.gender
            weightKg = current.weightKg?.toString().orEmpty()
            heightCm = current.heightCm?.toString().orEmpty()
            bloodType = current.bloodType
            allergies = current.allergies
            chronicConditions = current.chronicConditions
            loadedProfileId = current.id
        }
    }

    NursyScreen(
        eyebrow = "Identity and safety",
        title = "Profile",
        subtitle = "Store the health details Nursy AI uses for offline context and emergency display."
    ) {
        item {
            NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionTitle(title = "Profile status")
                Text(
                    text = if (profile == null) {
                        "No profile saved yet. Add basic information to personalize local context."
                    } else {
                        "Saved locally for offline use and queued for backup when sync is available."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                StatusPill(
                    text = if (profile == null) "Incomplete" else "Saved locally",
                    containerColor = if (profile == null) NursyColors.amberSoft else NursyColors.mintSoft,
                    contentColor = if (profile == null) NursyColors.ink else NursyColors.mossDark
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Basic information")
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of birth") },
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Measurements")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        label = { Text("Weight") },
                        suffix = { Text("kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = { Text("Height") },
                        suffix = { Text("cm") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Medical details")
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = { bloodType = it },
                    label = { Text("Blood type") },
                    placeholder = { Text("O+") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies") },
                    placeholder = { Text("Penicillin, peanuts") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = chronicConditions,
                    onValueChange = { chronicConditions = it },
                    label = { Text("Chronic conditions") },
                    placeholder = { Text("Asthma, diabetes") },
                    modifier = Modifier.fillMaxWidth()
                )
                PrimaryActionButton(
                    text = "Save Profile",
                    onClick = {
                        viewModel.saveProfile(
                            fullName = fullName.trim(),
                            dateOfBirth = dateOfBirth.trim(),
                            gender = gender.trim(),
                            weightKg = weightKg.toDoubleOrNull(),
                            heightCm = heightCm.toDoubleOrNull(),
                            bloodType = bloodType.trim(),
                            allergies = allergies.trim(),
                            chronicConditions = chronicConditions.trim()
                        )
                    }
                )
            }
        }
    }
}
