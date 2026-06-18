package com.nursyai.ai.offline

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class OfflineAiDownloader(
    private val client: OkHttpClient = OkHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun fetchManifest(manifestUrl: String): OfflineAiManifest {
        val request = Request.Builder().url(manifestUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Manifest download failed with HTTP ${response.code}")
            }
            val body = response.body?.string() ?: throw IOException("Manifest response was empty.")
            return json.decodeFromString<OfflineAiManifest>(body)
        }
    }

    fun downloadModel(
        manifest: OfflineAiManifest,
        destination: File,
        onProgress: (downloadedBytes: Long, totalBytes: Long) -> Unit
    ) {
        destination.parentFile?.mkdirs()
        val request = Request.Builder().url(manifest.modelUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Model download failed with HTTP ${response.code}")
            }

            val body = response.body ?: throw IOException("Model response was empty.")
            val totalBytes = body.contentLength().takeIf { it > 0L } ?: manifest.sizeBytes
            var downloadedBytes = 0L

            body.byteStream().use { input ->
                destination.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloadedBytes += read
                        onProgress(downloadedBytes, totalBytes)
                    }
                }
            }
        }
    }
}
