package com.nursyai.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * API client for syncing local health records to the cloud backend.
 * Uses DynamoDB single-table design with USER#<userId> PK pattern.
 */

@Serializable
data class SyncPayload(
    val PK: String,
    val SK: String,
    val entityType: String,
    val userId: String,
    val entityId: String,
    val payload: String,
    val updatedAt: Long,
    val deviceId: String = "mobile-android",
    val syncVersion: Int = 1
)

@Serializable
data class SyncResponse(
    val success: Boolean,
    val message: String = "",
    val recordCount: Int = 0
)

class SyncApiClient(
    private val baseUrl: String = "https://api.nursyai.com/v1"
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json".toMediaType()

    /**
     * Upsert a single sync record to the cloud.
     */
    suspend fun upsertRecord(payload: SyncPayload): Result<SyncResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val body = json.encodeToString(SyncPayload.serializer(), payload)
                    .toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("$baseUrl/sync/upsert")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: "{}"
                val syncResponse = json.decodeFromString(SyncResponse.serializer(), responseBody)

                if (response.isSuccessful) {
                    Result.success(syncResponse)
                } else {
                    Result.failure(Exception("Sync failed: ${response.code} - $responseBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Batch upsert multiple records.
     */
    suspend fun batchUpsert(records: List<SyncPayload>): Result<SyncResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val body = json.encodeToString(records)
                    .toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("$baseUrl/sync/batch")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: "{}"
                val syncResponse = json.decodeFromString(SyncResponse.serializer(), responseBody)

                if (response.isSuccessful) {
                    Result.success(syncResponse)
                } else {
                    Result.failure(Exception("Batch sync failed: ${response.code} - $responseBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch synced records for a user from the cloud.
     */
    suspend fun fetchRecords(
        userId: String,
        entityType: String? = null,
        sinceTimestamp: Long? = null
    ): Result<List<SyncPayload>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildString {
                    append("$baseUrl/sync/records/$userId")
                    entityType?.let { append("?entityType=$it") }
                }

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: "[]"
                val records = json.decodeFromString<List<SyncPayload>>(responseBody)

                Result.success(records)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    companion object {
        fun createSyncPayload(
            userId: String,
            entityType: String,
            entityId: String,
            payloadData: String,
            updatedAt: Long = System.currentTimeMillis()
        ): SyncPayload {
            return SyncPayload(
                PK = "USER#$userId",
                SK = "$entityType#$updatedAt#$entityId",
                entityType = entityType,
                userId = userId,
                entityId = entityId,
                payload = payloadData,
                updatedAt = updatedAt
            )
        }
    }
}
