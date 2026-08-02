package com.gms.cheerlotandroid.presentation.playback.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

// Metal 쉐이더로 만든 iOS의 움직이는 blob 배경을 정확히 포팅하는 대신, 원형 그라데이션 3개를
// 천천히 떠다니게 그려서 근사합니다 (플랜 문서의 디자인 결정 참고).
@Composable
internal fun PlaybackBackground(primaryColor: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "playbackBackground")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(26_000, easing = LinearEasing), RepeatMode.Restart),
        label = "blobPhase"
    )

    Canvas(modifier.fillMaxSize()) {
        drawRect(primaryColor)

        val radius = size.maxDimension * 0.62f
        listOf(0f, 2.1f, 4.2f).forEachIndexed { index, shift ->
            val center = Offset(
                x = size.width * (0.5f + 0.26f * cos(phase + shift)),
                y = size.height * (0.5f + 0.28f * sin(phase * (if (index == 1) 0.8f else 1.1f) + shift))
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent), center, radius),
                radius = radius,
                center = center
            )
        }
    }
}
