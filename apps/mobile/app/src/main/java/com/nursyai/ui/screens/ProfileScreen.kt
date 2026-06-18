package com.nursyai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun ProfileScreen(viewModel: NursyViewModel) {
    val profile by viewModel.profile.collectAsState()

    var fullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var dateOfBirth by remember { mutableStateOf(profile?.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(profile?.gender ?: "") }
    var weightKg by remember { mutableStateOf(profile?.weightKg?.toString() ?: "") }
    var heightCm by remember { mutableStateOf(profile?.heightCm?.toString() ?: "") }
    var bloodType by remember { mutableStateOf(profile?.bloodType ?: "") }
    var allergies by remember { mutableStateOf(profile?.allergies ?: "") }
    var chronicConditions by remember { mutableStateOf(profile?.chronicConditions ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Profile",
            color = NursyColors.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your health profile",
            color = NursyColors.inkMuted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NursyColors.surface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bloodType,
                    onValueChange = { bloodType = it },
                    label = { Text("Blood Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = chronicConditions,
                    onValueChange = { chronicConditions = it },
                    label = { Text("Chronic Conditions (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.saveProfile(
                            fullName = fullName,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            weightKg = weightKg.toDoubleOrNull(),
                            heightCm = heightCm.toDoubleOrNull(),
                            bloodType = bloodType,
                            allergies = allergies,
                            chronicConditions = chronicConditions
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NursyColors.moss)
                ) {
                    Text("Save Profile", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
