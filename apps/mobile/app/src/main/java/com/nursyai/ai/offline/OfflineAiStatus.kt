package com.nursyai.ai.offline

enum class OfflineAiDownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    LOAD_FAILED,
    UNSUPPORTED_DEVICE
}

data class OfflineAiStatus(
    val state: OfflineAiDownloadState = OfflineAiDownloadState.NOT_DOWNLOADED,
    val modelVersion: String? = null,
    val modelSizeBytes: Long = 0L,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val recommendedRamGb: Int = 6,
    val warning: String? = null,
    val lastError: String? = null,
    val testSummary: String? = null
) {
    val progress: Float
        get() = if (totalBytes > 0L) downloadedBytes.toFloat() / totalBytes else 0f

    val isDownloaded: Boolean
        get() = state == OfflineAiDownloadState.DOWNLOADED
}
