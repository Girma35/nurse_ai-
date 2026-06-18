package com.nursyai.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nursyai.ai.LocalRulesEngine
import com.nursyai.data.local.NursyDatabase
import com.nursyai.data.local.dao.HealthDao
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.EmergencyContactEntity
import com.nursyai.data.local.entity.MedicationDoseEventEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.ProfileEntity
import com.nursyai.data.local.entity.SymptomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class NursyViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: HealthDao
    private val rulesEngine = LocalRulesEngine()

    private val demoUserId = "demo-user"

    init {
        val db = NursyDatabase.getInstance(application)
        dao = db.healthDao()
    }

    // ─── Observable flows ──────────────────────────────────────

    val latestCheckIn: StateFlow<DailyCheckInEntity?> = dao
        .observeLatestCheckIn(demoUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val checkIns: Flow<List<DailyCheckInEntity>> = dao.observeCheckIns(demoUserId)

    val activeSymptoms: StateFlow<List<SymptomEntity>> = dao
        .observeActiveSymptoms(demoUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSymptoms: Flow<List<SymptomEntity>> = dao.observeSymptoms(demoUserId)

    val activeMedications: StateFlow<List<MedicationEntity>> = dao
        .observeActiveMedications(demoUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedications: Flow<List<MedicationEntity>> = dao.observeAllMedications(demoUserId)

    val doseEvents: Flow<List<MedicationDoseEventEntity>> = dao.observeDoseEvents(demoUserId)

    val profile: StateFlow<ProfileEntity?> = dao
        .observeProfile(demoUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val emergencyContacts: Flow<List<EmergencyContactEntity>> =
        dao.observeEmergencyContacts(demoUserId)

    // ─── Insights ──────────────────────────────────────────────

    val insights: StateFlow<List<String>> = combine(
        latestCheckIn,
        activeSymptoms
    ) { checkIn, symptoms ->
        rulesEngine.insights(checkIn, symptoms)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── Pending sync counts ───────────────────────────────────

    val pendingSyncCounts = MutableStateFlow(PendingSyncCounts())

    fun refreshPendingSyncCounts() {
        viewModelScope.launch {
            pendingSyncCounts.value = PendingSyncCounts(
                checkIns = dao.pendingCheckInCount(),
                symptoms = dao.pendingSymptomCount(),
                medications = dao.pendingMedicationCount()
            )
        }
    }

    // ─── Check-in actions ─────────────────────────────────────

    private val _savedCheckIn = MutableStateFlow(false)
    val savedCheckIn: StateFlow<Boolean> = _savedCheckIn.asStateFlow()

    fun saveCheckIn(
        mood: String,
        energyLevel: Int,
        sleepHours: Double,
        sleepQuality: String,
        stressLevel: Int,
        waterIntakeMl: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())
            val existing = dao.getCheckInByDate(demoUserId, today)
            val entity = DailyCheckInEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = demoUserId,
                date = today,
                mood = mood,
                energyLevel = energyLevel,
                sleepHours = sleepHours,
                sleepQuality = sleepQuality,
                stressLevel = stressLevel,
                waterIntakeMl = waterIntakeMl,
                notes = notes
            )
            dao.upsertCheckIn(entity)
            _savedCheckIn.value = true
            refreshPendingSyncCounts()
        }
    }

    fun resetSavedCheckIn() {
        _savedCheckIn.value = false
    }

    // ─── Symptom actions ───────────────────────────────────────

    fun addSymptom(
        name: String,
        severity: Int,
        durationHours: Int?,
        notes: String?
    ) {
        viewModelScope.launch {
            val entity = SymptomEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = name,
                severity = severity,
                startedAt = System.currentTimeMillis(),
                durationHours = durationHours,
                notes = notes
            )
            dao.upsertSymptom(entity)
            refreshPendingSyncCounts()
        }
    }

    fun resolveSymptom(symptomId: String) {
        viewModelScope.launch {
            dao.resolveSymptom(symptomId)
            refreshPendingSyncCounts()
        }
    }

    // ─── Medication actions ────────────────────────────────────

    fun addMedication(
        name: String,
        dose: String,
        frequency: String,
        scheduledTimesCsv: String
    ) {
        viewModelScope.launch {
            val entity = MedicationEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = name,
                dose = dose,
                frequency = frequency,
                scheduledTimesCsv = scheduledTimesCsv,
                takenCount = 0,
                missedCount = 0,
                active = true
            )
            dao.upsertMedication(entity)
            refreshPendingSyncCounts()
        }
    }

    fun deactivateMedication(medicationId: String) {
        viewModelScope.launch {
            dao.deactivateMedication(medicationId)
            refreshPendingSyncCounts()
        }
    }

    fun markDoseAsTaken(eventId: String) {
        viewModelScope.launch {
            dao.markDoseAsTaken(eventId)
        }
    }

    // ─── Profile actions ───────────────────────────────────────

    fun saveProfile(
        fullName: String,
        dateOfBirth: String,
        gender: String,
        weightKg: Double?,
        heightCm: Double?,
        bloodType: String,
        allergies: String,
        chronicConditions: String
    ) {
        viewModelScope.launch {
            val existing = dao.getProfile(demoUserId)
            val entity = ProfileEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = demoUserId,
                fullName = fullName,
                dateOfBirth = dateOfBirth,
                gender = gender,
                weightKg = weightKg,
                heightCm = heightCm,
                bloodType = bloodType,
                allergies = allergies,
                chronicConditions = chronicConditions
            )
            dao.upsertProfile(entity)
            refreshPendingSyncCounts()
        }
    }

    // ─── Emergency contact actions ─────────────────────────────

    fun addEmergencyContact(name: String, relationship: String, phoneNumber: String) {
        viewModelScope.launch {
            val entity = EmergencyContactEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = name,
                relationship = relationship,
                phoneNumber = phoneNumber
            )
            dao.upsertEmergencyContact(entity)
            refreshPendingSyncCounts()
        }
    }

    fun deleteEmergencyContact(contactId: String) {
        viewModelScope.launch {
            dao.deleteEmergencyContact(contactId)
            refreshPendingSyncCounts()
        }
    }

    // ─── Symptom journal parser ────────────────────────────────

    fun parseJournalNote(note: String): List<String> {
        return rulesEngine.extractSymptoms(note)
    }

    fun saveParsedSymptoms(
        symptoms: List<Pair<String, Int>>
    ) {
        viewModelScope.launch {
            for ((name, severity) in symptoms) {
                val entity = SymptomEntity(
                    id = UUID.randomUUID().toString(),
                    userId = demoUserId,
                    name = name,
                    severity = severity,
                    startedAt = System.currentTimeMillis()
                )
                dao.upsertSymptom(entity)
            }
            refreshPendingSyncCounts()
        }
    }

    // ─── Dose event scheduling ─────────────────────────────────

    fun scheduleDoseEventsForMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            val times = medication.scheduledTimesCsv.split(",")
            val todayStart = run {
                val cal = java.util.Calendar.getInstance()
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                cal.set(java.util.Calendar.MINUTE, 0)
                cal.set(java.util.Calendar.SECOND, 0)
                cal.timeInMillis
            }

            for (time in times) {
                val parts = time.trim().split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: continue
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val scheduledTime = todayStart + (hour * 3600000L) + (minute * 60000L)

                val entity = MedicationDoseEventEntity(
                    id = UUID.randomUUID().toString(),
                    userId = demoUserId,
                    medicationId = medication.id,
                    scheduledTime = scheduledTime,
                    status = "missed"
                )
                dao.upsertDoseEvent(entity)
            }
        }
    }
}

data class PendingSyncCounts(
    val checkIns: Int = 0,
    val symptoms: Int = 0,
    val medications: Int = 0
) {
    val total: Int get() = checkIns + symptoms + medications
}
