package com.nursyai.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class HealthSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // Wire this to NursyDatabase and the cloud API once auth and backend clients exist.
        return Result.success()
    }
}
