package com.nursyai.ai.offline

import android.content.Context
import com.nursyai.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException

class OfflineAiManager(
    context: Context,
    private val manifestUrl: String = BuildConfig.OFFLINE_AI_MANIFEST_URL,
    private val downloader: OfflineAiDownloader = OfflineAiDownloader(),
    private val summaryEngine: OfflineAiSummaryEngine = OfflineAiSummaryEngine(),
    private val storage: OfflineAiStorage = OfflineAiStorage(context.applicationContext),
    private val preferences: OfflineAiPreferences = OfflineAiPreferences(context.applicationContext),
    private val capabilityChecker: CapabilityChecker = AndroidDeviceCapabilityChecker(
        context.applicationContext,
        storage.directory
    ),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _status = MutableStateFlow(initialStatus())
    val status: StateFlow<OfflineAiStatus> = _status.asStateFlow()

    suspend fun downloadModel() {
        withContext(ioDispatcher) {
            _status.update {
                it.copy(
                    state = OfflineAiDownloadState.DOWNLOADING,
                    downloadedBytes = 0L,
                    totalBytes = 0L,
                    warning = null,
                    lastError = null
                )
            }

            runCatching {
                val manifest = downloader.fetchManifest(manifestUrl)
                val capability = capabilityChecker.check(manifest)
                if (!capability.isSupported) {
                    _status.update {
                        it.copy(
                            state = OfflineAiDownloadState.UNSUPPORTED_DEVICE,
                            recommendedRamGb = manifest.recommendedRamGb,
                            warning = capability.warning,
                            lastError = capability.blockReason
                        )
                    }
                    return@withContext
                }

                val tempFile = storage.tempModelFile()
                if (tempFile.exists()) tempFile.delete()

                _status.update {
                    it.copy(
                        state = OfflineAiDownloadState.DOWNLOADING,
                        modelVersion = manifest.modelVersion,
                        totalBytes = manifest.sizeBytes,
                        recommendedRamGb = manifest.recommendedRamGb,
                        warning = capability.warning
                    )
                }

                downloader.downloadModel(manifest, tempFile) { downloadedBytes, totalBytes ->
                    _status.update {
                        it.copy(
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes
                        )
                    }
                }

                validateDownloadedFile(manifest, tempFile)
                storage.deleteModel()
                if (!tempFile.renameTo(storage.modelFile)) {
                    throw IOException("Downloaded model could not be saved.")
                }

                preferences.modelVersion = manifest.modelVersion
                preferences.modelSizeBytes = storage.modelSizeBytes()
                _status.update {
                    it.copy(
                        state = OfflineAiDownloadState.DOWNLOADED,
                        modelVersion = manifest.modelVersion,
                        modelSizeBytes = storage.modelSizeBytes(),
                        downloadedBytes = manifest.sizeBytes,
                        totalBytes = manifest.sizeBytes,
                        warning = capability.warning,
                        lastError = null
                    )
                }
            }.onFailure { error ->
                storage.tempModelFile().delete()
                _status.update {
                    it.copy(
                        state = OfflineAiDownloadState.LOAD_FAILED,
                        lastError = error.message ?: "Offline AI download failed."
                    )
                }
            }
        }
    }

    fun deleteModel() {
        storage.deleteModel()
        preferences.clearModelMetadata()
        _status.value = OfflineAiStatus()
    }

    fun resetSetupState() {
        deleteModel()
    }

    fun simulateNoModel() {
        _status.value = OfflineAiStatus(
            state = OfflineAiDownloadState.NOT_DOWNLOADED,
            warning = "Debug simulation: no offline AI model is installed."
        )
    }

    fun simulateLowRam() {
        _status.value = OfflineAiStatus(
            state = OfflineAiDownloadState.UNSUPPORTED_DEVICE,
            warning = null,
            lastError = "Debug simulation: this phone has less than 4 GB RAM."
        )
    }

    fun simulateDownloadFailed() {
        _status.update {
            it.copy(
                state = OfflineAiDownloadState.LOAD_FAILED,
                lastError = "Debug simulation: download failed."
            )
        }
    }

    fun runTestSummary() {
        if (!_status.value.isDownloaded) {
            _status.update {
                it.copy(lastError = "Download offline AI before running a test summary.")
            }
            return
        }
        _status.update {
            it.copy(testSummary = summaryEngine.generateTestSummary(), lastError = null)
        }
    }

    private fun validateDownloadedFile(manifest: OfflineAiManifest, file: java.io.File) {
        if (!file.exists() || file.length() == 0L) {
            throw IOException("Downloaded model file is empty.")
        }
        if (file.length() != manifest.sizeBytes) {
            throw IOException("Downloaded model size did not match the manifest.")
        }
        val actualSha256 = Sha256.of(file)
        if (!actualSha256.equals(manifest.sha256, ignoreCase = true)) {
            throw IOException("Downloaded model checksum did not match the manifest.")
        }
    }

    private fun initialStatus(): OfflineAiStatus {
        return if (storage.hasModel()) {
            OfflineAiStatus(
                state = OfflineAiDownloadState.DOWNLOADED,
                modelVersion = preferences.modelVersion,
                modelSizeBytes = storage.modelSizeBytes(),
                downloadedBytes = storage.modelSizeBytes(),
                totalBytes = storage.modelSizeBytes()
            )
        } else {
            OfflineAiStatus()
        }
    }
}
