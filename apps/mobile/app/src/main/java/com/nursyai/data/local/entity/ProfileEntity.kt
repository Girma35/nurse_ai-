package com.nursyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val bloodType: String = "",
    val allergies: String = "",
    val chronicConditions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: String = "queued"
)
