package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nursyai.data.local.entity.EmergencyContactEntity
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
fun EmergencyCardScreen(viewModel: NursyViewModel) {
    val profile by viewModel.profile.collectAsState()
    val contacts by viewModel.emergencyContacts.collectAsState(initial = emptyList())

    var name by rememberSaveable { mutableStateOf("") }
    var relationship by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    val hasCriticalInfo = profile?.let {
        it.fullName.isNotBlank() ||
            it.bloodType.isNotBlank() ||
            it.allergies.isNotBlank() ||
            it.chronicConditions.isNotBlank()
    } == true
    val canSaveContact = name.isNotBlank() && relationship.isNotBlank() && phone.isNotBlank()

    NursyScreen(
        eyebrow = "Offline safety",
        title = "Emergency Card",
        subtitle = "Critical profile details and contacts stay visible without internet."
    ) {
        item {
            EmergencySummaryCard(
                fullName = profile?.fullName.orEmpty(),
                bloodType = profile?.bloodType.orEmpty(),
                allergies = profile?.allergies.orEmpty(),
                conditions = profile?.chronicConditions.orEmpty(),
                hasCriticalInfo = hasCriticalInfo,
                contactCount = contacts.size
            )
        }

        item {
            NursyCard {
                SectionTitle(title = "Add contact")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                PrimaryActionButton(
                    text = "Add Contact",
                    onClick = {
                        viewModel.addEmergencyContact(
                            name = name.trim(),
                            relationship = relationship.trim(),
                            phoneNumber = phone.trim()
                        )
                        name = ""
                        relationship = ""
                        phone = ""
                    },
                    enabled = canSaveContact,
                    containerColor = NursyColors.coral
                )
            }
        }

        if (contacts.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No emergency contacts",
                    message = "Add at least one trusted contact for quick offline access."
                )
            }
        } else {
            item {
                SectionTitle(title = "Emergency contacts", action = "${contacts.size} saved")
            }
            items(contacts, key = { it.id }) { contact ->
                EmergencyContactCard(
                    contact = contact,
                    onDelete = { viewModel.deleteEmergencyContact(contact.id) }
                )
            }
        }
    }
}

@Composable
private fun EmergencySummaryCard(
    fullName: String,
    bloodType: String,
    allergies: String,
    conditions: String,
    hasCriticalInfo: Boolean,
    contactCount: Int
) {
    NursyCard(containerColor = NursyColors.coralSoft) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = fullName.ifBlank { "Emergency profile" },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hasCriticalInfo) {
                        "Available offline for urgent reference."
                    } else {
                        "Complete your profile to fill this card."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusPill(
                text = "$contactCount contacts",
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NursyColors.coral
            )
        }

        EmergencyInfoRow(label = "Blood type", value = bloodType.ifBlank { "Not set" })
        EmergencyInfoRow(label = "Allergies", value = allergies.ifBlank { "Not set" })
        EmergencyInfoRow(label = "Conditions", value = conditions.ifBlank { "Not set" })
    }
}

@Composable
private fun EmergencyInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.38f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.62f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContactEntity,
    onDelete: () -> Unit
) {
    NursyCard {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                color = NursyColors.coralSoft,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = NursyColors.coral,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = contact.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = contact.relationship,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = contact.phoneNumber,
                    color = NursyColors.moss,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                SecondaryActionButton(
                    text = "Remove",
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
