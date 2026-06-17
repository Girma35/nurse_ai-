package com.nursyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val dose: String,
    val frequency: String,
    val scheduledTimesCsv: String,
    val takenCount: Int,
    val missedCount: Int,
    val active: Boolean,
    val syncState: String = "queued",
    val updatedAt: Long = System.currentTimeMillis()
)
