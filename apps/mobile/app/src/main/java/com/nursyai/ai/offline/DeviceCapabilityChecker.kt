package com.nursyai.ai.offline

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.StatFs
import java.io.File
import kotlin.math.ceil

data class DeviceCapability(
    val isSupported: Boolean,
    val ramGb: Int,
    val freeStorageBytes: Long,
    val isWifiConnected: Boolean,
    val warning: String? = null,
    val blockReason: String? = null
)

interface CapabilityChecker {
    fun check(manifest: OfflineAiManifest): DeviceCapability
}

class AndroidDeviceCapabilityChecker(
    private val context: Context,
    private val storageDir: File
) : CapabilityChecker {

    override fun check(manifest: OfflineAiManifest): DeviceCapability {
        val ramGb = totalRamGb()
        val freeStorageBytes = availableBytes(storageDir)
        val isWifiConnected = isWifiConnected()

        val blockReason = when {
            ramGb < manifest.minRamGb ->
                "Offline AI needs at least ${manifest.minRamGb} GB RAM on this device."
            freeStorageBytes < manifest.minFreeStorageBytes ->
                "Free up storage before downloading the offline AI pack."
            !isWifiConnected ->
                "Connect to Wi-Fi before downloading the offline AI pack."
            else -> null
        }

        val warning = when {
            blockReason != null -> null
            ramGb < manifest.recommendedRamGb ->
                "This phone can try the small offline AI pack, but summaries may be slow and use more battery."
            else -> null
        }

        return DeviceCapability(
            isSupported = blockReason == null,
            ramGb = ramGb,
            freeStorageBytes = freeStorageBytes,
            isWifiConnected = isWifiConnected,
            warning = warning,
            blockReason = blockReason
        )
    }

    private fun totalRamGb(): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return ceil(memoryInfo.totalMem / BYTES_PER_GB.toDouble()).toInt()
    }

    private fun availableBytes(dir: File): Long {
        dir.mkdirs()
        val stat = StatFs(dir.absolutePath)
        return stat.availableBytes
    }

    private fun isWifiConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private companion object {
        const val BYTES_PER_GB = 1024L * 1024L * 1024L
    }
}
