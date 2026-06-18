package com.nursyai.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.EmergencyContactEntity
import com.nursyai.data.local.entity.MedicationDoseEventEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.ProfileEntity
import com.nursyai.data.local.entity.SymptomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    // Daily Check-Ins
    @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY date DESC")
    fun observeCheckIns(userId: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_check_ins WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getCheckInByDate(userId: String, date: String): DailyCheckInEntity?

    @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun observeLatestCheckIn(userId: String): Flow<DailyCheckInEntity?>

    @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllCheckIns(userId: String): List<DailyCheckInEntity>

    @Upsert
    suspend fun upsertCheckIn(checkIn: DailyCheckInEntity)

    @Query("SELECT * FROM daily_check_ins WHERE syncState != 'synced'")
    suspend fun pendingCheckIns(): List<DailyCheckInEntity>

    @Query("SELECT COUNT(*) FROM daily_check_ins WHERE userId = :userId")
    suspend fun checkInCount(userId: String): Int

    // Symptoms
    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY startedAt DESC")
    fun observeSymptoms(userId: String): Flow<List<SymptomEntity>>

    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY startedAt DESC")
    suspend fun getAllSymptoms(userId: String): List<SymptomEntity>

    @Query("SELECT * FROM symptoms WHERE userId = :userId AND active = 1 ORDER BY startedAt DESC")
    fun observeActiveSymptoms(userId: String): Flow<List<SymptomEntity>>

    @Query("SELECT * FROM symptoms WHERE userId = :userId AND active = 1")
    suspend fun getActiveSymptoms(userId: String): List<SymptomEntity>

    @Query("SELECT * FROM symptoms WHERE id = :symptomId LIMIT 1")
    suspend fun getSymptomById(symptomId: String): SymptomEntity?

    @Upsert
    suspend fun upsertSymptom(symptom: SymptomEntity)

    @Query("UPDATE symptoms SET active = 0, updatedAt = :updatedAt WHERE id = :symptomId")
    suspend fun resolveSymptom(symptomId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM symptoms WHERE syncState != 'synced'")
    suspend fun pendingSymptoms(): List<SymptomEntity>

    // Medications
    @Query("SELECT * FROM medications WHERE userId = :userId AND active = 1 ORDER BY name ASC")
    fun observeActiveMedications(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY name ASC")
    fun observeAllMedications(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY name ASC")
    suspend fun getAllMedications(userId: String): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE id = :medicationId LIMIT 1")
    suspend fun getMedicationById(medicationId: String): MedicationEntity?

    @Upsert
    suspend fun upsertMedication(medication: MedicationEntity)

    @Query("UPDATE medications SET active = 0, updatedAt = :updatedAt WHERE id = :medicationId")
    suspend fun deactivateMedication(medicationId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM medications WHERE syncState != 'synced'")
    suspend fun pendingMedications(): List<MedicationEntity>

    // Medication Dose Events
    @Query("SELECT * FROM medication_dose_events WHERE userId = :userId ORDER BY scheduledTime DESC")
    fun observeDoseEvents(userId: String): Flow<List<MedicationDoseEventEntity>>

    @Query("SELECT * FROM medication_dose_events WHERE medicationId = :medicationId ORDER BY scheduledTime DESC")
    fun observeDoseEventsForMedication(medicationId: String): Flow<List<MedicationDoseEventEntity>>

    @Query("SELECT * FROM medication_dose_events WHERE medicationId = :medicationId ORDER BY scheduledTime DESC")
    suspend fun getDoseEventsForMedication(medicationId: String): List<MedicationDoseEventEntity>

    @Query("SELECT * FROM medication_dose_events WHERE medicationId = :medicationId AND status = 'taken'")
    suspend fun getTakenDoseEvents(medicationId: String): List<MedicationDoseEventEntity>

    @Query("SELECT * FROM medication_dose_events WHERE medicationId = :medicationId AND status = 'missed'")
    suspend fun getMissedDoseEvents(medicationId: String): List<MedicationDoseEventEntity>

    @Upsert
    suspend fun upsertDoseEvent(event: MedicationDoseEventEntity)

    @Query("""
        UPDATE medication_dose_events 
        SET status = 'taken', takenAt = :takenAt, updatedAt = :updatedAt 
        WHERE id = :eventId
    """)
    suspend fun markDoseAsTaken(eventId: String, takenAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE medication_dose_events 
        SET status = 'missed', updatedAt = :updatedAt 
        WHERE id = :eventId AND status != 'taken'
    """)
    suspend fun markDoseAsMissed(eventId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM medication_dose_events WHERE syncState != 'synced'")
    suspend fun pendingDoseEvents(): List<MedicationDoseEventEntity>

    // Profile
    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    fun observeProfile(userId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: String): ProfileEntity?

    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE syncState != 'synced'")
    suspend fun pendingProfiles(): List<ProfileEntity>

    // Emergency Contacts
    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY name ASC")
    fun observeEmergencyContacts(userId: String): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY name ASC")
    suspend fun getEmergencyContacts(userId: String): List<EmergencyContactEntity>

    @Upsert
    suspend fun upsertEmergencyContact(contact: EmergencyContactEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :contactId")
    suspend fun deleteEmergencyContact(contactId: String)

    @Query("SELECT * FROM emergency_contacts WHERE syncState != 'synced'")
    suspend fun pendingEmergencyContacts(): List<EmergencyContactEntity>

    // Sync status aggregated
    @Query("SELECT COUNT(*) FROM daily_check_ins WHERE syncState = 'queued'")
    suspend fun pendingCheckInCount(): Int

    @Query("SELECT COUNT(*) FROM symptoms WHERE syncState = 'queued'")
    suspend fun pendingSymptomCount(): Int

    @Query("SELECT COUNT(*) FROM medications WHERE syncState = 'queued'")
    suspend fun pendingMedicationCount(): Int
}
