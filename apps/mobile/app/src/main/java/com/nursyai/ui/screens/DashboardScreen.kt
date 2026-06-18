package com.nursyai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.theme.NursyColors

@Composable
fun DashboardScreen(viewModel: NursyViewModel) {
    val checkIn by viewModel.latestCheckIn.collectAsState()
    val symptoms by viewModel.activeSymptoms.collectAsState()
    val medications by viewModel.activeMedications.collectAsState()
    val insights by viewModel.insights.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NursyColors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nursy AI",
                color = NursyColors.moss,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Today",
                color = NursyColors.ink,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Health Score Card
        item {
            HealthScoreCard(
                checkIn = checkIn,
                symptoms = symptoms,
                medications = medications
            )
        }

        // Quick Metrics
        item {
            QuickMetricRow(
                sleepHours = checkIn?.sleepHours ?: 0.0,
                waterIntake = checkIn?.waterIntakeMl ?: 0
            )
        }

        // Insights
        if (insights.isNotEmpty()) {
            item {
                Text(
                    text = "Insights",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(insights) { insight ->
                InsightCard(insight)
            }
        }

        // Active Symptoms
        if (symptoms.isNotEmpty()) {
            item {
                Text(
                    text = "Active Symptoms",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(symptoms) { symptom ->
                SymptomSummaryCard(
                    name = symptom.name,
                    severity = symptom.severity,
                    duration = symptom.durationHours
                )
            }
        }

        // Active Medications
        if (medications.isNotEmpty()) {
            item {
                Text(
                    text = "Active Medications",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(medications) { medication ->
                MedicationSummaryCard(
                    name = medication.name,
                    dose = medication.dose,
                    frequency = medication.frequency
                )
            }
        }

        // Empty state
        if (checkIn == null && symptoms.isEmpty() && medications.isEmpty()) {
            item {
                EmptyDashboardCard()
            }
        }
    }
}

@Composable
private fun HealthScoreCard(
    checkIn: com.nursyai.data.local.entity.DailyCheckInEntity?,
    symptoms: List<com.nursyai.data.local.entity.SymptomEntity>,
    medications: List<com.nursyai.data.local.entity.MedicationEntity>
) {
    // Simple local score calculation
    val sleepScore = ((checkIn?.sleepHours ?: 0.0) / 8.0).coerceAtMost(1.0) * 20
    val energyScore = ((checkIn?.energyLevel ?: 5) / 10.0) * 20
    val stressScore = kotlin.math.max(0, (5 - (checkIn?.stressLevel ?: 3)) / 5.0) * 15
    val hydrationScore = ((checkIn?.waterIntakeMl ?: 0) / 2000.0).coerceAtMost(1.0) * 15
    val symptomPenalty = symptoms.sumOf { it.severity * 3 }
    val totalScore = kotlin.math.max(0, kotlin.math.min(100,
        (sleepScore + energyScore + stressScore + hydrationScore - symptomPenalty).toInt()
    ))

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NursyColors.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score ring
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(80.dp)
            ) {
                val angle = totalScore * 3.6f
                androidx.compose.foundation.Canvas(modifier = Modifier.size(80.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    drawArc(
                        color = NursyColors.mint,
                        startAngle = -90f,
                        sweepAngle = angle,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                    )
                    drawArc(
                        color = NursyColors.cloud,
                        startAngle = -90f + angle,
                        sweepAngle = 360f - angle,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                    )
                }
                Text(
                    text = "$totalScore",
                    color = NursyColors.ink,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = "Health score",
                    color = NursyColors.moss,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when {
                        totalScore >= 80 -> "Good"
                        totalScore >= 60 -> "Stable"
                        totalScore >= 40 -> "Concerning"
                        else -> "Needs attention"
                    },
                    color = NursyColors.ink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                val insight = when {
                    checkIn?.sleepHours != null && checkIn.sleepHours < 6 -> "Low sleep detected"
                    symptomPenalty > 10 -> "Multiple symptoms active"
                    else -> "All metrics stable"
                }
                Text(
                    text = insight,
                    color = NursyColors.inkMuted,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun QuickMetricRow(sleepHours: Double, waterIntake: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickMetricCard(label = "Sleep", value = "${sleepHours} h", Modifier.weight(1f))
        QuickMetricCard(label = "Water", value = "$waterIntake ml", Modifier.weight(1f))
        QuickMetricCard(
            label = "Energy",
            value = "—",
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        color = NursyColors.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, color = NursyColors.inkMuted, fontSize = 13.sp)
            Text(
                text = value,
                color = NursyColors.ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun InsightCard(insight: String) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFBF0),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier.size(8.dp),
                color = NursyColors.amber,
                shape = RoundedCornerShape(50)
            ) {}
            Text(
                text = insight,
                color = NursyColors.ink,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
private fun SymptomSummaryCard(name: String, severity: Int, duration: Int?) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NursyColors.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier.size(8.dp),
                color = when {
                    severity >= 4 -> NursyColors.coral
                    severity >= 2 -> NursyColors.amber
                    else -> NursyColors.mint
                },
                shape = RoundedCornerShape(50)
            ) {}
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = name, color = NursyColors.ink, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Severity: $severity/5${duration?.let { " · ${it}h duration" } ?: ""}",
                    color = NursyColors.inkMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MedicationSummaryCard(name: String, dose: String, frequency: String) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NursyColors.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier.size(36.dp),
                color = NursyColors.cloud,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💊",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = name, color = NursyColors.ink, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "$dose · $frequency",
                    color = NursyColors.inkMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyDashboardCard() {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NursyColors.cloud,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Nursy AI",
                color = NursyColors.ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start by logging your first check-in, symptom, or medication. All data stays private on your device.",
                color = NursyColors.inkMuted,
                fontSize = 14.sp
            )
        }
    }
}
