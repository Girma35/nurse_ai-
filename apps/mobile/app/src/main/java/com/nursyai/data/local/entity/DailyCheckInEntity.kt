package com.nursyai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_check_ins")
data class DailyCheckInEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val date: String,
    val mood: String,
    val energyLevel: Int,
    val sleepHours: Double,
    val sleepQuality: String,
    val stressLevel: Int,
    val waterIntakeMl: Int,
    val notes: String?,
    val syncState: String = "queued",
    val updatedAt: Long = System.currentTimeMillis()
)
