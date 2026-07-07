package com.example.hiatoo.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.hiatoo.models.AIFaceState
import com.example.hiatoo.ui.theme.AccentCyan
import com.example.hiatoo.ui.theme.AccentPurple
import kotlinx.coroutines.delay

@Composable
fun AIFace(
    state: AIFaceState,
    modifier: Modifier = Modifier
) {
    // Blinking logic
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((2000..5000).random().toLong()) // Random blink interval
            isBlinking = true
            delay(150)
            isBlinking = false
        }
    }

    val eyeScaleY by animateFloatAsState(
        targetValue = if (isBlinking) 0.1f else 1f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "blink_animation"
    )

    // Eye offset and mouth curve based on state
    val targetEyeOffset = when (state) {
        AIFaceState.IDLE -> 0f
        AIFaceState.LISTENING -> 0f
        AIFaceState.THINKING -> -20f // Look up
        AIFaceState.SPEAKING -> 0f
    }

    val eyeOffset by animateFloatAsState(
        targetValue = targetEyeOffset,
        animationSpec = tween(durationMillis = 500),
        label = "eye_offset"
    )

    val targetMouthCurve = when (state) {
        AIFaceState.IDLE -> 40f     // Smile
        AIFaceState.LISTENING -> 20f  // Neutral/small smile
        AIFaceState.THINKING -> 0f    // Straight line
        AIFaceState.SPEAKING -> 60f   // Wide open/talking
    }

    val mouthCurve by animateFloatAsState(
        targetValue = targetMouthCurve,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "mouth_curve"
    )

    // Breathing/Glowing effect
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    
    val glowDuration = when(state) {
        AIFaceState.IDLE -> 2000
        AIFaceState.LISTENING -> 800
        AIFaceState.THINKING -> 300
        AIFaceState.SPEAKING -> 150
    }

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = glowDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Ripple effect for listening
    val rippleRadius by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_radius"
    )

    val faceColor = when (state) {
        AIFaceState.IDLE -> AccentCyan
        AIFaceState.LISTENING -> AccentPurple
        AIFaceState.THINKING -> Color.White
        AIFaceState.SPEAKING -> AccentCyan
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val eyeWidth = 40.dp.toPx()
        val eyeHeight = 60.dp.toPx()
        val eyeSpacing = 120.dp.toPx()

        // Draw Ripple if listening
        if (state == AIFaceState.LISTENING) {
            drawCircle(
                color = AccentPurple.copy(alpha = 1f - (rippleRadius - 100f) / 80f),
                radius = rippleRadius.dp.toPx(),
                center = Offset(centerX, centerY),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Draw Eyes
        val leftEyeCenter = Offset(centerX - eyeSpacing / 2, centerY - 40.dp.toPx() + eyeOffset)
        val rightEyeCenter = Offset(centerX + eyeSpacing / 2, centerY - 40.dp.toPx() + eyeOffset)

        val eyeSize = Size(eyeWidth, eyeHeight * eyeScaleY)
        val eyeRadius = CornerRadius(20.dp.toPx())

        drawRoundRect(
            color = faceColor.copy(alpha = glowAlpha),
            topLeft = Offset(leftEyeCenter.x - eyeWidth / 2, leftEyeCenter.y - (eyeHeight * eyeScaleY) / 2),
            size = eyeSize,
            cornerRadius = eyeRadius
        )

        drawRoundRect(
            color = faceColor.copy(alpha = glowAlpha),
            topLeft = Offset(rightEyeCenter.x - eyeWidth / 2, rightEyeCenter.y - (eyeHeight * eyeScaleY) / 2),
            size = eyeSize,
            cornerRadius = eyeRadius
        )

        // Draw Mouth
        val mouthY = centerY + 80.dp.toPx()
        val mouthWidth = 100.dp.toPx()
        
        // Dynamic mouth flutter when speaking
        val dynamicMouthCurve = if (state == AIFaceState.SPEAKING) {
            mouthCurve * glowAlpha 
        } else {
            mouthCurve
        }
        
        val mouthPath = Path().apply {
            moveTo(centerX - mouthWidth / 2, mouthY)
            quadraticBezierTo(
                centerX, mouthY + dynamicMouthCurve,
                centerX + mouthWidth / 2, mouthY
            )
        }

        drawPath(
            path = mouthPath,
            color = faceColor.copy(alpha = glowAlpha),
            style = Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}
