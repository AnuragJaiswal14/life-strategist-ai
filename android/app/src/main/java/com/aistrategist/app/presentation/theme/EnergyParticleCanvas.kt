package com.aistrategist.app.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var radius: Float,
    var alpha: Float,
    val color: Color
)

@Composable
fun EnergyParticleCanvas(
    modifier: Modifier = Modifier,
    energyLevel: Int = 5 // 1-10
) {
    // Generate particles based on energy state
    // High energy (8-10): Smooth Indigo/Green orbits
    // Low energy (1-4): Fast, leaking Orange/Red particles
    val particles = remember(energyLevel) {
        val count = if (energyLevel > 6) 40 else 60
        List(count) {
            val isBurnout = energyLevel <= 4
            val baseColor = if (isBurnout) {
                listOf(Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFF44336)).random()
            } else {
                listOf(Color(0xFF3F51B5), Color(0xFF4CAF50), Color(0xFF00BCD4)).random()
            }
            
            Particle(
                x = Random.nextFloat(), // Percentage of width
                y = Random.nextFloat(), // Percentage of height
                velocityX = if (isBurnout) Random.nextFloat() * 0.01f - 0.005f else Random.nextFloat() * 0.002f - 0.001f,
                velocityY = if (isBurnout) -Random.nextFloat() * 0.02f else Random.nextFloat() * 0.002f - 0.001f, // Burnout flies up and leaks
                radius = Random.nextFloat() * 8f + 4f,
                alpha = Random.nextFloat() * 0.5f + 0.1f,
                color = baseColor
            )
        }
    }

    val timeMillis = remember { mutableStateOf(0L) }

    LaunchedEffect(energyLevel) {
        while (true) {
            withFrameMillis { frameTime ->
                timeMillis.value = frameTime
            }
        }
    }

    Canvas(modifier = modifier) {
        // Just reading the state triggers recomposition on frame rate
        val currentMillis = timeMillis.value 

        clipRect {
            val w = size.width
            val h = size.height

            particles.forEach { p ->
                // Apply velocity
                p.x += p.velocityX
                p.y += p.velocityY

                // Wrap around edges
                if (p.x < 0f) p.x = 1f
                if (p.x > 1f) p.x = 0f
                if (p.y < 0f) p.y = 1f
                if (p.y > 1f) p.y = 0f

                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = androidx.compose.ui.geometry.Offset(p.x * w, p.y * h)
                )
            }
        }
    }
}
