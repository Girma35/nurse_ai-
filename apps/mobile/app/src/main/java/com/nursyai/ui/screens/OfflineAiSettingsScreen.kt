package com.nursyai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursyai.BuildConfig
import com.nursyai.ai.offline.OfflineAiDownloadState
import com.nursyai.ai.offline.OfflineAiStatus
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SecondaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors

@Composable
fun OfflineAiSettingsScreen(viewModel: NursyViewModel) {
    val status by viewModel.offlineAiStatus.collectAsState()

    NursyScreen(
        eyebrow = "Optional offline pack",
        title = "Offline AI",
        subtitle = "Download a private on-device AI pack for summaries. The base app still works without it."
    ) {
        item {
            OfflineAiStatusCard(status = status)
        }

        item {
            OfflineAiActionsCard(
                status = status,
                onDownload = viewModel::downloadOfflineAiModel,
                onDelete = viewModel::deleteOfflineAiModel,
                onRunTestSummary = viewModel::runOfflineAiTestSummary
            )
        }

        if (status.testSummary != null) {
            item {
                NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                    SectionTitle(title = "Test summary")
                    Text(
                        text = status.testSummary.orEmpty(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "How it works")
                SettingDetailRow(label = "Base app", value = "No AI model included")
                SettingDetailRow(label = "Download source", value = "Manifest URL to model file")
                SettingDetailRow(label = "Storage", value = "Private app storage, not backed up")
                SettingDetailRow(label = "Use", value = "Summaries and doctor-visit notes only")
            }
        }

        if (BuildConfig.DEBUG) {
            item {
                DebugOfflineAiCard(
                    onClearModel = viewModel::deleteOfflineAiModel,
                    onSimulateNoModel = viewModel::simulateOfflineAiNoModel,
                    onSimulateLowRam = viewModel::simulateOfflineAiLowRam,
                    onSimulateDownloadFailed = viewModel::simulateOfflineAiDownloadFailed,
                    onReset = viewModel::resetOfflineAiSetupState
                )
            }
        }
    }
}

@Composable
private fun OfflineAiStatusCard(status: OfflineAiStatus) {
    NursyCard(containerColor = statusContainerColor(status.state)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = statusTitle(status.state),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = statusDescription(status),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusPill(
                text = status.state.displayLabel(),
                containerColor = statusPillColor(status.state),
                contentColor = statusPillTextColor(status.state)
            )
        }

        if (status.state == OfflineAiDownloadState.DOWNLOADING) {
            LinearProgressIndicator(
                progress = { status.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "${status.downloadedBytes.formatBytes()} of ${status.totalBytes.formatBytes()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (status.warning != null) {
            Text(
                text = status.warning,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (status.lastError != null) {
            Text(
                text = status.lastError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun OfflineAiActionsCard(
    status: OfflineAiStatus,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onRunTestSummary: () -> Unit
) {
    val isDownloading = status.state == OfflineAiDownloadState.DOWNLOADING

    NursyCard {
        SectionTitle(title = "Model controls")
        PrimaryActionButton(
            text = if (status.isDownloaded) "Re-download AI Model" else "Download AI Model",
            onClick = onDownload,
            enabled = !isDownloading
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryActionButton(
                text = "Delete",
                onClick = onDelete,
                enabled = status.isDownloaded && !isDownloading,
                modifier = Modifier.weight(1f)
            )
            SecondaryActionButton(
                text = "Run Test",
                onClick = onRunTestSummary,
                enabled = status.isDownloaded && !isDownloading,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = "Download over Wi-Fi. The model is stored privately on this phone and can be deleted anytime.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DebugOfflineAiCard(
    onClearModel: () -> Unit,
    onSimulateNoModel: () -> Unit,
    onSimulateLowRam: () -> Unit,
    onSimulateDownloadFailed: () -> Unit,
    onReset: () -> Unit
) {
    NursyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        SectionTitle(title = "Developer AI tools", action = "Debug only")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryActionButton(
                text = "Clear",
                onClick = onClearModel,
                modifier = Modifier.weight(1f)
            )
            SecondaryActionButton(
                text = "No Model",
                onClick = onSimulateNoModel,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryActionButton(
                text = "Low RAM",
                onClick = onSimulateLowRam,
                modifier = Modifier.weight(1f)
            )
            SecondaryActionButton(
                text = "Failed",
                onClick = onSimulateDownloadFailed,
                modifier = Modifier.weight(1f)
            )
        }
        SecondaryActionButton(text = "Reset AI Setup State", onClick = onReset)
    }
}

@Composable
private fun SettingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.8f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.2f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun statusContainerColor(state: OfflineAiDownloadState) = when (state) {
    OfflineAiDownloadState.DOWNLOADED -> NursyColors.mintSoft
    OfflineAiDownloadState.LOAD_FAILED,
    OfflineAiDownloadState.UNSUPPORTED_DEVICE -> NursyColors.coralSoft
    OfflineAiDownloadState.DOWNLOADING -> NursyColors.cloud
    OfflineAiDownloadState.NOT_DOWNLOADED -> MaterialTheme.colorScheme.surface
}

@Composable
private fun statusPillColor(state: OfflineAiDownloadState) = when (state) {
    OfflineAiDownloadState.DOWNLOADED -> NursyColors.mintSoft
    OfflineAiDownloadState.LOAD_FAILED,
    OfflineAiDownloadState.UNSUPPORTED_DEVICE -> NursyColors.coralSoft
    OfflineAiDownloadState.DOWNLOADING -> NursyColors.amberSoft
    OfflineAiDownloadState.NOT_DOWNLOADED -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun statusPillTextColor(state: OfflineAiDownloadState) = when (state) {
    OfflineAiDownloadState.DOWNLOADED -> NursyColors.mossDark
    OfflineAiDownloadState.LOAD_FAILED,
    OfflineAiDownloadState.UNSUPPORTED_DEVICE -> NursyColors.coral
    OfflineAiDownloadState.DOWNLOADING -> NursyColors.ink
    OfflineAiDownloadState.NOT_DOWNLOADED -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun statusTitle(state: OfflineAiDownloadState) = when (state) {
    OfflineAiDownloadState.NOT_DOWNLOADED -> "Offline AI not installed"
    OfflineAiDownloadState.DOWNLOADING -> "Downloading offline AI"
    OfflineAiDownloadState.DOWNLOADED -> "Offline AI ready"
    OfflineAiDownloadState.LOAD_FAILED -> "Offline AI needs attention"
    OfflineAiDownloadState.UNSUPPORTED_DEVICE -> "Device not ready"
}

private fun statusDescription(status: OfflineAiStatus) = when (status.state) {
    OfflineAiDownloadState.NOT_DOWNLOADED ->
        "Download the optional AI pack to generate local summaries without cloud AI."
    OfflineAiDownloadState.DOWNLOADING ->
        "Keep the app open while the model downloads and verifies."
    OfflineAiDownloadState.DOWNLOADED ->
        "Version ${status.modelVersion ?: "installed"} uses ${status.modelSizeBytes.formatBytes()} on this phone."
    OfflineAiDownloadState.LOAD_FAILED ->
        "The model was not installed. You can try again on Wi-Fi."
    OfflineAiDownloadState.UNSUPPORTED_DEVICE ->
        "This device does not currently meet the offline AI requirements."
}

private fun OfflineAiDownloadState.displayLabel() = when (this) {
    OfflineAiDownloadState.NOT_DOWNLOADED -> "Not installed"
    OfflineAiDownloadState.DOWNLOADING -> "Downloading"
    OfflineAiDownloadState.DOWNLOADED -> "Ready"
    OfflineAiDownloadState.LOAD_FAILED -> "Failed"
    OfflineAiDownloadState.UNSUPPORTED_DEVICE -> "Unsupported"
}

private fun Long.formatBytes(): String {
    if (this <= 0L) return "0 MB"
    val mb = this / (1024.0 * 1024.0)
    return if (mb >= 1024.0) {
        "%.1f GB".format(mb / 1024.0)
    } else {
        "%.0f MB".format(mb)
    }
}
