package com.gms.cheerlotandroid.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.brand.BrandColor
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

internal data class MiniPlayerUiState(
    val title: String,
    val teamInitial: String,
    val isPlaying: Boolean
)

@Composable
internal fun MiniPlayerView(
    state: MiniPlayerUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onSkipNextClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(GrayScaleColor.GrayWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniPlayerCover(
            teamInitial = state.teamInitial,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = state.title,
            modifier = Modifier.weight(1f),
            style = CheerLotTextStyle.SB7,
            color = GrayScaleColor.Gray900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "일시정지" else "재생",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onPlayClick),
                tint = GrayScaleColor.GrayBlack
            )
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = "다음 곡",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onSkipNextClick),
                tint = GrayScaleColor.GrayBlack
            )
        }
    }
}

@Composable
private fun MiniPlayerCover(
    teamInitial: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
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
            style = CheerLotTextStyle.B4,
            color = BrandColor.Seam500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MiniPlayerViewPreview() {
    CheerLotTheme {
        MiniPlayerView(
            state = MiniPlayerUiState(
                title = "김도영",
                teamInitial = "KIA",
                isPlaying = false
            )
        )
    }
}
