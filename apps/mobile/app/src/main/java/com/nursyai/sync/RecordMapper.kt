package com.nursyai.sync

import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.EmergencyContactEntity
import com.nursyai.data.local.entity.MedicationDoseEventEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.ProfileEntity
import com.nursyai.data.local.entity.SymptomEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.Serializable

/**
 * Maps Room entities to DynamoDB-compatible sync payloads.
 * Follows the single-table key pattern: PK=USER#<userId>, SK=<ENTITY>#<timestamp>#<entityId>
 */

@Serializable
data class CheckInSyncPayload(
    val mood: String,
    val energyLevel: Int,
    val sleepHours: Double,
    val sleepQuality: String,
    val stressLevel: Int,
    val waterIntakeMl: Int,
    val notes: String?,
    val date: String
)

@Serializable
data class SymptomSyncPayload(
    val name: String,
    val severity: Int,
    val startedAt: Long,
    val durationHours: Int?,
    val notes: String?,
    val active: Boolean
)

@Serializable
data class MedicationSyncPayload(
    val name: String,
    val dose: String,
    val frequency: String,
    val scheduledTimesCsv: String,
    val takenCount: Int,
    val missedCount: Int,
    val active: Boolean
)

@Serializable
data class ProfileSyncPayload(
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val weightKg: Double?,
    val heightCm: Double?,
    val bloodType: String,
    val allergies: String,
    val chronicConditions: String
)

@Serializable
data class EmergencyContactSyncPayload(
    val name: String,
    val relationship: String,
    val phoneNumber: String
)

@Serializable
data class DoseEventSyncPayload(
    val medicationId: String,
    val scheduledTime: Long,
    val takenAt: Long?,
    val status: String
)

object RecordMapper {
    private val json = Json { encodeDefaults = true }

    fun checkInToPayload(checkIn: DailyCheckInEntity): SyncPayload {
        val payload = CheckInSyncPayload(
            mood = checkIn.mood,
            energyLevel = checkIn.energyLevel,
            sleepHours = checkIn.sleepHours,
            sleepQuality = checkIn.sleepQuality,
            stressLevel = checkIn.stressLevel,
            waterIntakeMl = checkIn.waterIntakeMl,
            notes = checkIn.notes,
            date = checkIn.date
        )
        return SyncApiClient.createSyncPayload(
            userId = checkIn.userId,
            entityType = "checkIn",
            entityId = checkIn.id,
            payloadData = json.encodeToString(CheckInSyncPayload.serializer(), payload),
            updatedAt = checkIn.updatedAt
        )
    }

    fun symptomToPayload(symptom: SymptomEntity): SyncPayload {
        val payload = SymptomSyncPayload(
            name = symptom.name,
            severity = symptom.severity,
            startedAt = symptom.startedAt,
            durationHours = symptom.durationHours,
            notes = symptom.notes,
            active = symptom.active
        )
        return SyncApiClient.createSyncPayload(
            userId = symptom.userId,
            entityType = "symptom",
            entityId = symptom.id,
            payloadData = json.encodeToString(SymptomSyncPayload.serializer(), payload),
            updatedAt = symptom.updatedAt
        )
    }

    fun medicationToPayload(medication: MedicationEntity): SyncPayload {
        val payload = MedicationSyncPayload(
            name = medication.name,
            dose = medication.dose,
            frequency = medication.frequency,
            scheduledTimesCsv = medication.scheduledTimesCsv,
            takenCount = medication.takenCount,
            missedCount = medication.missedCount,
            active = medication.active
        )
        return SyncApiClient.createSyncPayload(
            userId = medication.userId,
            entityType = "medication",
            entityId = medication.id,
            payloadData = json.encodeToString(MedicationSyncPayload.serializer(), payload),
            updatedAt = medication.updatedAt
        )
    }

    fun profileToPayload(profile: ProfileEntity): SyncPayload {
        val payload = ProfileSyncPayload(
            fullName = profile.fullName,
            dateOfBirth = profile.dateOfBirth,
            gender = profile.gender,
            weightKg = profile.weightKg,
            heightCm = profile.heightCm,
            bloodType = profile.bloodType,
            allergies = profile.allergies,
            chronicConditions = profile.chronicConditions
        )
        return SyncApiClient.createSyncPayload(
            userId = profile.userId,
            entityType = "profile",
            entityId = profile.id,
            payloadData = json.encodeToString(ProfileSyncPayload.serializer(), payload),
            updatedAt = profile.updatedAt
        )
    }

    fun emergencyContactToPayload(contact: EmergencyContactEntity): SyncPayload {
        val payload = EmergencyContactSyncPayload(
            name = contact.name,
            relationship = contact.relationship,
            phoneNumber = contact.phoneNumber
        )
        return SyncApiClient.createSyncPayload(
            userId = contact.userId,
            entityType = "emergencyContact",
            entityId = contact.id,
            payloadData = json.encodeToString(EmergencyContactSyncPayload.serializer(), payload),
            updatedAt = contact.updatedAt
        )
    }

    fun doseEventToPayload(event: MedicationDoseEventEntity): SyncPayload {
        val payload = DoseEventSyncPayload(
            medicationId = event.medicationId,
            scheduledTime = event.scheduledTime,
            takenAt = event.takenAt,
            status = event.status
        )
        return SyncApiClient.createSyncPayload(
            userId = event.userId,
            entityType = "doseEvent",
            entityId = event.id,
            payloadData = json.encodeToString(DoseEventSyncPayload.serializer(), payload),
            updatedAt = event.updatedAt
        )
    }
}
