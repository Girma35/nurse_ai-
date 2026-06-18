package com.nursyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_dose_events")
data class MedicationDoseEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val medicationId: String,
    val scheduledTime: Long,
    val takenAt: Long? = null,
    val status: String = "missed",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: String = "queued"
)
