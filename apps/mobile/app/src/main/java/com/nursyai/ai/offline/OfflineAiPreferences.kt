package com.nursyai.ai.offline

import android.content.Context

class OfflineAiPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("offline_ai", Context.MODE_PRIVATE)

    var modelVersion: String?
        get() = prefs.getString(KEY_MODEL_VERSION, null)
        set(value) = prefs.edit().putString(KEY_MODEL_VERSION, value).apply()

    var modelSizeBytes: Long
        get() = prefs.getLong(KEY_MODEL_SIZE_BYTES, 0L)
        set(value) = prefs.edit().putLong(KEY_MODEL_SIZE_BYTES, value).apply()

    fun clearModelMetadata() {
        prefs.edit()
            .remove(KEY_MODEL_VERSION)
            .remove(KEY_MODEL_SIZE_BYTES)
            .apply()
    }

    private companion object {
        const val KEY_MODEL_VERSION = "model_version"
        const val KEY_MODEL_SIZE_BYTES = "model_size_bytes"
    }
}
