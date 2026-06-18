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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun EmergencyCardScreen(viewModel: NursyViewModel) {
    val profile by viewModel.profile.collectAsState()
    val contacts by viewModel.emergencyContacts.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Emergency Card",
            color = NursyColors.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Critical health info — available offline",
            color = NursyColors.coral,
            fontSize = 14.sp
        )

        // Emergency Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NursyColors.coralSoft,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(12.dp),
                        color = NursyColors.coral,
                        shape = RoundedCornerShape(50)
                    ) {}
                    Text(
                        text = " Emergency Card",
                        color = NursyColors.coral,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (profile != null || contacts.isNotEmpty()) {
                    // Profile info
                    profile?.let { p ->
                        if (p.fullName.isNotBlank()) {
                            Text(
                                text = p.fullName,
                                color = NursyColors.ink,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (p.bloodType.isNotBlank()) {
                            InfoRow("Blood Type", p.bloodType)
                        }
                        if (p.allergies.isNotBlank()) {
                            InfoRow("Allergies", p.allergies)
                        }
                        if (p.chronicConditions.isNotBlank()) {
                            InfoRow("Conditions", p.chronicConditions)
                        }
                    }

                    // Emergency contacts
                    if (contacts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Emergency Contacts",
                            color = NursyColors.ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        contacts.forEach { contact ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = contact.name, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = contact.relationship,
                                        color = NursyColors.inkMuted,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = contact.phoneNumber,
                                        color = NursyColors.moss,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Complete your profile and add emergency contacts to see them here.",
                        color = NursyColors.inkMuted,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Add emergency contact form
        var name by remember { mutableStateOf("") }
        var relationship by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NursyColors.surface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add Emergency Contact",
                    color = NursyColors.ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        if (name.isNotBlank() && relationship.isNotBlank() && phone.isNotBlank()) {
                            viewModel.addEmergencyContact(name, relationship, phone)
                            name = ""
                            relationship = ""
                            phone = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = NursyColors.coral
                    ),
                    enabled = name.isNotBlank() && relationship.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Add Contact", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            color = NursyColors.inkMuted,
            fontSize = 14.sp,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            color = NursyColors.ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
