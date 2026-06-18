package com.nursyai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursyai.ai.HealthInsight
import com.nursyai.ai.InsightSeverity
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.SymptomEntity
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.components.EmptyStateCard
import com.nursyai.ui.components.MetricTile
import com.nursyai.ui.components.NursyCard
import com.nursyai.ui.components.NursyScreen
import com.nursyai.ui.components.PrimaryActionButton
import com.nursyai.ui.components.SecondaryActionButton
import com.nursyai.ui.components.SectionTitle
import com.nursyai.ui.components.StatusPill
import com.nursyai.ui.theme.NursyColors

@Composable
fun DashboardScreen(
    viewModel: NursyViewModel,
    onOpenTimeline: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenEmergency: () -> Unit = {},
    onOpenJournal: () -> Unit = {},
    onOpenOfflineAi: () -> Unit = {}
) {
    val checkIn by viewModel.latestCheckIn.collectAsState()
    val symptoms by viewModel.activeSymptoms.collectAsState()
    val medications by viewModel.activeMedications.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val pendingSync by viewModel.pendingSyncCounts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshPendingSyncCounts()
    }

    NursyScreen(
        eyebrow = "Offline companion",
        title = "Today",
        subtitle = "Your local health summary stays available without internet."
    ) {
        item {
            HealthScoreCard(checkIn = checkIn, symptoms = symptoms)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    label = "Sleep",
                    value = "${checkIn?.sleepHours ?: 0.0} h",
                    detail = checkIn?.sleepQuality?.replaceFirstChar { it.uppercase() } ?: "No check-in",
                    modifier = Modifier.weight(1f),
                    accent = NursyColors.mint
                )
                MetricTile(
                    label = "Water",
                    value = "${checkIn?.waterIntakeMl ?: 0} ml",
                    detail = "Goal 2000 ml",
                    modifier = Modifier.weight(1f),
                    accent = NursyColors.moss
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    label = "Energy",
                    value = "${checkIn?.energyLevel ?: 0}/10",
                    detail = checkIn?.mood?.replaceFirstChar { it.uppercase() } ?: "No mood yet",
                    modifier = Modifier.weight(1f),
                    accent = NursyColors.amber
                )
                MetricTile(
                    label = "Sync",
                    value = pendingSync.total.toString(),
                    detail = if (pendingSync.total == 0) "All local" else "Queued records",
                    modifier = Modifier.weight(1f),
                    accent = if (pendingSync.total == 0) NursyColors.mint else NursyColors.amber
                )
            }
        }

        item {
            NursyCard {
                SectionTitle(title = "Quick actions")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryActionButton(
                        text = "Journal",
                        onClick = onOpenJournal,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryActionButton(
                        text = "Timeline",
                        onClick = onOpenTimeline,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryActionButton(
                        text = "Offline AI",
                        onClick = onOpenOfflineAi,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryActionButton(
                        text = "Profile",
                        onClick = onOpenProfile,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (insights.isNotEmpty()) {
            item { SectionTitle(title = "Local insights", action = "${insights.size} active") }
            items(insights, key = { it.id }) { insight ->
                InsightCard(insight = insight)
            }
        }

        if (symptoms.isNotEmpty()) {
            item { SectionTitle(title = "Active symptoms", action = "${symptoms.size} logged") }
            items(symptoms, key = { it.id }) { symptom ->
                SymptomSummaryCard(symptom)
            }
        }

        if (medications.isNotEmpty()) {
            item { SectionTitle(title = "Medications", action = "${medications.size} active") }
            items(medications, key = { it.id }) { medication ->
                MedicationSummaryCard(medication)
            }
        }

        if (checkIn == null && symptoms.isEmpty() && medications.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Start with one record",
                    message = "Log a check-in, symptom, or medication. Nursy AI will build your offline dashboard from local data."
                )
            }
        }
    }
}

@Composable
private fun HealthScoreCard(
    checkIn: DailyCheckInEntity?,
    symptoms: List<SymptomEntity>
) {
    val sleepScore = ((checkIn?.sleepHours ?: 0.0) / 8.0).coerceAtMost(1.0) * 20
    val energyScore = ((checkIn?.energyLevel ?: 5) / 10.0) * 20
    val stressScore = kotlin.math.max(0.0, (5 - (checkIn?.stressLevel ?: 3)) / 5.0) * 15
    val hydrationScore = ((checkIn?.waterIntakeMl ?: 0) / 2000.0).coerceAtMost(1.0) * 15
    val symptomPenalty = symptoms.sumOf { it.severity * 3 }
    val score = (sleepScore + energyScore + stressScore + hydrationScore - symptomPenalty)
        .toInt()
        .coerceIn(0, 100)

    NursyCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScoreRing(score = score)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(text = scoreLabel(score))
                Text(
                    text = "Health score",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = scoreMessage(score, checkIn, symptomPenalty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ScoreRing(score: Int) {
    Box(
        modifier = Modifier.size(92.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(92.dp)) {
            val strokeWidth = 9.dp.toPx()
            drawArc(
                color = NursyColors.cloud,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = when {
                    score >= 80 -> NursyColors.mint
                    score >= 60 -> NursyColors.moss
                    score >= 40 -> NursyColors.amber
                    else -> NursyColors.coral
                },
                startAngle = -90f,
                sweepAngle = score * 3.6f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = score.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun scoreLabel(score: Int): String = when {
    score >= 80 -> "Good"
    score >= 60 -> "Stable"
    score >= 40 -> "Watch"
    else -> "Needs attention"
}

private fun scoreMessage(score: Int, checkIn: DailyCheckInEntity?, symptomPenalty: Int): String = when {
    checkIn == null -> "No check-in yet. Add today's baseline to personalize this score."
    checkIn.sleepHours < 6 -> "Low sleep is affecting today's score."
    symptomPenalty > 10 -> "Active symptoms are pulling the score down."
    score >= 80 -> "Your local metrics look steady today."
    else -> "Keep tracking today so patterns become clearer."
}

@Composable
private fun InsightCard(insight: HealthInsight) {
    val (container, accent) = when (insight.severity) {
        InsightSeverity.INFO -> NursyColors.mintSoft to NursyColors.moss
        InsightSeverity.WARNING -> NursyColors.amberSoft to NursyColors.amber
        InsightSeverity.ALERT -> NursyColors.coralSoft to NursyColors.coral
    }

    NursyCard(containerColor = container) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(9.dp),
                color = accent,
                shape = CircleShape
            ) {}
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = insight.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = insight.message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SymptomSummaryCard(symptom: SymptomEntity) {
    NursyCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeverityDot(severity = symptom.severity)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = symptom.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Severity ${symptom.severity}/5${symptom.durationHours?.let { ", ${it}h duration" } ?: ""}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MedicationSummaryCard(medication: MedicationEntity) {
    val total = medication.takenCount + medication.missedCount
    val adherence = if (total == 0) 100 else (medication.takenCount * 100 / total)

    NursyCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                color = NursyColors.mintSoft,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Rx", color = NursyColors.moss, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${medication.dose} - ${medication.frequency}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusPill(
                text = "$adherence%",
                containerColor = if (adherence >= 80) NursyColors.mintSoft else NursyColors.amberSoft
            )
        }
    }
}

@Composable
private fun SeverityDot(severity: Int) {
    Surface(
        modifier = Modifier.size(10.dp),
        color = when {
            severity >= 4 -> NursyColors.coral
            severity >= 2 -> NursyColors.amber
            else -> NursyColors.mint
        },
        shape = CircleShape
    ) {}
}
