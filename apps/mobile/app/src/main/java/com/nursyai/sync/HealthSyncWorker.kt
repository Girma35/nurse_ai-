package com.nursyai.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nursyai.data.local.NursyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HealthSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val db = NursyDatabase.getInstance(appContext)
    private val dao = db.healthDao()
    private val apiClient = SyncApiClient()

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting sync worker...")

        return try {
            val pendingCheckIns = dao.pendingCheckIns()
            val pendingSymptoms = dao.pendingSymptoms()
            val pendingMedications = dao.pendingMedications()
            val pendingProfiles = dao.pendingProfiles()
            val pendingContacts = dao.pendingEmergencyContacts()
            val pendingDoseEvents = dao.pendingDoseEvents()

            val totalPending = pendingCheckIns.size + pendingSymptoms.size +
                pendingMedications.size + pendingProfiles.size +
                pendingContacts.size + pendingDoseEvents.size

            if (totalPending == 0) {
                Log.d(TAG, "No pending records to sync.")
                return Result.success()
            }

            Log.d(TAG, "Syncing $totalPending records...")

            // Collect all sync payloads
            val syncPayloads = mutableListOf<SyncPayload>()

            for (checkIn in pendingCheckIns) {
                syncPayloads.add(RecordMapper.checkInToPayload(checkIn))
            }
            for (symptom in pendingSymptoms) {
                syncPayloads.add(RecordMapper.symptomToPayload(symptom))
            }
            for (medication in pendingMedications) {
                syncPayloads.add(RecordMapper.medicationToPayload(medication))
            }
            for (profile in pendingProfiles) {
                syncPayloads.add(RecordMapper.profileToPayload(profile))
            }
            for (contact in pendingContacts) {
                syncPayloads.add(RecordMapper.emergencyContactToPayload(contact))
            }
            for (event in pendingDoseEvents) {
                syncPayloads.add(RecordMapper.doseEventToPayload(event))
            }

            // Batch upload all payloads
            val result = apiClient.batchUpsert(syncPayloads)

            return result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        Log.d(TAG, "Sync successful: ${response.recordCount} records synced")
                        // Mark all synced records as synced in Room
                        withContext(Dispatchers.IO) {
                            markRecordsSynced(
                                checkIns = pendingCheckIns.map { it.id },
                                symptoms = pendingSymptoms.map { it.id },
                                medications = pendingMedications.map { it.id },
                                profiles = pendingProfiles.map { it.id },
                                contacts = pendingContacts.map { it.id },
                                doseEvents = pendingDoseEvents.map { it.id }
                            )
                        }
                        Result.success()
                    } else {
                        Log.w(TAG, "Sync reported failure: ${response.message}")
                        Result.retry()
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Sync failed: ${error.message}", error)
                    if (runAttemptCount < MAX_RETRIES) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Sync worker crashed: ${e.message}", e)
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Mark synced records in the database so they aren't re-uploaded.
     */
    private suspend fun markRecordsSynced(
        checkIns: List<String>,
        symptoms: List<String>,
        medications: List<String>,
        profiles: List<String>,
        contacts: List<String>,
        doseEvents: List<String>
    ) {
        val now = System.currentTimeMillis()

        if (checkIns.isNotEmpty()) {
            dao.markCheckInsSynced(checkIns, now)
        }
        if (symptoms.isNotEmpty()) {
            dao.markSymptomsSynced(symptoms, now)
        }
        if (medications.isNotEmpty()) {
            dao.markMedicationsSynced(medications, now)
        }
        if (profiles.isNotEmpty()) {
            dao.markProfilesSynced(profiles, now)
        }
        if (contacts.isNotEmpty()) {
            dao.markEmergencyContactsSynced(contacts, now)
        }
        if (doseEvents.isNotEmpty()) {
            dao.markDoseEventsSynced(doseEvents, now)
        }
    }

    enum class SyncState {
        IDLE, SYNCING, SUCCESS, FAILED, QUEUED
    }

    companion object {
        private const val TAG = "HealthSyncWorker"
        private const val MAX_RETRIES = 3
        private const val WORK_NAME = "health_sync"

        /**
         * Enqueue a one-time sync request with exponential backoff.
         */
        fun requestSync(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<HealthSyncWorker>()
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }
}
