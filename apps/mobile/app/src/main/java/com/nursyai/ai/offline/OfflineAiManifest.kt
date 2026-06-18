package com.nursyai.ai.offline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfflineAiManifest(
    @SerialName("modelVersion")
    val modelVersion: String,
    @SerialName("modelUrl")
    val modelUrl: String,
    @SerialName("sizeBytes")
    val sizeBytes: Long,
    @SerialName("sha256")
    val sha256: String,
    @SerialName("minRamGb")
    val minRamGb: Int = 4,
    @SerialName("recommendedRamGb")
    val recommendedRamGb: Int = 6,
    @SerialName("minFreeStorageBytes")
    val minFreeStorageBytes: Long = sizeBytes,
    @SerialName("displayName")
    val displayName: String = "Offline AI"
)
