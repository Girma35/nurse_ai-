package com.nursyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptoms")
data class SymptomEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val severity: Int,
    val startedAt: Long,
    val durationHours: Int?,
    val notes: String?,
    val syncState: String = "queued",
    val updatedAt: Long = System.currentTimeMillis()
)
