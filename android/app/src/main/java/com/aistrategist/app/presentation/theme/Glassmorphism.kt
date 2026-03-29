package com.aistrategist.app.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Custom Jetpack Compose modifier to simulate the requested Glassmorphism Aesthetic.
 * Applies a translucent gradient and a fine reflective border to mimic frosted glass.
 */
fun Modifier.glassmorphism(
    shape: Shape,
    startColor: Color = Color.White.copy(alpha = 0.15f),
    endColor: Color = Color.White.copy(alpha = 0.05f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Float = 1f
): Modifier = this.composed {
    this.clip(shape)
        .background(
            Brush.linearGradient(
                colors = listOf(startColor, endColor)
            )
        )
        .border(borderWidth.dp, borderColor, shape)
}
