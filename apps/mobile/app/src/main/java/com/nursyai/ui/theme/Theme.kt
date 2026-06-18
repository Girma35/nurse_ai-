package com.nursyai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object NursyColors {
    val ink = Color(0xFF17211D)
    val inkMuted = Color(0xFF66756D)
    val moss = Color(0xFF285947)
    val mossDark = Color(0xFF173D31)
    val mint = Color(0xFF8EDBB0)
    val mintSoft = Color(0xFFE7F6EE)
    val coral = Color(0xFFCC5F50)
    val coralSoft = Color(0xFFFFEDE8)
    val amber = Color(0xFFE4A63A)
    val amberSoft = Color(0xFFFFF5D8)
    val cloud = Color(0xFFEAF0EC)
    val background = Color(0xFFF7FAF8)
    val surface = Color.White
    val line = Color(0xFFDDE6E0)
}

private val NursyColorScheme = lightColorScheme(
    primary = NursyColors.moss,
    onPrimary = Color.White,
    secondary = NursyColors.mint,
    onSecondary = NursyColors.ink,
    tertiary = NursyColors.amber,
    background = NursyColors.background,
    onBackground = NursyColors.ink,
    surface = NursyColors.surface,
    onSurface = NursyColors.ink,
    surfaceVariant = NursyColors.cloud,
    onSurfaceVariant = NursyColors.inkMuted,
    error = NursyColors.coral,
    onError = Color.White
)

private val NursyTypography = Typography().let { base ->
    base.copy(
        headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.Bold),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold)
    )
}

private val NursyShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
)

@Composable
fun NursyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NursyColorScheme,
        typography = NursyTypography,
        shapes = NursyShapes,
        content = content
    )
}
