package com.gms.cheerlotandroid.presentation.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.brand.BrandColor
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.semantic.CheerLotColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

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
    Surface(modifier = modifier.fillMaxSize(), color = GrayScaleColor.GrayWhite) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onClose),
                    tint = GrayScaleColor.Gray900
                )
            }

            PlaybackCover(
                teamInitial = state.teamInitial,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .aspectRatio(1f)
            )

            Text(
                text = state.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                style = CheerLotTextStyle.SB3,
                color = GrayScaleColor.Gray900,
                textAlign = TextAlign.Center
            )
            Text(
                text = state.playerName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                style = CheerLotTextStyle.M3,
                color = GrayScaleColor.Gray500,
                textAlign = TextAlign.Center
            )

            PlaybackProgressBar(
                progressMs = state.progressMs,
                durationMs = state.durationMs,
                onSeek = onSeek,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )

            PlaybackControls(
                state = state,
                onTogglePlayback = onTogglePlayback,
                onPlayNext = onPlayNext,
                onPlayPrevious = onPlayPrevious,
                onToggleShuffle = onToggleShuffle,
                onToggleRepeatOne = onToggleRepeatOne,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Text(
                text = state.lyrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 28.dp, bottom = 24.dp)
                    .verticalScroll(rememberScrollState()),
                style = CheerLotTextStyle.R2,
                color = GrayScaleColor.Gray700,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PlaybackCover(
    teamInitial: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        BrandColor.Seam100,
                        GrayScaleColor.GrayWhite,
                        BrandColor.Seam100
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = teamInitial,
            style = CheerLotTextStyle.SB1,
            color = BrandColor.Seam500
        )
    }
}

@Composable
private fun PlaybackProgressBar(
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 드래그 중에는 실제 재생 위치(progressMs) 대신 로컬 상태를 보여줘, 재생 위치 폴링 때문에 슬라이더가 튀지 않게 합니다.
    var draggingPositionMs by remember { mutableStateOf<Long?>(null) }
    val maxMs = durationMs.coerceAtLeast(1L)
    val displayedPositionMs = (draggingPositionMs ?: progressMs).coerceIn(0L, maxMs)

    Column(modifier = modifier) {
        Slider(
            value = displayedPositionMs.toFloat(),
            onValueChange = { draggingPositionMs = it.toLong() },
            onValueChangeFinished = {
                draggingPositionMs?.let(onSeek)
                draggingPositionMs = null
            },
            valueRange = 0f..maxMs.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = CheerLotColor.AppSecondary,
                activeTrackColor = CheerLotColor.AppSecondary,
                inactiveTrackColor = GrayScaleColor.Gray100
            )
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = formatDuration(displayedPositionMs),
                style = CheerLotTextStyle.R3,
                color = GrayScaleColor.Gray500,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = formatDuration(durationMs),
                style = CheerLotTextStyle.R3,
                color = GrayScaleColor.Gray500,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun PlaybackControls(
    state: PlaybackUiState,
    onTogglePlayback: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeatOne: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = "셔플",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onToggleShuffle),
                tint = if (state.isShuffleEnabled) CheerLotColor.AppSecondary else GrayScaleColor.Gray400
            )
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "이전 곡",
                modifier = Modifier
                    .size(36.dp)
                    .clickable(enabled = state.canSkipManually, onClick = onPlayPrevious),
                tint = if (state.canSkipManually) GrayScaleColor.GrayBlack else GrayScaleColor.Gray300
            )
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "일시정지" else "재생",
                modifier = Modifier
                    .size(56.dp)
                    .clickable(onClick = onTogglePlayback),
                tint = GrayScaleColor.GrayBlack
            )
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "다음 곡",
                modifier = Modifier
                    .size(36.dp)
                    .clickable(enabled = state.canSkipManually, onClick = onPlayNext),
                tint = if (state.canSkipManually) GrayScaleColor.GrayBlack else GrayScaleColor.Gray300
            )
            Icon(
                imageVector = if (state.isRepeatOneEnabled) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                contentDescription = "한 곡 반복",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onToggleRepeatOne),
                tint = if (state.isRepeatOneEnabled) CheerLotColor.AppSecondary else GrayScaleColor.Gray400
            )
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
