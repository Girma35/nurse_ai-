package com.nursyai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NursyApp()
        }
    }
}

@Composable
fun NursyApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7FAF7)),
            color = Color(0xFFF7FAF7)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Nursy AI",
                    color = Color(0xFF285947),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Today",
                    color = Color(0xFF16211C),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
                HealthScoreCard()
                QuickMetricRow()
                EmergencyCard()
            }
        }
    }
}

@Composable
private fun HealthScoreCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Health score",
                color = Color(0xFF285947),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "78",
                color = Color(0xFF16211C),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Stable. Watch sleep and fatigue patterns.",
                color = Color(0xAA16211C),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun QuickMetricRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickMetric("Sleep", "6.5 h", Modifier.weight(1f))
        QuickMetric("Water", "1450 ml", Modifier.weight(1f))
    }
}

@Composable
private fun QuickMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, color = Color(0xAA16211C), fontSize = 13.sp)
            Text(
                text = value,
                color = Color(0xFF16211C),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmergencyCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF7F4),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    color = Color(0xFFEF7B63),
                    shape = RoundedCornerShape(50)
                ) {}
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Emergency card",
                    color = Color(0xFFEF7B63),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Blood type O+",
                color = Color(0xFF16211C),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Allergy: Penicillin", color = Color(0xAA16211C), fontSize = 15.sp)
        }
    }
}
