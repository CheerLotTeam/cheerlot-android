package com.gms.cheerlotandroid.presentation.playback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.presentation.playback.component.PlaybackBackground
import com.gms.cheerlotandroid.presentation.playback.component.PlaybackSeekBar
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
internal fun PlaybackScreen(
    state: PlaybackUiState,
    onClose: () -> Unit,
    onTogglePlayback: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeatOne: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dragOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val closeThresholdPx = with(LocalDensity.current) { 40.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, dragOffset.value.roundToInt()) }
    ) {
        PlaybackBackground(state.primaryColor)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f))
        )

        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            DragHandle(
                dragOffset = dragOffset,
                closeThresholdPx = closeThresholdPx,
                onClose = onClose,
                onDragCancelled = { scope.launch { dragOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) } },
                onDragged = { amount -> scope.launch { dragOffset.snapTo((dragOffset.value + amount).coerceAtLeast(0f)) } }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.playerName,
                    style = CheerLotTextStyle.B3,
                    color = GrayScaleColor.GrayWhite,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = state.title,
                    style = CheerLotTextStyle.SB8,
                    color = GrayScaleColor.GrayWhite.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            Lyrics(
                lyrics = state.lyrics,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 40.dp, bottom = 40.dp)
            )

            Footer(
                state = state,
                onTogglePlayback = onTogglePlayback,
                onSeek = onSeek,
                onPlayNext = onPlayNext,
                onPlayPrevious = onPlayPrevious,
                onToggleShuffle = onToggleShuffle,
                onToggleRepeatOne = onToggleRepeatOne
            )
        }
    }
}

@Composable
private fun DragHandle(
    dragOffset: Animatable<Float, *>,
    closeThresholdPx: Float,
    onClose: () -> Unit,
    onDragCancelled: () -> Unit,
    onDragged: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        onDragged(amount)
                    },
                    onDragEnd = {
                        if (dragOffset.value >= closeThresholdPx) onClose() else onDragCancelled()
                    }
                )
            }
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 60.dp, height = 5.dp)
                .background(GrayScaleColor.GrayWhite.copy(alpha = 0.5f), RoundedCornerShape(50))
        )
    }
}

@Composable
private fun Lyrics(lyrics: String, modifier: Modifier = Modifier) {
    val scroll = rememberScrollState()
    val isFadeVisible = scroll.value < scroll.maxValue

    Text(
        text = lyrics,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                if (isFadeVisible) {
                    drawRect(
                        brush = Brush.verticalGradient(0f to Color.Black, 0.85f to Color.Black, 1f to Color.Transparent),
                        blendMode = BlendMode.DstIn
                    )
                }
            },
        style = CheerLotTextStyle.B1_1,
        color = GrayScaleColor.GrayWhite,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun Footer(
    state: PlaybackUiState,
    onTogglePlayback: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeatOne: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 50.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            PlaybackSeekBar(progressMs = state.progressMs, durationMs = state.durationMs, onSeek = onSeek)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatDuration(state.progressMs), style = CheerLotTextStyle.M5, color = GrayScaleColor.GrayWhite)
                Text(
                    text = "-${formatDuration((state.durationMs - state.progressMs).coerceAtLeast(0))}",
                    style = CheerLotTextStyle.M5,
                    color = GrayScaleColor.GrayWhite.copy(alpha = 0.5f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 셔플/반복은 iOS와 동일하게 "켜져있는지"가 밝기(opacity)만 바꾸고 항상 클릭 가능합니다.
            // 이전/다음곡만 canSkipManually에 따라 밝기와 클릭 가능 여부가 함께 바뀝니다(iOS의 .disabled(!canSkipManually)).
            PlaybackControl(
                icon = Icons.Filled.Shuffle,
                description = "셔플",
                iconSize = 21.dp,
                isHighlighted = state.isShuffleEnabled,
                onClick = onToggleShuffle
            )
            PlaybackControl(
                icon = Icons.Filled.SkipPrevious,
                description = "이전 곡",
                iconSize = 24.dp,
                isHighlighted = state.canSkipManually,
                isClickable = state.canSkipManually,
                onClick = onPlayPrevious
            )
            PlaybackControl(
                icon = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                description = "재생",
                iconSize = 32.dp,
                isHighlighted = true,
                onClick = onTogglePlayback
            )
            PlaybackControl(
                icon = Icons.Filled.SkipNext,
                description = "다음 곡",
                iconSize = 24.dp,
                isHighlighted = state.canSkipManually,
                isClickable = state.canSkipManually,
                onClick = onPlayNext
            )
            PlaybackControl(
                icon = Icons.Filled.Repeat,
                description = "반복",
                iconSize = 21.dp,
                isHighlighted = state.isRepeatOneEnabled,
                onClick = onToggleRepeatOne
            )
        }
    }
}

@Composable
private fun PlaybackControl(
    icon: ImageVector,
    description: String,
    iconSize: Dp,
    isHighlighted: Boolean,
    isClickable: Boolean = true,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                color = if (pressed) GrayScaleColor.GrayWhite.copy(alpha = 0.6f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = isClickable,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(iconSize),
            tint = GrayScaleColor.GrayWhite.copy(alpha = if (isHighlighted) 1f else 0.3f)
        )
    }
}

private fun formatDuration(ms: Long): String = "%d:%02d".format(ms / 60_000, (ms / 1_000) % 60)
