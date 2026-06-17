package com.nursyai.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.SymptomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY date DESC")
    fun observeCheckIns(userId: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY startedAt DESC")
    fun observeSymptoms(userId: String): Flow<List<SymptomEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId AND active = 1 ORDER BY name ASC")
    fun observeActiveMedications(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM daily_check_ins WHERE syncState != 'synced'")
    suspend fun pendingCheckIns(): List<DailyCheckInEntity>

    @Query("SELECT * FROM symptoms WHERE syncState != 'synced'")
    suspend fun pendingSymptoms(): List<SymptomEntity>

    @Query("SELECT * FROM medications WHERE syncState != 'synced'")
    suspend fun pendingMedications(): List<MedicationEntity>

    @Upsert
    suspend fun upsertCheckIn(checkIn: DailyCheckInEntity)

    @Upsert
    suspend fun upsertSymptom(symptom: SymptomEntity)

    @Upsert
    suspend fun upsertMedication(medication: MedicationEntity)
}
