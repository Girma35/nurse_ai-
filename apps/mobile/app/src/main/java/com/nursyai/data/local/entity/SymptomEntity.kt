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
    val durationHours: Int? = null,
    val notes: String? = null,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: String = "queued"
)
