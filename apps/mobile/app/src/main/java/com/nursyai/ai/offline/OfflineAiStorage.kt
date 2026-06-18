package com.nursyai.ai.offline

import android.content.Context
import java.io.File

class OfflineAiStorage(
    context: Context,
    baseDir: File = context.noBackupFilesDir
) {
    val directory: File = File(baseDir, "offline_ai")
    val modelFile: File = File(directory, MODEL_FILE_NAME)

    init {
        directory.mkdirs()
    }

    fun modelSizeBytes(): Long = if (modelFile.exists()) modelFile.length() else 0L

    fun hasModel(): Boolean = modelFile.exists() && modelFile.length() > 0L

    fun deleteModel() {
        if (modelFile.exists()) {
            modelFile.delete()
        }
        File(directory, TEMP_MODEL_FILE_NAME).delete()
    }

    fun tempModelFile(): File = File(directory, TEMP_MODEL_FILE_NAME)

    companion object {
        private const val MODEL_FILE_NAME = "model.bin"
        private const val TEMP_MODEL_FILE_NAME = "model.bin.download"
    }
}
