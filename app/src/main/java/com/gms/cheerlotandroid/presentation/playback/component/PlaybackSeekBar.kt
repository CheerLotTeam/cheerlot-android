package com.gms.cheerlotandroid.presentation.playback.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
internal fun PlaybackSeekBar(
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 드래그 중에는 실제 재생 위치(progressMs) 대신 로컬 상태를 보여줘, 재생 위치 폴링 때문에 슬라이더가 튀지 않게 합니다.
    var draggingPositionMs by remember { mutableStateOf<Long?>(null) }
    val maxMs = durationMs.coerceAtLeast(1L)
    val displayed = (draggingPositionMs ?: progressMs).coerceIn(0L, maxMs)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(maxMs) {
                fun positionToMs(x: Float): Long = ((x / size.width).coerceIn(0f, 1f) * maxMs).toLong()

                detectDragGestures(
                    onDragStart = { draggingPositionMs = positionToMs(it.x) },
                    onDrag = { change, _ -> draggingPositionMs = positionToMs(change.position.x) },
                    onDragEnd = {
                        draggingPositionMs?.let(onSeek)
                        draggingPositionMs = null
                    },
                    onDragCancel = { draggingPositionMs = null }
                )
            }
    ) {
        val trackHeight = 6.dp.toPx()
        val trackY = size.height / 2f
        val progressX = size.width * displayed.toFloat() / maxMs

        drawRoundRect(
            color = Color.White.copy(alpha = 0.25f),
            topLeft = Offset(0f, trackY - trackHeight / 2f),
            size = Size(size.width, trackHeight),
            cornerRadius = CornerRadius(trackHeight / 2f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, trackY - trackHeight / 2f),
            size = Size(progressX, trackHeight),
            cornerRadius = CornerRadius(trackHeight / 2f)
        )
        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(progressX, trackY))
    }
}
